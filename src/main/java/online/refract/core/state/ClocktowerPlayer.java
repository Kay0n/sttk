package online.refract;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;



public class ClocktowerPlayer {

    public UUID uuid;
    public String name;

    public boolean isDead;
    public boolean hasUsedGhostVote;
    public boolean isNominated;
    public boolean isOnBlock;


    public ClocktowerPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.isDead = false;
        this.hasUsedGhostVote = false;
        this.isNominated = false;
        this.isOnBlock = false;
    }


    public ClocktowerPlayer(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.isDead = false;
        this.hasUsedGhostVote = false;
        this.isNominated = false;
        this.isOnBlock = false;
    }


    public ClocktowerPlayer(
        UUID uuid,
        String name,
        boolean isDead,
        boolean hasUsedGhostVote,
        boolean isNominated,
        boolean isOnBlock
    ) {
        this.uuid = uuid;
        this.name = name;
        this.isDead = isDead;
        this.hasUsedGhostVote = hasUsedGhostVote;
        this.isNominated = isNominated;
        this.isOnBlock = isOnBlock;
    }


    public static final Codec<ClocktowerPlayer> CODEC =
        RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(p -> p.uuid),
            Codec.STRING.fieldOf("name").forGetter(p -> p.name),

            Codec.BOOL.optionalFieldOf("is_dead", false).forGetter(p -> p.isDead),
            Codec.BOOL.optionalFieldOf("has_used_ghost_vote", false).forGetter(p -> p.hasUsedGhostVote),
            Codec.BOOL.optionalFieldOf("is_nominated", false).forGetter(p -> p.isNominated),
            Codec.BOOL.optionalFieldOf("is_on_block", false).forGetter(p -> p.isOnBlock)
        ).apply(instance, ClocktowerPlayer::new));

        
    public static final StreamCodec<FriendlyByteBuf, ClocktowerPlayer> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, p -> p.uuid,
            ByteBufCodecs.STRING_UTF8, p -> p.name,
            ByteBufCodecs.BOOL, p -> p.isDead,
            ByteBufCodecs.BOOL, p -> p.hasUsedGhostVote,
            ByteBufCodecs.BOOL, p -> p.isNominated,
            ByteBufCodecs.BOOL, p -> p.isOnBlock,
            ClocktowerPlayer::new 
    );

}
