package online.refract.game.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import online.refract.game.state.Enums.Alignment;
import online.refract.game.state.Enums.RoleType;

public record ClocktowerRole(
    String name,
    RoleType type,
    Alignment defaultAlignment,
    String alignedIconUrl,
    String abilityText,
    String edition
) {
    public static final Codec<ClocktowerRole> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(ClocktowerRole::name),
        RoleType.CODEC.fieldOf("type").forGetter(ClocktowerRole::type),
        Alignment.CODEC.fieldOf("alignment").forGetter(ClocktowerRole::defaultAlignment),
        Codec.STRING.fieldOf("aligned_icon_url").forGetter(ClocktowerRole::alignedIconUrl),
        Codec.STRING.fieldOf("ability").forGetter(ClocktowerRole::abilityText),
        Codec.STRING.fieldOf("edition").forGetter(ClocktowerRole::edition)
    ).apply(instance, ClocktowerRole::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClocktowerRole> STREAM_CODEC = StreamCodec.of(
        (buf, role) -> {
            ByteBufCodecs.STRING_UTF8.encode(buf, role.name());
            RoleType.STREAM_CODEC.encode(buf, role.type());
            Alignment.STREAM_CODEC.encode(buf, role.defaultAlignment());
            ByteBufCodecs.STRING_UTF8.encode(buf, role.alignedIconUrl());
            ByteBufCodecs.STRING_UTF8.encode(buf, role.abilityText());
            ByteBufCodecs.STRING_UTF8.encode(buf, role.edition());
        },
        buf -> new ClocktowerRole(
            ByteBufCodecs.STRING_UTF8.decode(buf),
            RoleType.STREAM_CODEC.decode(buf),
            Alignment.STREAM_CODEC.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf)
        )
    );
}
