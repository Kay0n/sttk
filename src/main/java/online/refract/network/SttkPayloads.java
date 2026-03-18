package online.refract;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class SttkPayloads {

    public record ActionPayload(String action, int targetId) implements CustomPacketPayload {
        public static final Type<ActionPayload> ID = new Type<>(Sttk.id("action"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ActionPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ActionPayload::action,
            ByteBufCodecs.INT, ActionPayload::targetId,
            ActionPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }


}