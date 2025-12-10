package online.refract;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;



public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(Commands.literal("clocktower")
        .requires(source -> source.hasPermission(2))

        .then(Commands.literal("role")
            .then(Commands.argument("target", EntityArgument.player())
            .then(Commands.argument("role", StringArgumentType.word())
            .suggests((c, b) -> net.minecraft.commands.SharedSuggestionProvider.suggest(
                List.of("player", "spectator", "storyteller"), b))
            .executes(context -> {
                ServerPlayer target = EntityArgument.getPlayer(context, "target");
                String role = StringArgumentType.getString(context, "role");

                if (role.equals("spectator")) {
                    target.setGameMode(GameType.SPECTATOR);
                } else {
                    target.setGameMode(GameType.ADVENTURE);
                }
                return 1;
            }))))
        
            
        .then(Commands.literal("playercount")
            .then(Commands.argument("playercount", IntegerArgumentType.integer())
            .executes(context -> {
                Sttk.SERVER_PLAYER_COUNT = IntegerArgumentType.getInteger(context, "playercount");
                return 1;
            })))
    );
}

}