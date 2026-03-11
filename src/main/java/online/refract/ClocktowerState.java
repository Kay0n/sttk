package online.refract;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable; 

public class ClocktowerState {

    public List<ClocktowerPlayer> players;
    public boolean isVoteActive;
    public TimeOfDay timeOfDay;


    public ClocktowerState(List<ClocktowerPlayer> players, boolean isVoteActive, TimeOfDay timeOfDay){
        this.players = new ArrayList<>(players); // wrap to keep mutable
        this.isVoteActive = isVoteActive;
        this.timeOfDay = timeOfDay;
    }


    public static final Codec<ClocktowerState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.list(ClocktowerPlayer.CODEC).fieldOf("players").forGetter(s -> s.players),
        Codec.BOOL.fieldOf("is_vote_active").forGetter(s -> s.isVoteActive),
        TimeOfDay.CODEC.fieldOf("time_of_day").forGetter(s -> s.timeOfDay)
    ).apply(instance, ClocktowerState::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, ClocktowerState> STREAM_CODEC = StreamCodec.composite(
        ClocktowerPlayer.STREAM_CODEC.apply(ByteBufCodecs.list()), 
        s -> s.players,
        // dummy state for client, only care about players
        (players) -> new ClocktowerState(players, false, TimeOfDay.DAY)
    );


    public enum TimeOfDay implements StringRepresentable {
        DAY("day"),
        EVENING("evening"),
        NIGHT("night");

        public static final Codec<TimeOfDay> CODEC = StringRepresentable.fromEnum(TimeOfDay::values);
        
        private final String name;

        TimeOfDay(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}


//  curl -N "https://botc.games/api/town/stream/test?password=pass"