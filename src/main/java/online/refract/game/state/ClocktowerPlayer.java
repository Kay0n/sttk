package online.refract.game.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

import static online.refract.game.state.Enums.Alignment;


public class ClocktowerPlayer {

    public UUID uuid;
    public String name;
    public String roleName;
    public Alignment alignment;
    public String linkedMinecraftUsername;

    public boolean isDead;
    public boolean hasUsedGhostVote;
    public boolean isNominated;


    public ClocktowerPlayer(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.roleName = null;
        this.alignment = Alignment.UNKNOWN;
        this.linkedMinecraftUsername = null;
        this.isDead = false;
        this.hasUsedGhostVote = false;
        this.isNominated = false;
    }

    public ClocktowerPlayer(
        UUID uuid,
        String name,
        String roleName,
        Alignment alignment,
        String linkedMinecraftUsername,
        boolean isDead,
        boolean hasUsedGhostVote,
        boolean isNominated
    ) {
        this.uuid = uuid;
        this.name = name;
        this.roleName = roleName;
        this.alignment = alignment;
        this.linkedMinecraftUsername = linkedMinecraftUsername;
        this.isDead = isDead;
        this.hasUsedGhostVote = hasUsedGhostVote;
        this.isNominated = isNominated;
    }


    public static final Codec<ClocktowerPlayer> CODEC =
        RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(p -> p.uuid),
            Codec.STRING.fieldOf("name").forGetter(p -> p.name),
            Codec.STRING.optionalFieldOf("role_name", null).forGetter(p -> p.roleName),
            Alignment.CODEC.optionalFieldOf("alignment", Alignment.UNKNOWN).forGetter(p -> p.alignment),
            Codec.STRING.optionalFieldOf("linked_minecraft_username", null).forGetter(p -> p.linkedMinecraftUsername),

            Codec.BOOL.optionalFieldOf("is_dead", false).forGetter(p -> p.isDead),
            Codec.BOOL.optionalFieldOf("has_used_ghost_vote", false).forGetter(p -> p.hasUsedGhostVote),
            Codec.BOOL.optionalFieldOf("is_nominated", false).forGetter(p -> p.isNominated)
        ).apply(instance, ClocktowerPlayer::new));


    public static final StreamCodec<FriendlyByteBuf, ClocktowerPlayer> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, p -> p.uuid,
            ByteBufCodecs.STRING_UTF8, p -> p.name,
            ByteBufCodecs.STRING_UTF8, p -> p.roleName,
            Alignment.STREAM_CODEC, p -> p.alignment,
            ByteBufCodecs.STRING_UTF8, p -> p.linkedMinecraftUsername,
            ByteBufCodecs.BOOL, p -> p.isDead,
            ByteBufCodecs.BOOL, p -> p.hasUsedGhostVote,
            ByteBufCodecs.BOOL, p -> p.isNominated,
            ClocktowerPlayer::new 
    );

}
