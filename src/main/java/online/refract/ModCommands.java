package online.refract;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.List;



public class ModCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(CommandManager.literal("clocktower")
        .requires(source -> source.hasPermissionLevel(2))

        .then(CommandManager.literal("role")
            .then(CommandManager.argument("target", EntityArgumentType.player())
            .then(CommandManager.argument("role", StringArgumentType.word())
            .suggests((c, b) -> net.minecraft.command.CommandSource.suggestMatching(
                List.of("player", "spectator", "storyteller"), b))
            .executes(context -> {
                ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                String role = StringArgumentType.getString(context, "role");

                if (role.equals("spectator")) {
                    target.changeGameMode(GameMode.SPECTATOR);
                } else {
                    target.changeGameMode(GameMode.ADVENTURE);
                }
                return 1;
            }))))
        
            
        .then(CommandManager.literal("playercount")
            .then(CommandManager.argument("playercount", IntegerArgumentType.integer())
            .executes(context -> {
                Sttk.SERVER_PLAYER_COUNT = IntegerArgumentType.getInteger(context, "playercount");
                return 1;
            })))
    );
}

}