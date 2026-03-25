package online.refract.game.state;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import online.refract.game.state.Enums.GamePhase;
import online.refract.game.state.Enums.TownConnectionStatus;

public class ClocktowerState {

    public List<ClocktowerPlayer> players;
    public List<ClocktowerRole> roles;
    public int currentDay;
    public GamePhase currentPhase;
    public String townName;
    public String scriptEdition;
    public boolean isVoteActive;
    public TownConnectionStatus townConnectionStatus;

    public static final ClocktowerState EMPTY = new ClocktowerState(
        List.of(),
        List.of(),
        0,
        GamePhase.DAY,
        "Unknown",
        "Unknown",
        false,
        TownConnectionStatus.DISCONNECTED
    );

    public ClocktowerState(
        List<ClocktowerPlayer> players,
        List<ClocktowerRole> roles,
        int currentDay,
        GamePhase currentPhase,
        String townName,
        String scriptEdition,
        boolean isVoteActive,
        TownConnectionStatus townConnectionStatus
    ) {
        this.players = new ArrayList<>(players);
        this.roles = new ArrayList<>(roles);
        this.currentDay = currentDay;
        this.currentPhase = currentPhase;
        this.townName = townName;
        this.scriptEdition = scriptEdition;
        this.isVoteActive = isVoteActive;
        this.townConnectionStatus = townConnectionStatus;
    }


    public static final Codec<ClocktowerState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.list(ClocktowerPlayer.CODEC).fieldOf("players").forGetter(s -> s.players),
        Codec.list(ClocktowerRole.CODEC).fieldOf("roles").forGetter(s ->s.roles),
        Codec.INT.optionalFieldOf("current_day", 0).forGetter(s -> s.currentDay),
        GamePhase.CODEC.optionalFieldOf("current_phase", GamePhase.DAY).forGetter(s -> s.currentPhase),
        Codec.STRING.optionalFieldOf("town_name", "Unknown").forGetter(s -> s.townName),
        Codec.STRING.optionalFieldOf("script_edition", "Unknown").forGetter(s -> s.scriptEdition),
        Codec.BOOL.optionalFieldOf("is_vote_active", false).forGetter(s -> s.isVoteActive),
        TownConnectionStatus.CODEC.optionalFieldOf("town_connection_status", TownConnectionStatus.DISCONNECTED).forGetter(s -> s.townConnectionStatus)
    ).apply(instance, ClocktowerState::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, ClocktowerState> STREAM_CODEC = StreamCodec.of(
        (buf, state) -> {
            ClocktowerPlayer.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, state.players);
            ClocktowerRole.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, state.roles);
            ByteBufCodecs.VAR_INT.encode(buf, state.currentDay);
            GamePhase.STREAM_CODEC.encode(buf, state.currentPhase);
            ByteBufCodecs.STRING_UTF8.encode(buf, state.townName);
            ByteBufCodecs.STRING_UTF8.encode(buf, state.scriptEdition);
            ByteBufCodecs.BOOL.encode(buf, state.isVoteActive);
            TownConnectionStatus.STREAM_CODEC.encode(buf, state.townConnectionStatus);
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



    public List<ClocktowerPlayer> getPlayers() {
        return new ArrayList<>(players);
    }
    public List<ClocktowerRole> getRoles() {
        return new ArrayList<>(roles);
    }
}
