package online.refract.game.state;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TimerState(
    boolean isRunning,
    boolean isStopwatch,
    int currentTime,
    int targetTime,
    @Nullable Long startTimestamp
) {

    public static final TimerState EMPTY = new TimerState(false, false, 0, 0, null);

    private boolean isStarted() {
        return startTimestamp != null;
    }

    public static final Codec<TimerState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("isRunning", false).forGetter(TimerState::isRunning),
        Codec.BOOL.optionalFieldOf("isStopwatch", false).forGetter(TimerState::isStopwatch),
        Codec.INT.optionalFieldOf("currentTime", 0).forGetter(TimerState::currentTime),
        Codec.INT.optionalFieldOf("targetTime", 0).forGetter(TimerState::targetTime),
        Codec.LONG.optionalFieldOf("startTimestamp").forGetter(t -> Optional.ofNullable(t.startTimestamp()))
    ).apply(instance,
        (running, stopwatch, current, target, timestamp) ->
            new TimerState(running, stopwatch, current, target, timestamp.orElse(null))
    ));

    public static final StreamCodec<RegistryFriendlyByteBuf, TimerState> STREAM_CODEC = StreamCodec.of(
        (buf, timer) -> {
            ByteBufCodecs.BOOL.encode(buf, timer.isRunning());
            ByteBufCodecs.BOOL.encode(buf, timer.isStopwatch());
            ByteBufCodecs.VAR_INT.encode(buf, timer.currentTime());
            ByteBufCodecs.VAR_INT.encode(buf, timer.targetTime());
            ByteBufCodecs.BOOL.encode(buf, timer.isStarted());
            if (timer.isStarted()) {
                ByteBufCodecs.VAR_LONG.encode(buf, timer.startTimestamp());
            }
        },
        buf -> new TimerState(
            ByteBufCodecs.BOOL.decode(buf),
            ByteBufCodecs.BOOL.decode(buf),
            ByteBufCodecs.VAR_INT.decode(buf),
            ByteBufCodecs.VAR_INT.decode(buf),
            ByteBufCodecs.BOOL.decode(buf) ? ByteBufCodecs.VAR_LONG.decode(buf) : null
        )
    );
}