package online.refract.http;

import online.refract.Sttk;
import online.refract.game.server.ClocktowerServerStateManager;
import online.refract.game.state.ClocktowerState;
import online.refract.game.state.Enums.TownConnectionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class TownConnectionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sttk.MOD_ID);
    private static final long RETRY_DELAY_MS = 1000;
    private static final long MAX_RETRY_DURATION_MS = 10000;

    private final HttpClient httpClient;
    private final ClocktowerServerStateManager stateManager;
    private final String sseEndpoint;

    private volatile boolean running = false;
    private volatile TownConnectionStatus connectionStatus = TownConnectionStatus.DISCONNECTED;
    private volatile String currentTownName = "";

    private final AtomicReference<Stream<String>> activeStream = new AtomicReference<>(null);
    private final AtomicReference<Thread> connectionThread = new AtomicReference<>(null);

    public TownConnectionHandler(String sseEndpoint, ClocktowerServerStateManager stateManager) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(5))
                .build();
        this.sseEndpoint = sseEndpoint;
        this.stateManager = stateManager;
    }

    // --- Public API ---

    public void connect(String townName) {
        stopExistingConnection();
        currentTownName = townName;
        running = true;
        connectionStatus = TownConnectionStatus.CONNECTING;
        LOGGER.info("Attempting to connect to town: {}", townName);

        startConnectionThread();
    }

    public void disconnect() {
        LOGGER.info("Disconnecting from town server");
        stopExistingConnection();
        stateManager.setEmptyState();
    }

    public TownConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    // --- Connection lifecycle ---

    private void stopExistingConnection() {
        running = false;
        connectionStatus = TownConnectionStatus.DISCONNECTED;
        currentTownName = "";

        Stream<String> stream = activeStream.getAndSet(null);
        if (stream != null) stream.close();

        Thread thread = connectionThread.getAndSet(null);
        if (thread != null) thread.interrupt();
    }

    private void startConnectionThread() {
        Thread thread = new Thread(this::retryLoop, "sse-connection-thread");
        connectionThread.set(thread);
        thread.setDaemon(true);
        thread.start();
    }

    private void retryLoop() {
        long firstFailureTime = -1;

        while (running) {
            ConnectionResult result = connectAndListen();

            if (result == ConnectionResult.DISCONNECTED_BY_USER) return;

            if (result == ConnectionResult.CONNECTED_AND_STREAM_ENDED) {
                firstFailureTime = -1; // reset failure window
                continue;
            }

            // result == FAILED_TO_CONNECT
            if (firstFailureTime == -1) firstFailureTime = System.currentTimeMillis();

            boolean exceededRetryWindow = System.currentTimeMillis() - firstFailureTime >= MAX_RETRY_DURATION_MS;
            if (exceededRetryWindow) {
                LOGGER.error("Could not reconnect after {}ms, giving up", MAX_RETRY_DURATION_MS);
                connectionStatus = TownConnectionStatus.DISCONNECTED;
                return;
            }

            connectionStatus = TownConnectionStatus.CONNECTION_LOST;
            LOGGER.info("Reconnecting in {}ms...", RETRY_DELAY_MS);

            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    // --- SSE connection ---

    private enum ConnectionResult {
        CONNECTED_AND_STREAM_ENDED,
        FAILED_TO_CONNECT,
        DISCONNECTED_BY_USER
    }

    private ConnectionResult connectAndListen() {
        boolean stillRunning = false; 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(sseEndpoint))                              // TODO: add town name as suffix to endpoint
                .header("Accept", "text/event-stream")
                .header("Cache-Control", "no-cache")
                .GET()
                .build();

        try {
            HttpResponse<Stream<String>> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofLines()
            );

            if (response.statusCode() != 200) {
                LOGGER.error("Failed to connect, status: {}", response.statusCode());
                return ConnectionResult.FAILED_TO_CONNECT;
            }

            connectionStatus = TownConnectionStatus.CONNECTED;
            LOGGER.info("Successfully connected to town server");
            stillRunning = consumeEventStream(response.body());
        } 

        catch (IOException | InterruptedException | UncheckedIOException e) {
            if (!running) {
                LOGGER.info("SSE connection closed by disconnect()");
                return ConnectionResult.DISCONNECTED_BY_USER;
            }
            LOGGER.error("SSE connection error: {}", e.getMessage(), e);
            return ConnectionResult.FAILED_TO_CONNECT;
        }

        if (stillRunning) LOGGER.warn("SSE stream ended unexpectedly");
        return ConnectionResult.CONNECTED_AND_STREAM_ENDED;
    }

    private boolean consumeEventStream(Stream<String> stream) {
        try (stream) {
            activeStream.set(stream);
            stream
                .filter(line -> running && line.startsWith("data:"))
                .map(line -> line.substring("data:".length()).trim())
                .forEach(this::processEventData);
        } catch (UncheckedIOException e) {
            if (!running) {
                LOGGER.info("SSE stream closed during disconnect");
                return false;
            }
            throw e;
        } finally {
            activeStream.set(null);
        }
        return running;
    }

    // --- Event processing ---

    private void processEventData(String jsonData) {
        try {
            ClocktowerState newState = ClocktowerStateConverter.updateClocktowerState(
                    stateManager.getState(),
                    jsonData,
                    currentTownName
            );
            LOGGER.info("State updated - Town: {}, Day: {}, Phase: {}, Players: {}",
                    newState.townName(),
                    newState.currentDay(),
                    newState.currentPhase(),
                    newState.players().size());
            stateManager.updateState(newState);
        } catch (Exception e) {
            LOGGER.error("Failed to parse state JSON: {}", e.getMessage(), e);
        }
    }
}