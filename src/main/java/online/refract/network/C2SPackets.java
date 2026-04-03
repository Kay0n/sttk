package online.refract.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import online.refract.game.state.ClocktowerPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import online.refract.Sttk;

public class C2SPackets {

    // public record ToggleVotePayload(boolean active) implements CustomPacketPayload {
    //     public static final Type<ToggleVotePayload> ID = new Type<>(Sttk.id("toggle_vote"));
    //     public static final StreamCodec<RegistryFriendlyByteBuf, ToggleVotePayload> STREAM_CODEC = StreamCodec.composite(
    //         ByteBufCodecs.BOOL, ToggleVotePayload::active,
    //         ToggleVotePayload::new
    //     );
    //     @Override
    //     public Type<? extends CustomPacketPayload> type() { return ID; }
    // }

    public record StartVoteForPlayerPayload(ClocktowerPlayer player) implements CustomPacketPayload {
        public static final Type<StartVoteForPlayerPayload> ID = new Type<>(Sttk.id("start_vote_for_player"));
        public static final StreamCodec<RegistryFriendlyByteBuf, StartVoteForPlayerPayload> STREAM_CODEC = StreamCodec.composite(
            ClocktowerPlayer.STREAM_CODEC, StartVoteForPlayerPayload::player,
            StartVoteForPlayerPayload::new
        );
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }


    public record StopVotePayload() implements CustomPacketPayload {
        public static final Type<StopVotePayload> ID = new Type<>(Sttk.id("stop_vote"));
        public static final StreamCodec<RegistryFriendlyByteBuf, StopVotePayload> STREAM_CODEC = StreamCodec.unit(new StopVotePayload());
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }


    public record DistributeRolesToTownPayload() implements CustomPacketPayload {
        public static final Type<DistributeRolesToTownPayload> ID = new Type<>(Sttk.id("send_roles_to_town"));
        public static final StreamCodec<RegistryFriendlyByteBuf, DistributeRolesToTownPayload> STREAM_CODEC = StreamCodec.unit(new DistributeRolesToTownPayload());
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }


    public record RequestPrivateChatPayload(ClocktowerPlayer player) implements CustomPacketPayload {
        public static final Type<RequestPrivateChatPayload> ID = new Type<>(Sttk.id("request_private_chat"));
        public static final StreamCodec<RegistryFriendlyByteBuf, RequestPrivateChatPayload> STREAM_CODEC = StreamCodec.composite(
            ClocktowerPlayer.STREAM_CODEC, RequestPrivateChatPayload::player,
            RequestPrivateChatPayload::new
        );
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }


    public record RequestTeleportToPlayerPayload(ClocktowerPlayer player) implements CustomPacketPayload {
        public static final Type<RequestTeleportToPlayerPayload> ID = new Type<>(Sttk.id("request_teleport_to_player"));
        public static final StreamCodec<RegistryFriendlyByteBuf, RequestTeleportToPlayerPayload> STREAM_CODEC = StreamCodec.composite(
            ClocktowerPlayer.STREAM_CODEC, RequestTeleportToPlayerPayload::player,
            RequestTeleportToPlayerPayload::new
        );
        @Override
        public Type<? extends CustomPacketPayload> type() { return ID; }
    }


    public record RequestTeleportToHousePayload(ClocktowerPlayer player) implements CustomPacketPayload {
        public static final Type<RequestTeleportToHousePayload> ID = new Type<>(Sttk.id("request_teleport_to_house"));
        public static final StreamCodec<RegistryFriendlyByteBuf, RequestTeleportToHousePayload> STREAM_CODEC = StreamCodec.composite(
            ClocktowerPlayer.STREAM_CODEC, RequestTeleportToHousePayload::player,
            RequestTeleportToHousePayload::new
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
        PayloadTypeRegistry.playC2S().register(StartVoteForPlayerPayload.ID, StartVoteForPlayerPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(StopVotePayload.ID, StopVotePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(DistributeRolesToTownPayload.ID, DistributeRolesToTownPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestPrivateChatPayload.ID, RequestPrivateChatPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestTeleportToPlayerPayload.ID, RequestTeleportToPlayerPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestTeleportToHousePayload.ID, RequestTeleportToHousePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(LinkUsernamePayload.ID, LinkUsernamePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ConnectToTownPayload.ID, ConnectToTownPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(DisconnectFromTownPayload.ID, DisconnectFromTownPayload.STREAM_CODEC);
    }
}