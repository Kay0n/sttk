package online.refract.game.server;

import net.fabricmc.loader.api.FabricLoader;
import online.refract.Sttk;
import online.refract.game.state.ClocktowerState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ServerAssetCache {

    private static final Logger LOGGER = LoggerFactory.getLogger("sttk/ServerAssetCache");

    public static final ServerAssetCache INSTANCE = new ServerAssetCache();

    private final Path cacheDir;
    private final HttpClient http;
    private final Executor ioExecutor;

    private final Map<String, CompletableFuture<byte[]>> cache = new ConcurrentHashMap<>();



    public ServerAssetCache() {
        this.cacheDir = FabricLoader.getInstance()
            .getConfigDir()
            .resolve(Sttk.MOD_ID)
            .resolve("asset_cache");
        this.http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.ioExecutor = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r, "sttk-asset-io");
            t.setDaemon(true);
            return t;
        });

        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            LOGGER.error("Failed to create asset cache directory: {}", cacheDir, e);
        }
    }



    public void onStateUpdate(ClocktowerState state) {
        state.allAssetUrls().forEach(url -> {
            if (!cache.containsKey(url)) {
                getOrFetch(url);
            }
        });
    }





    public CompletableFuture<byte[]> getOrFetch(String url) {
        return cache.computeIfAbsent(url, this::loadAsset);
    }



    // public @Nullable byte[] getIfCached(String url) {
    //     Path file = urlToFile(url);
    //     if (!Files.exists(file)) return null;
    //     try {
    //         return Files.readAllBytes(file);
    //     } catch (IOException e) {
    //         LOGGER.error("Failed to read cached asset for {}", url, e);
    //         return null;
    //     }
    // }



    private CompletableFuture<byte[]> loadAsset(String url) {
        Path file = urlToFile(url);

        return CompletableFuture.supplyAsync(() -> {
            if (Files.exists(file)) {
                try {
                    return Files.readAllBytes(file);
                } catch (IOException e) {
                    LOGGER.warn("Disk read failed for {}, re-downloading", url, e);
                }
            }

            try {
                byte[] pngBytes = downloadAndConvert(url);
                Files.write(file, pngBytes);
                LOGGER.info("Cached asset {}", url);
                return pngBytes;
            } catch (Exception e) {
                LOGGER.error("Failed to fetch/convert asset {}: {}", url, e.getMessage());
                cache.remove(url);
                throw new RuntimeException("Asset fetch failed for " + url, e);
            }
        }, ioExecutor);
    }



    private byte[] downloadAndConvert(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(30))
            .GET()
            .build();

        HttpResponse<byte[]> response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " for " + url);
        }

        byte[] raw = response.body();
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(raw));
        if (image == null) {
            throw new IOException("ImageIO could not decode image from " + url);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", out);
        return out.toByteArray();
    }



    private Path urlToFile(String url) {
        return cacheDir.resolve(sha256(url) + ".png");
    }



    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}