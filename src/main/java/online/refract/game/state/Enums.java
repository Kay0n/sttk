package online.refract.game.state;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public class Enums {
    

    public static enum Alignment implements StringRepresentable {
        GOOD("good"), EVIL("evil"), UNKNOWN("unknown");
        public static final Codec<Alignment> CODEC = StringRepresentable.fromEnum(Alignment::values);
        public static final StreamCodec<ByteBuf, Alignment> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
        private final String name;
        Alignment(String name) { this.name = name; }
        public String getSerializedName() { return name; }
    }
    

    public static enum RoleType implements StringRepresentable {
        TOWNSFOLK("townsfolk"), OUTSIDER("outsider"), MINION("minion"), DEMON("demon");
        public static final Codec<RoleType> CODEC = StringRepresentable.fromEnum(RoleType::values);
        public static final StreamCodec<ByteBuf, RoleType> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
        private final String name;
        RoleType(String name) { this.name = name; }
        public String getSerializedName() { return name; }
    }
    

    public static enum TownConnectionStatus implements StringRepresentable {
        DISCONNECTED("disconnected"), CONNECTING("connecting"), CONNECTED("connected"),
        INVALID_TOWN("invalid_town"), CONNECTION_LOST("connection_lost");
        public static final Codec<TownConnectionStatus> CODEC = StringRepresentable.fromEnum(TownConnectionStatus::values);
        public static final StreamCodec<ByteBuf, TownConnectionStatus> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
        private final String name;
        TownConnectionStatus(String name) { this.name = name; }
        public String getSerializedName() { return name; }
    }
    

    public static enum GamePhase implements StringRepresentable {
        DAY("day"), NIGHT("night");
        public static final Codec<GamePhase> CODEC = StringRepresentable.fromEnum(GamePhase::values);
        public static final StreamCodec<ByteBuf, GamePhase> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
        private final String name;
        GamePhase(String name) { this.name = name; }
        public String getSerializedName() { return name; }
    }



    

    

}
