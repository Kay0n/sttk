package online.refract;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public class SttkPayloads {

    public record ActionPayload(String action, int targetId) implements CustomPayload {
        public static final Id<ActionPayload> ID = new Id<>(Sttk.id("action"));
        public static final PacketCodec<RegistryByteBuf, ActionPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, ActionPayload::action,
            PacketCodecs.INTEGER, ActionPayload::targetId,
            ActionPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }


}