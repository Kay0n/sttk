package online.refract.game.state;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import online.refract.game.state.Enums.Alignment;



public record ClocktowerPlayer(
    String name,
    Alignment alignment,
    String roleName,
    String alignedIconUrl,
    @Nullable String linkedMinecraftUsername,
    boolean isDead,
    boolean hasUsedGhostVote,
    boolean isNominated
){
    
    public ClocktowerPlayer(String name) {
        this(
            name,
            Alignment.GOOD,
            "",
            "",
            null,
            false,
            false,
            false
        );
    }



    public boolean isLinked(){
        return (linkedMinecraftUsername != null);
    }



    public ClocktowerPlayer withLinkedMinecraftUsername(String username) {
        return new ClocktowerPlayer(
            name,
            alignment,
            roleName,
            alignedIconUrl,
            username, 
            isDead,
            hasUsedGhostVote,
            isNominated
        );
    }



    public static final Codec<ClocktowerPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(p -> p.name),
        Alignment.CODEC.fieldOf("alignment").forGetter(p -> p.alignment),
        Codec.STRING.fieldOf("role_name").forGetter(p -> p.roleName),
        Codec.STRING.fieldOf("aligned_icon_url").forGetter(p -> p.alignedIconUrl),
        Codec.STRING.optionalFieldOf("linked_minecraft_username").forGetter(p -> Optional.ofNullable(p.linkedMinecraftUsername)),
        
        Codec.BOOL.fieldOf("is_dead").forGetter(p -> p.isDead),
        Codec.BOOL.fieldOf("has_used_ghost_vote").forGetter(p -> p.hasUsedGhostVote),
        Codec.BOOL.fieldOf("is_nominated").forGetter(p -> p.isNominated)
    ).apply(instance, 
        (name, align, roleName, alignedIconUrl, linkedName, dead, gv, nom) ->
        new ClocktowerPlayer(name, align, roleName, alignedIconUrl, linkedName.orElse(null), dead, gv, nom)
    ));



    public static final StreamCodec<RegistryFriendlyByteBuf, ClocktowerPlayer> STREAM_CODEC = StreamCodec.of(
        (buf, player) -> {
            ByteBufCodecs.STRING_UTF8.encode(buf, player.name);
            Alignment.STREAM_CODEC.encode(buf, player.alignment);
            ByteBufCodecs.STRING_UTF8.encode(buf, player.roleName);
            ByteBufCodecs.STRING_UTF8.encode(buf, player.alignedIconUrl);
            ByteBufCodecs.BOOL.encode(buf, player.isLinked());
            if (player.isLinked()) {
                ByteBufCodecs.STRING_UTF8.encode(buf, player.linkedMinecraftUsername);
            }
            ByteBufCodecs.BOOL.encode(buf, player.isDead);
            ByteBufCodecs.BOOL.encode(buf, player.hasUsedGhostVote);
            ByteBufCodecs.BOOL.encode(buf, player.isNominated);
        },
        buf -> new ClocktowerPlayer(
            ByteBufCodecs.STRING_UTF8.decode(buf),
            Alignment.STREAM_CODEC.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.BOOL.decode(buf) ? ByteBufCodecs.STRING_UTF8.decode(buf) : null,
            ByteBufCodecs.BOOL.decode(buf),
            ByteBufCodecs.BOOL.decode(buf),
            ByteBufCodecs.BOOL.decode(buf)
        )
    );

}
