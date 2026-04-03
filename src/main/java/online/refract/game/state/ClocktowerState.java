package online.refract.game.state;

import java.util.List;
import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import online.refract.game.state.Enums.GamePhase;
import online.refract.game.state.Enums.TownConnectionStatus;

public record ClocktowerState(
    List<ClocktowerPlayer> players,
    List<ClocktowerRole> roles,
    int currentDay,
    GamePhase currentPhase,
    String townName,
    String scriptEdition,
    boolean isVoteActive,
    // TODO: TimerState class
    TownConnectionStatus townConnectionStatus
) {

    public ClocktowerState {
        players = List.copyOf(players);
        roles = List.copyOf(roles);
    }

    public static final ClocktowerState EMPTY = new ClocktowerState(
        List.of(),
        List.of(),
        0,
        GamePhase.DAY,
        "",
        "",
        false,
        TownConnectionStatus.DISCONNECTED
    );


    public ClocktowerState withVoteActive(boolean active) {
        return new ClocktowerState(
            players,
            roles,
            currentDay,
            currentPhase,
            townName,
            scriptEdition,
            active,
            townConnectionStatus
        );
    }

    // update single player
    public ClocktowerState withUpdatedPlayer(String clocktowerPlayerName, UnaryOperator<ClocktowerPlayer> updater) {
        return new ClocktowerState(
            players.stream()
                   .map(p -> p.name().equals(clocktowerPlayerName) ? updater.apply(p) : p)
                   .toList(),
            roles,
            currentDay,
            currentPhase,
            townName,
            scriptEdition,
            isVoteActive,
            townConnectionStatus
        );
    }


    public ClocktowerState withTownConnectionStatus(TownConnectionStatus status){
        return new ClocktowerState(
            players,
            roles,
            currentDay,
            currentPhase,
            townName,
            scriptEdition,
            isVoteActive,
            status
        );
    }

    public static final Codec<ClocktowerState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.list(ClocktowerPlayer.CODEC).fieldOf("players").forGetter(ClocktowerState::players),
        Codec.list(ClocktowerRole.CODEC).fieldOf("roles").forGetter(ClocktowerState::roles),
        Codec.INT.optionalFieldOf("current_day", 0).forGetter(ClocktowerState::currentDay),
        GamePhase.CODEC.optionalFieldOf("current_phase", GamePhase.DAY).forGetter(ClocktowerState::currentPhase),
        Codec.STRING.optionalFieldOf("town_name", "Unknown").forGetter(ClocktowerState::townName),
        Codec.STRING.optionalFieldOf("script_edition", "Unknown").forGetter(ClocktowerState::scriptEdition),
        Codec.BOOL.optionalFieldOf("is_vote_active", false).forGetter(ClocktowerState::isVoteActive),
        TownConnectionStatus.CODEC.optionalFieldOf("town_connection_status", TownConnectionStatus.DISCONNECTED)
            .forGetter(ClocktowerState::townConnectionStatus)
    ).apply(instance, ClocktowerState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClocktowerState> STREAM_CODEC = StreamCodec.of(
        (buf, state) -> {
            ClocktowerPlayer.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, state.players());
            ClocktowerRole.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, state.roles());
            ByteBufCodecs.VAR_INT.encode(buf, state.currentDay());
            GamePhase.STREAM_CODEC.encode(buf, state.currentPhase());
            ByteBufCodecs.STRING_UTF8.encode(buf, state.townName());
            ByteBufCodecs.STRING_UTF8.encode(buf, state.scriptEdition());
            ByteBufCodecs.BOOL.encode(buf, state.isVoteActive());
            TownConnectionStatus.STREAM_CODEC.encode(buf, state.townConnectionStatus());
        },
        buf -> new ClocktowerState(
            ClocktowerPlayer.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
            ClocktowerRole.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
            ByteBufCodecs.VAR_INT.decode(buf),
            GamePhase.STREAM_CODEC.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.BOOL.decode(buf),
            TownConnectionStatus.STREAM_CODEC.decode(buf)
        )
    );

}