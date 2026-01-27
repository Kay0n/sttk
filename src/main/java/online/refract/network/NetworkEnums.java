package online.refract.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import online.refract.network.NetworkEnums.PlayerActionType;

public class NetworkEnums {

    public enum PlayerActionType {
        KILL,
        NOMINATE,
        PUT_ON_BLOCK,
        GHOST_VOTE,
        HOME_TELEPORT;

        // private static final PlayerActionType[] VALUES = values();

        // public static PlayerActionType byId(int id) {
        //     if (id < 0 || id >= VALUES.length) return VALUES[0]; 
        //     return VALUES[id];
        // }

        public static final StreamCodec<ByteBuf, PlayerActionType> STREAM_CODEC =
            // ByteBufCodecs.idMapper(PlayerActionType::byId, PlayerActionType::ordinal);
            ByteBufCodecs.idMapper(i -> PlayerActionType.values()[i], PlayerActionType::ordinal);

    }

    public enum GameActionType {
        START_VOTE,
        SET_NIGHT,
        SET_EVENING,
        SET_DAY,
        RESET_SCORES;

        // private static final GameActionType[] VALUES = values();

        // public static GameActionType byId(int id) {
        //     if (id < 0 || id >= VALUES.length) return VALUES[0];
        //     return VALUES[id];
        // }

        public static final StreamCodec<ByteBuf, GameActionType> STREAM_CODEC =
            // ByteBufCodecs.idMapper(GameActionType::byId, GameActionType::ordinal);
            ByteBufCodecs.idMapper(i -> GameActionType.values()[i], GameActionType::ordinal);

    }
}