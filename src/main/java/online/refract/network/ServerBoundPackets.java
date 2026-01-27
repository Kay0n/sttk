package online.refract.network;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;
import online.refract.Sttk;
import online.refract.network.NetworkEnums.GameActionType;
import online.refract.network.NetworkEnums.PlayerActionType;

public class ServerBoundPackets {



    public record PlayerActionPayload(UUID target, PlayerActionType action) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<PlayerActionPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Sttk.MOD_ID, "player_action"));
        
        public static final StreamCodec<RegistryFriendlyByteBuf, PlayerActionPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PlayerActionPayload::target,
            PlayerActionType.STREAM_CODEC, PlayerActionPayload::action,
            PlayerActionPayload::new
        );
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }



    public record GameActionPayload(GameActionType action) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<GameActionPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Sttk.MOD_ID, "game_action"));
        
        public static final StreamCodec<RegistryFriendlyByteBuf, GameActionPayload> STREAM_CODEC = StreamCodec.composite(
            GameActionType.STREAM_CODEC, GameActionPayload::action,
            GameActionPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }



    public record SetTimerPayload(int seconds) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SetTimerPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Sttk.MOD_ID, "set_timer"));
        
        public static final StreamCodec<RegistryFriendlyByteBuf, SetTimerPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetTimerPayload::seconds,
            SetTimerPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }



    public record ReorderPlayersPayload(List<UUID> newOrder) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ReorderPlayersPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Sttk.MOD_ID, "reorder_players"));
        
        public static final StreamCodec<RegistryFriendlyByteBuf, ReorderPlayersPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), ReorderPlayersPayload::newOrder,
            ReorderPlayersPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }
}