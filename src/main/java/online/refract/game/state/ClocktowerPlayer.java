package online.refract.game.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import online.refract.game.state.Enums.Alignment;

import static online.refract.game.state.Enums.Alignment;


public record ClocktowerPlayer(
    String name,
    String roleName,
    Alignment alignment,
    String linkedMinecraftUsername,
    boolean isDead,
    boolean hasUsedGhostVote,
    boolean isNominated
) {
    public ClocktowerPlayer(String name) {
        this(
            name,
            null,
            Alignment.UNKNOWN,
            null,
            false,
            false,
            false
        );
    }

    public ClocktowerPlayer withLinkedMinecraftUsername(String username) {
        return new ClocktowerPlayer(
            name,
            roleName,
            alignment,
            username, 
            isDead,
            hasUsedGhostVote,
            isNominated
        );
    }





    public static final Codec<ClocktowerPlayer> CODEC =
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(p -> p.name),
            Codec.STRING.optionalFieldOf("role_name", null).forGetter(p -> p.roleName),
            Alignment.CODEC.optionalFieldOf("alignment", Alignment.UNKNOWN).forGetter(p -> p.alignment),
            Codec.STRING.optionalFieldOf("linked_minecraft_username", null).forGetter(p -> p.linkedMinecraftUsername),

            Codec.BOOL.optionalFieldOf("is_dead", false).forGetter(p -> p.isDead),
            Codec.BOOL.optionalFieldOf("has_used_ghost_vote", false).forGetter(p -> p.hasUsedGhostVote),
            Codec.BOOL.optionalFieldOf("is_nominated", false).forGetter(p -> p.isNominated)
        ).apply(instance, ClocktowerPlayer::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, ClocktowerPlayer> STREAM_CODEC = StreamCodec.composite(
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
