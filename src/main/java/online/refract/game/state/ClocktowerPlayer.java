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
    String roleName,
    Alignment alignment,
    @Nullable String linkedMinecraftUsername,
    boolean isDead,
    boolean hasUsedGhostVote,
    boolean isNominated
){
    
    public ClocktowerPlayer(String name) {
        this(
            name,
            "",
            Alignment.GOOD,
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
            Codec.STRING.fieldOf("role_name").forGetter(p -> p.roleName),
            Alignment.CODEC.fieldOf("alignment").forGetter(p -> p.alignment),
            
            Codec.STRING.optionalFieldOf("linked_minecraft_username").forGetter(p -> Optional.ofNullable(p.linkedMinecraftUsername)),
            
            Codec.BOOL.fieldOf("is_dead").forGetter(p -> p.isDead),
            Codec.BOOL.fieldOf("has_used_ghost_vote").forGetter(p -> p.hasUsedGhostVote),
            Codec.BOOL.fieldOf("is_nominated").forGetter(p -> p.isNominated)
        ).apply(instance, 
            (name, role, align, linkedName, dead, gv, nom) ->
            new ClocktowerPlayer(name, role, align, linkedName.orElse(null), dead, gv, nom)
        ));



    public static final StreamCodec<RegistryFriendlyByteBuf, ClocktowerPlayer> STREAM_CODEC = StreamCodec.of(
        (buf, player) -> {
            ByteBufCodecs.STRING_UTF8.encode(buf, player.name);
            ByteBufCodecs.STRING_UTF8.encode(buf, player.roleName);
            Alignment.STREAM_CODEC.encode(buf, player.alignment);
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
            ByteBufCodecs.STRING_UTF8.decode(buf),
            Alignment.STREAM_CODEC.decode(buf),
            ByteBufCodecs.BOOL.decode(buf) ? ByteBufCodecs.STRING_UTF8.decode(buf) : null,
            ByteBufCodecs.BOOL.decode(buf),
            ByteBufCodecs.BOOL.decode(buf),
            ByteBufCodecs.BOOL.decode(buf)
        )
    );

}
