package online.refract.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import online.refract.Sttk;
import online.refract.game.state.ClocktowerState;

public class S2CPackets {


    public record SyncStatePayload(ClocktowerState state) implements CustomPacketPayload {
        public static final ResourceLocation SYNC_STATE_PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(Sttk.MOD_ID, "sync_state");
        public static final CustomPacketPayload.Type<SyncStatePayload> ID = new CustomPacketPayload.Type<>(SYNC_STATE_PAYLOAD_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncStatePayload> CODEC = StreamCodec.composite(
            ClocktowerState.STREAM_CODEC,
            SyncStatePayload::state, 
            SyncStatePayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }

    
    public record ShowRoleAnimationPacket() implements CustomPacketPayload {
        public static final ResourceLocation SHOW_ROLE_ANIMATION_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Sttk.MOD_ID, "show_role_animation");
        public static final CustomPacketPayload.Type<ShowRoleAnimationPacket> ID = new CustomPacketPayload.Type<>(SHOW_ROLE_ANIMATION_PACKET_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, ShowRoleAnimationPacket> CODEC = StreamCodec.unit(new ShowRoleAnimationPacket());
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }


    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(SyncStatePayload.ID, SyncStatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ShowRoleAnimationPacket.ID, ShowRoleAnimationPacket.CODEC);
    }

    
    
}
