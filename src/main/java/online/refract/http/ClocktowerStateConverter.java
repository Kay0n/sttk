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
import java.util.List;

public class ClocktowerStateConverter {

    public static ClocktowerState fromJsonPayload(String jsonPayload, String townName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonPayload);
        
        JsonNode gameStateNode = rootNode.get("gameState");
        JsonNode playersNode = rootNode.get("playersInGrim");
        JsonNode rolesNode = rootNode.get("rolesInScript");
        JsonNode scriptNameNode = rootNode.get("scriptName");
        
        List<ClocktowerPlayer> players = new ArrayList<>();
        if (playersNode != null && playersNode.isArray()) {
            for (JsonNode playerNode : playersNode) {
                players.add(toClocktowerPlayer(playerNode));
            }
        }
        
        List<ClocktowerRole> scriptRoles = new ArrayList<>();
        if (rolesNode != null && rolesNode.isArray()) {
            for (JsonNode roleNode : rolesNode) {
                scriptRoles.add(toClocktowerRole(roleNode));
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
            false, 
            TownConnectionStatus.CONNECTED // townConnectionStatus - default to CONNECTED
        );
    }



    private static ClocktowerPlayer toClocktowerPlayer(JsonNode playerNode) {
        String name = playerNode.get("name").asText();
        JsonNode roleNode = playerNode.get("roles").get(0);
        String roleName = roleNode.get("name").asText();
        boolean isGood = roleNode.get("alignment").asText().equals("Good");
        boolean isNominated = roleNode.get("onTheBlock").asBoolean();
        boolean isDead = !playerNode.get("alive").asBoolean();
        boolean hasUsedGhostVote = playerNode.has("ghostvote") ? playerNode.get("ghostvote").asBoolean() : false;

        return new ClocktowerPlayer(
            null, // uuid - will be generated
            name,
            roleName,
            isGood ? Alignment.GOOD : Alignment.EVIL, 
            null, 
            isDead, // isDead in ClocktowerPlayer
            hasUsedGhostVote,
            isNominated  // isNominated - role level
        );
    }

    private static ClocktowerRole toClocktowerRole(JsonNode roleNode) {
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