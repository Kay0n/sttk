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
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

public class ClocktowerStateConverter {

    public static ClocktowerState parseJsonToState(ClocktowerState currentState, String jsonPayload, String townName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonPayload);
        
        JsonNode gameStateNode = rootNode.get("gameState");
        JsonNode playersNode = rootNode.get("playersInGrim");
        JsonNode rolesNode = rootNode.get("rolesInScript");
        JsonNode scriptNameNode = rootNode.get("scriptName");
        JsonNode timerNode = rootNode.get("timer");
        
        List<ClocktowerPlayer> players = new ArrayList<>();
        List<ClocktowerRole> scriptRoles = new ArrayList<>();

        if (playersNode != null && playersNode.isArray()) {
            Map<String, ClocktowerPlayer> existingPlayersByName = new HashMap<>();
            for (ClocktowerPlayer existingPlayer : currentState.players()) {
                existingPlayersByName.put(existingPlayer.name(), existingPlayer);
            }

            for (JsonNode playerNode : playersNode) {
                String playerName = playerNode.get("name").asText();
                ClocktowerPlayer existingPlayer = existingPlayersByName.get(playerName); // can be null, representing "new" player // TODO:  check this
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
        TimerState timer = parseTimerState(timerNode);
        List<String> firstNightOrder = parseNightOrder(rootNode.get("nightOrder"), "firstNight", scriptRoles);
        List<String> otherNightOrder = parseNightOrder(rootNode.get("nightOrder"), "otherNight", scriptRoles);
        
        return new ClocktowerState(
            players,
            scriptRoles,
            currentDay,
            currentPhase,
            townName,
            scriptEdition,
            firstNightOrder,
            otherNightOrder,
            currentState.isVoteActive(), 
            timer,
            TownConnectionStatus.CONNECTED
        );
    }



    private static ClocktowerPlayer updateClocktowerPlayer(@Nullable ClocktowerPlayer player , JsonNode playerNode) {
        String name = playerNode.get("name").asText();
        JsonNode roleNode = playerNode.get("roles").get(0);
        boolean isGood = roleNode.get("alignment").asText().equals("Good");
        String roleName = roleNode.get("name").asText();
        String alignedIconUrl = isGood ? roleNode.get("officialGoodIcon").asText() : roleNode.get("officialEvilIcon").asText();
        boolean isNominated = roleNode.get("onTheBlock").asBoolean();
        boolean isDead = !playerNode.get("alive").asBoolean();
        boolean hasUsedGhostVote = !playerNode.get("ghostvote").asBoolean(); // TODO: switch state to hasGhostVote
        String linkedMinecraftUsername = player != null ? player.linkedMinecraftUsername() : null;

        return new ClocktowerPlayer(
            name,
            isGood ? Alignment.GOOD : Alignment.EVIL, 
            roleName,
            alignedIconUrl,
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
        String alignedIconUrl = isGood ? roleNode.get("officialGoodIcon").asText() : roleNode.get("officialEvilIcon").asText();
        String abilityText = roleNode.get("ability").asText();
        String edition = roleNode.get("edition").asText();
        return new ClocktowerRole(
            name,
            type,
            isGood ? Alignment.GOOD : Alignment.EVIL,
            alignedIconUrl,
            abilityText,
            edition
        );
    }

    private static TimerState parseTimerState(@Nullable JsonNode timerNode) {
        if (timerNode == null || timerNode.isNull()) {
            return TimerState.EMPTY;
        }

        boolean isRunning = timerNode.get("isRunning").asBoolean();
        boolean isStopwatch = timerNode.get("isStopwatch").asBoolean();
        int currentTime = timerNode.get("currentTime").asInt();
        int targetTime = timerNode.get("targetTime").asInt();

        JsonNode timestampNode = timerNode.get("startTimestamp");
        Long startTimestamp = (timestampNode != null && !timestampNode.isNull())
            ? timestampNode.asLong()
            : null;

        return new TimerState(isRunning, isStopwatch, currentTime, targetTime, startTimestamp);
    }

    private static List<String> parseNightOrder( @Nullable JsonNode nightOrderNode, String key, List<ClocktowerRole> scriptRoles) {

        if (nightOrderNode == null || nightOrderNode.isNull()) {
            return List.of();
        }

        JsonNode orderNode = nightOrderNode.get(key);
        if (orderNode == null || !orderNode.isArray()) {
            return List.of();
        }

        Map<String, String> scriptRolesByNormalized = scriptRoles.stream().collect(Collectors.toMap(
            role -> role.name().toLowerCase().replace(" ", ""),
            ClocktowerRole::name
        ));

        List<String> result = new ArrayList<>();
        for (JsonNode entry : orderNode) {
            String matched = scriptRolesByNormalized.get(entry.asText().toLowerCase());
            if (matched != null) {
                result.add(matched);
            }
        }

        return result;
    }


}