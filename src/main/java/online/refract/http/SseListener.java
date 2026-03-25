package online.refract.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger("sttk");
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    // Handle incoming SSE requests from web app
    public void handleRequest(Request request, Response response) throws IOException {
        // Parse URL: /botc/stream/{town}
        String url = request.url().toString();
        String townName = extractTownName(url);

        if (townName == null || townName.isEmpty()) {
            throw new IOException("Invalid town name");
        }

        LOGGER.info("SSE request received for town: {}", townName);

        // Create EventSource response
        response.request().newBuilder()
            .header("Content-Type", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .header("Connection", "keep-alive")
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
            .build();

        // Send initial empty state
        response.body().close();
        response.close();
    }

    // Parse URL and extract town name
    private static String extractTownName(String url) {
        try {
            if (url.contains("/botc/stream/")) {
                String[] parts = url.split("/botc/stream/");
                if (parts.length > 1) {
                    String town = parts[1];
                    // Remove query parameters
                    int queryIndex = town.indexOf("?");
                    if (queryIndex >= 0) {
                        town = town.substring(0, queryIndex);
                    }
                    return town;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error parsing URL: {}", url, e);
        }
        return null;
    }
}