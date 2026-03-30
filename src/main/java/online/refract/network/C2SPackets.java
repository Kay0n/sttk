package online.refract.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.network.C2SPackets.ConnectToTownPayload;
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

    public record LinkUsernamePayload(ClocktowerPlayer playerToLink, String minecraftUsername) implements CustomPacketPayload {
        public static final Type<LinkUsernamePayload> ID = new Type<>(Sttk.id("link_username"));
        public static final StreamCodec<RegistryFriendlyByteBuf, LinkUsernamePayload> STREAM_CODEC = StreamCodec.composite(
            ClocktowerPlayer.STREAM_CODEC, LinkUsernamePayload::playerToLink,
            ByteBufCodecs.STRING_UTF8, LinkUsernamePayload::minecraftUsername,
            LinkUsernamePayload::new
        );
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }

    public record ConnectToTownPayload(String townName) implements CustomPacketPayload {
        public static final Type<ConnectToTownPayload> ID = new Type<>(Sttk.id("connect_to_town"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ConnectToTownPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ConnectToTownPayload::townName,
            ConnectToTownPayload::new
        );
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }

    public record DisconnectFromTownPayload() implements CustomPacketPayload {
        public static final Type<DisconnectFromTownPayload> ID = new Type<>(Sttk.id("disconnect_from_town"));
        public static final StreamCodec<RegistryFriendlyByteBuf, DisconnectFromTownPayload> STREAM_CODEC = StreamCodec.unit(new DisconnectFromTownPayload());
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }


    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(ToggleVotePayload.ID, ToggleVotePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(LinkUsernamePayload.ID, LinkUsernamePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ConnectToTownPayload.ID, ConnectToTownPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(DisconnectFromTownPayload.ID, DisconnectFromTownPayload.STREAM_CODEC);
    }
}