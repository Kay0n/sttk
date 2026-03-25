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
        public static final StreamCodec<RegistryFriendlyByteBuf, Alignment> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.getSerializedName()),
            buf -> Enum.valueOf(Alignment.class, ByteBufCodecs.STRING_UTF8.decode(buf))
        );
        private final String name;
        Alignment(String name) { this.name = name; }
        public String getSerializedName() { return name; }
    }

    public static enum RoleType implements StringRepresentable {
        TOWNSFOLK("townsfolk"), OUTSIDER("outsider"), MINION("minion"), DEMON("demon");
        public static final Codec<RoleType> CODEC = StringRepresentable.fromEnum(RoleType::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, RoleType> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.getSerializedName()),
            buf -> Enum.valueOf(RoleType.class, ByteBufCodecs.STRING_UTF8.decode(buf))
        );
        private final String name;
        RoleType(String name) { this.name = name; }
        public String getSerializedName() { return name; }
        public static RoleType from(String name) {
            for (RoleType t : values()) {
                if (t.name.equalsIgnoreCase(name)) {
                    return t;
                }
            }
            return TOWNSFOLK;
        }
    }

    public static enum TownConnectionStatus implements StringRepresentable {
        DISCONNECTED("disconnected"), CONNECTING("connecting"), CONNECTED("connected"),
        INVALID_TOWN("invalid_town"), CONNECTION_LOST("connection_lost");
        public static final Codec<TownConnectionStatus> CODEC = StringRepresentable.fromEnum(TownConnectionStatus::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, TownConnectionStatus> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.getSerializedName()),
            buf -> (TownConnectionStatus) Enum.valueOf(TownConnectionStatus.class, ByteBufCodecs.STRING_UTF8.decode(buf))
        );
        private final String name;
        TownConnectionStatus(String name) { this.name = name; }
        public String getSerializedName() { return name; }
    }

    public static enum GamePhase implements StringRepresentable {
        DAY("day"), NIGHT("night");
        public static final Codec<GamePhase> CODEC = StringRepresentable.fromEnum(GamePhase::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, GamePhase> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.getSerializedName()),
            buf -> (GamePhase) Enum.valueOf(GamePhase.class, ByteBufCodecs.STRING_UTF8.decode(buf))
        );
        private final String name;
        GamePhase(String name) { this.name = name; }
        public String getSerializedName() { return name; }
    }

}
