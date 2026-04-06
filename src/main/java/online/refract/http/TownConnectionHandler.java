package online.refract.http;

import online.refract.Sttk;
import online.refract.game.state.Enums.TownConnectionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TownConnectionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sttk.MOD_ID);
    private static final long RETRY_DELAY_MS = 1000;
    private static final long MAX_RETRY_DURATION_MS = 3000;

    public sealed interface ConnectionEvent {
        record StatusChanged(TownConnectionStatus status)     implements ConnectionEvent {}
        record DataReceived(String jsonData, String townName) implements ConnectionEvent {}
    }

    private final String sseEndpoint;
    private volatile Consumer<ConnectionEvent> listener = event -> {};

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "sse-scheduler");
                t.setDaemon(true);
                return t;
            });

    private final AtomicReference<CompletableFuture<?>> activeFuture = new AtomicReference<>();

    private volatile String currentTownName = "";
    private volatile long firstFailureTime = -1;
    private volatile boolean running = false;

    public TownConnectionHandler(String sseEndpoint) {
        this.sseEndpoint = sseEndpoint;
    }

    public void setConnectionListener(Consumer<ConnectionEvent> listener) {
        this.listener = listener != null ? listener : event -> {};
    }

    public void connect(String townName) {
        disconnect(); 
        currentTownName = townName;
        firstFailureTime = -1;
        running = true;
        emit(new ConnectionEvent.StatusChanged(TownConnectionStatus.CONNECTING));
        attemptConnect();
    }

    public void disconnect() {
        running = false;
        currentTownName = "";
        CompletableFuture<?> f = activeFuture.getAndSet(null);
        if (f != null) f.cancel(true);
        emit(new ConnectionEvent.StatusChanged(TownConnectionStatus.DISCONNECTED));
    }

    private void attemptConnect() {
        if (!running) return;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(sseEndpoint))     //  TODO: + "/" + currentTownName
                .header("Accept", "text/event-stream")
                .header("Cache-Control", "no-cache")
                .GET()
                .build();

        CompletableFuture<Void> future = httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(this::handleResponse)
                .exceptionally(this::handleError);

        activeFuture.set(future);
    }

    private void handleResponse(HttpResponse<Stream<String>> response) {
        if (!running) return;

        if (response.statusCode() != 200) {
            LOGGER.error("Failed to connect, status: {}", response.statusCode());
            scheduleRetryOrGiveUp();
            return;
        }

        firstFailureTime = -1;
        emit(new ConnectionEvent.StatusChanged(TownConnectionStatus.CONNECTED));

        try (Stream<String> body = response.body()) {
            body.takeWhile(line -> running && !Thread.currentThread().isInterrupted())
                .filter(line -> line.startsWith("data:"))
                .map(line -> line.substring("data:".length()).trim())
                .forEach(json -> emit(new ConnectionEvent.DataReceived(json, currentTownName)));
        }

        if (running) {
            LOGGER.warn("SSE stream ended unexpectedly");
            scheduleRetryOrGiveUp();
        }
    }

    private Void handleError(Throwable ex) {
        if (!running) return null; 
        LOGGER.error("SSE connection error: {}", ex.getMessage());
        scheduleRetryOrGiveUp();
        return null;
    }

    private void scheduleRetryOrGiveUp() {
        if (!running) return;

        if (firstFailureTime == -1) firstFailureTime = System.currentTimeMillis();

        if (System.currentTimeMillis() - firstFailureTime >= MAX_RETRY_DURATION_MS) {
            LOGGER.error("Could not reconnect after {}ms, giving up", MAX_RETRY_DURATION_MS);
            emit(new ConnectionEvent.StatusChanged(TownConnectionStatus.CONNECTION_LOST));
            running = false;
            return;
        }

        emit(new ConnectionEvent.StatusChanged(TownConnectionStatus.CONNECTION_LOST));
        LOGGER.info("Reconnecting in {}ms...", RETRY_DELAY_MS);
        scheduler.schedule(this::attemptConnect, RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    private void emit(ConnectionEvent event) {
        listener.accept(event);
    }
}