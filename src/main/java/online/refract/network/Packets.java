package online.refract.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import online.refract.ClocktowerState;
import online.refract.Sttk;

public class Packets {


    public record SyncStateS2CPayload(ClocktowerState state) implements CustomPacketPayload {
        public static final ResourceLocation SYNC_STATE_PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(Sttk.MOD_ID, "sync_state");
        public static final CustomPacketPayload.Type<SyncStateS2CPayload> ID = new CustomPacketPayload.Type<>(SYNC_STATE_PAYLOAD_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncStateS2CPayload> CODEC = StreamCodec.composite(
            ClocktowerState.STREAM_CODEC,
            SyncStateS2CPayload::state, 
            SyncStateS2CPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    
    
}
