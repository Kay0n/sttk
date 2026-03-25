package online.refract.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import online.refract.Sttk;

public class C2SPackets {


    public record ToggleVotePayload(boolean active) implements CustomPacketPayload {
        public static final Type<ToggleVotePayload> ID = new Type<>(Sttk.id("toggle_vote"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ToggleVotePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ToggleVotePayload::active,
            ToggleVotePayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }

    public record LinkUsernamePayload(String playerId, String minecraftUsername) implements CustomPacketPayload {
        public static final Type<LinkUsernamePayload> ID = new Type<>(Sttk.id("link_username"));
        public static final StreamCodec<RegistryFriendlyByteBuf, LinkUsernamePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, LinkUsernamePayload::playerId,
            ByteBufCodecs.STRING_UTF8, LinkUsernamePayload::minecraftUsername,
            LinkUsernamePayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }


}