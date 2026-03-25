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
    Alignment alignment,
    String iconUrl,
    String abilityText,
    String edition,
    String firstNightReminder,
    String otherNightReminder
) {
    public static final Codec<ClocktowerRole> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(ClocktowerRole::name),
        RoleType.CODEC.optionalFieldOf("type", RoleType.TOWNSFOLK).forGetter(ClocktowerRole::type),
        Alignment.CODEC.optionalFieldOf("alignment", Alignment.UNKNOWN).forGetter(ClocktowerRole::alignment),
        Codec.STRING.fieldOf("icon").forGetter(ClocktowerRole::iconUrl),
        Codec.STRING.fieldOf("ability").forGetter(ClocktowerRole::abilityText),
        Codec.STRING.optionalFieldOf("edition", "").forGetter(ClocktowerRole::edition),
        Codec.STRING.optionalFieldOf("first_night_reminder", "").forGetter(ClocktowerRole::firstNightReminder),
        Codec.STRING.optionalFieldOf("other_night_reminder", "").forGetter(ClocktowerRole::otherNightReminder)
    ).apply(instance, ClocktowerRole::new));
    
    // public static final StreamCodec<RegistryFriendlyByteBuf, ClocktowerRole> STREAM_CODEC = StreamCodec.composite(
    //     ByteBufCodecs.STRING_UTF8, ClocktowerRole::name,
    //     RoleType.STREAM_CODEC, ClocktowerRole::type,
    //     Alignment.STREAM_CODEC, ClocktowerRole::alignment,
    //     ByteBufCodecs.STRING_UTF8, ClocktowerRole::iconUrl,
    //     ByteBufCodecs.STRING_UTF8, ClocktowerRole::abilityText,
    //     ByteBufCodecs.STRING_UTF8, ClocktowerRole::edition,
    //     ByteBufCodecs.STRING_UTF8, ClocktowerRole::firstNightReminder,
    //     ByteBufCodecs.STRING_UTF8, ClocktowerRole::otherNightReminder,
    //     ClocktowerRole::new
    // );

    // uses StreamCodec.of instead of composite as composite has 6 a argument limit 
    public static final StreamCodec<RegistryFriendlyByteBuf, ClocktowerRole> STREAM_CODEC = StreamCodec.of(
    (buf, role) -> {
        ByteBufCodecs.STRING_UTF8.encode(buf, role.name());
        RoleType.STREAM_CODEC.encode(buf, role.type());
        Alignment.STREAM_CODEC.encode(buf, role.alignment());
        ByteBufCodecs.STRING_UTF8.encode(buf, role.iconUrl());
        ByteBufCodecs.STRING_UTF8.encode(buf, role.abilityText());
        ByteBufCodecs.STRING_UTF8.encode(buf, role.edition());
        ByteBufCodecs.STRING_UTF8.encode(buf, role.firstNightReminder());
        ByteBufCodecs.STRING_UTF8.encode(buf, role.otherNightReminder());
    },
    buf -> new ClocktowerRole(
        ByteBufCodecs.STRING_UTF8.decode(buf),
        RoleType.STREAM_CODEC.decode(buf),
        Alignment.STREAM_CODEC.decode(buf),
        ByteBufCodecs.STRING_UTF8.decode(buf),
        ByteBufCodecs.STRING_UTF8.decode(buf),
        ByteBufCodecs.STRING_UTF8.decode(buf),
        ByteBufCodecs.STRING_UTF8.decode(buf),
        ByteBufCodecs.STRING_UTF8.decode(buf)
    )
);
}
