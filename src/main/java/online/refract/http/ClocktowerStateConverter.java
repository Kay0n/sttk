package online.refract.http;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import online.refract.game.state.*;
import online.refract.game.state.Enums.Alignment;
import online.refract.game.state.Enums.GamePhase;
import online.refract.game.state.Enums.RoleType;
import online.refract.game.state.Enums.TownConnectionStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class ClocktowerStateConverter {

    public static ClocktowerState parseJsonToState(ClocktowerState currentState, String jsonPayload, String townName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonPayload);
        
        JsonNode gameStateNode = rootNode.get("gameState");
        JsonNode playersNode = rootNode.get("playersInGrim");
        JsonNode rolesNode = rootNode.get("rolesInScript");
        JsonNode scriptNameNode = rootNode.get("scriptName");
        
        List<ClocktowerPlayer> players = new ArrayList<>();
        List<ClocktowerRole> scriptRoles = new ArrayList<>();

        if (playersNode != null && playersNode.isArray()) {
            Map<String, ClocktowerPlayer> existingPlayersByName = new HashMap<>();
            for (ClocktowerPlayer existingPlayer : currentState.players()) {
                existingPlayersByName.put(existingPlayer.name(), existingPlayer);
            }

            for (JsonNode playerNode : playersNode) {
                String playerName = playerNode.get("name").asText();
                ClocktowerPlayer existingPlayer = existingPlayersByName.get(playerName); // can be null, representing "new" player
                players.add(updateClocktowerPlayer(existingPlayer, playerNode));
            }
        }
        
        if (rolesNode != null && rolesNode.isArray()) {
            for (JsonNode roleNode : rolesNode) {
                scriptRoles.add(getClocktowerRole(roleNode));
            }
        }
        
        int currentDay = gameStateNode != null ? gameStateNode.get("currentDay").asInt() : 0;
        String currentPhaseStr = gameStateNode != null ? gameStateNode.get("currentPhase").asText() : "Day";
        GamePhase currentPhase = GamePhase.valueOf(currentPhaseStr.toUpperCase());
        String scriptEdition = scriptNameNode != null ? scriptNameNode.asText() : "Unknown";
        
        return new ClocktowerState(
            players,
            scriptRoles,
            currentDay,
            currentPhase,
            townName,
            scriptEdition,
            currentState.isVoteActive(), 
            TownConnectionStatus.CONNECTED // townConnectionStatus - default to CONNECTED        TODO: Check if correct
        );
    }



    private static ClocktowerPlayer updateClocktowerPlayer(@Nullable ClocktowerPlayer player , JsonNode playerNode) {
        String name = playerNode.get("name").asText();
        JsonNode roleNode = playerNode.get("roles").get(0);
        String roleName = roleNode.get("name").asText();
        boolean isGood = roleNode.get("alignment").asText().equals("Good");
        boolean isNominated = roleNode.get("onTheBlock").asBoolean();
        boolean isDead = !playerNode.get("alive").asBoolean();
        boolean hasUsedGhostVote = !playerNode.get("ghostvote").asBoolean(); // TODO: switch state to hasGhostVote
        String linkedMinecraftUsername = player != null ? player.linkedMinecraftUsername() : "";

        return new ClocktowerPlayer(
            name,
            roleName,
            isGood ? Alignment.GOOD : Alignment.EVIL, 
            linkedMinecraftUsername,
            isDead, 
            hasUsedGhostVote,
            isNominated  
        );
    }

    private static ClocktowerRole getClocktowerRole(JsonNode roleNode) {
        String name = roleNode.get("name").asText();
        String typeStr = roleNode.get("type").asText();
        RoleType type = RoleType.from(typeStr);
        boolean isGood = roleNode.get("alignment").asText().equals("Good");
        String iconUrl = roleNode.get("icon").asText();
        String abilityText = roleNode.get("ability").asText();
        String edition = roleNode.get("edition").asText();
        String firstNightReminder = roleNode.get("firstNightReminder") != null ? roleNode.get("firstNightReminder").asText() : "";
        String otherNightReminder = roleNode.get("otherNightReminder") != null ? roleNode.get("otherNightReminder").asText() : "";
        
        return new ClocktowerRole(
            name,
            type,
            isGood ? Alignment.GOOD : Alignment.EVIL, 
            iconUrl,
            abilityText,
            edition,
            firstNightReminder,
            otherNightReminder
        );
    }


}