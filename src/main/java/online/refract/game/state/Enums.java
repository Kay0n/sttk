package online.refract.game.state;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public class Enums {

    public enum Alignment implements StringRepresentable {
        GOOD, EVIL;

        public static final Codec<Alignment> CODEC = StringRepresentable.fromEnum(Alignment::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, Alignment> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.name()),
            buf -> Alignment.valueOf(ByteBufCodecs.STRING_UTF8.decode(buf).toUpperCase())
        );

        @Override
        public String getSerializedName() { return name().toLowerCase(); }
    }

    public enum RoleType implements StringRepresentable {
        TOWNSFOLK, OUTSIDER, MINION, DEMON;

        public static final Codec<RoleType> CODEC = StringRepresentable.fromEnum(RoleType::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, RoleType> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.name()),
            buf -> RoleType.valueOf(ByteBufCodecs.STRING_UTF8.decode(buf).toUpperCase())
        );

        @Override
        public String getSerializedName() { return name().toLowerCase(); }

        public static RoleType from(String name) {
            for (RoleType t : values()) {
                if (t.name().equalsIgnoreCase(name)) return t;
            }
            return TOWNSFOLK;
        }
    }

    public enum TownConnectionStatus implements StringRepresentable {
        DISCONNECTED, CONNECTING, CONNECTED, INVALID_TOWN, CONNECTION_LOST;

        public static final Codec<TownConnectionStatus> CODEC = StringRepresentable.fromEnum(TownConnectionStatus::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, TownConnectionStatus> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.name()),
            buf -> TownConnectionStatus.valueOf(ByteBufCodecs.STRING_UTF8.decode(buf).toUpperCase())
        );

        @Override
        public String getSerializedName() { return name().toLowerCase(); }
    }

    public enum GamePhase implements StringRepresentable {
        DAY, NIGHT;

        public static final Codec<GamePhase> CODEC = StringRepresentable.fromEnum(GamePhase::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, GamePhase> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.name()),
            buf -> GamePhase.valueOf(ByteBufCodecs.STRING_UTF8.decode(buf).toUpperCase())
        );

        @Override
        public String getSerializedName() { return name().toLowerCase(); }
    }
}