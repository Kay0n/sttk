package online.refract.game.server;

import net.fabricmc.loader.api.FabricLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import online.refract.Sttk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;


public class PlayerLinkStore {


    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final MapType MAP_TYPE = MAPPER.getTypeFactory()
        .constructMapType(HashMap.class, String.class, String.class);

    private final Path filePath;
    private Map<String, String> playerToUsernameLinks;

    public PlayerLinkStore() {
        this.filePath = FabricLoader.getInstance()
            .getConfigDir()
            .resolve(Sttk.MOD_ID)
            .resolve("player_links.json");
        this.playerToUsernameLinks = load();
    }

    public String get(String clocktowerName) {
        return playerToUsernameLinks.get(clocktowerName);
    }

    public Map<String, String> all() {
        return Map.copyOf(playerToUsernameLinks);
    }


    public void set(String clocktowerName, @Nullable String minecraftUsername) {
        if (minecraftUsername == null) {
            playerToUsernameLinks.remove(clocktowerName);
        } else {
            playerToUsernameLinks.put(clocktowerName, minecraftUsername);
        }
        save();
    }

    private Map<String, String> load() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }
        try {
            return MAPPER.readValue(filePath.toFile(), MAP_TYPE);
        } catch (IOException e) {
            Sttk.LOGGER.error("Failed to load player links: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private void save() {
        try {
            Files.createDirectories(filePath.getParent());
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), playerToUsernameLinks);
        } catch (IOException e) {
            Sttk.LOGGER.error("Failed to save player links: {}", e.getMessage());
        }
    }
}