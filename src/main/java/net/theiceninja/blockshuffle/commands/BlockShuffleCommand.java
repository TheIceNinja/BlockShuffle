package net.theiceninja.blockshuffle.commands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.blockshuffle.game.Game;
import net.theiceninja.blockshuffle.game.states.ActiveGameState;
import net.theiceninja.blockshuffle.game.states.CountdownGameState;
import net.theiceninja.blockshuffle.game.states.DefaultGameState;
import net.theiceninja.blockshuffle.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BlockShuffleCommand implements CommandExecutor, TabCompleter {

    private final Game game;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.color("&#FF6E6EYou can't execute this command"));
            return true;
        }

        if (!player.hasPermission("blockshuffle.admin")) {
            player.sendMessage(ColorUtil.color("&#FF6E6EYou don't have permission to execute this command."));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ColorUtil.color("&#F3D346Usage: /blockshuffle <start|revive|stop|setspawn>"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> {
                if (game.getPlugin().getServer().getOnlinePlayers().size() <= 1) {
                    player.sendMessage(ColorUtil.color("&#FF706EYou can't start the game with 1 player."));
                    return true;
                }

                if (game.getSpawnLocation() == null) {
                    player.sendMessage(ColorUtil.color("&#FF706EYou need to set the spawn."));
                    return true;
                }

                if (!(game.getState() instanceof DefaultGameState)) {
                    player.sendMessage(ColorUtil.color("&#FF706EYou can't start another game while the game is still running."));
                    return true;
                }

                game.setState(new CountdownGameState());
                player.sendMessage(ColorUtil.color("&#23FF00You successfully started the game."));
            }

            case "stop" -> {
                if (!(game.getState() instanceof ActiveGameState)) {
                    player.sendMessage(ColorUtil.color("&#FF706EYou can't stop the game if its not running."));
                    return true;
                }

                game.setState(new DefaultGameState());
                player.sendMessage(ColorUtil.color("&#23FF00You successfully stopped the game."));
            }

            case "revive" -> {
                if (!(game.getState() instanceof ActiveGameState)) {
                    player.sendMessage(ColorUtil.color("&#FF706EYou can't do that if the game doesn't running."));
                    return true;
                }

                if (args.length == 1) {
                    player.sendMessage(ColorUtil.color("&#FF706EYou need to provide a player."));
                    return true;
                }

                Player target = game.getPlugin().getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ColorUtil.color("&#FF706EThe player was not found, try again."));
                    return true;
                }

                game.revivePlayer(target);
            }

            case "setspawn" -> {
                game.setSpawnLocation(player.getLocation());
                player.sendMessage(ColorUtil.color("&#77FB16You successfully set the spawn."));
            }

            default -> player.sendMessage(ColorUtil.color("&#F3D346Usage: /blockshuffle <start|revive|stop|setspawn>"));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length != 1) return null;

        List<String> completion = new ArrayList<>();
        completion.add("setspawn");
        completion.add("revive");
        completion.add("stop");
        completion.add("start");

        return completion.stream().filter(arg -> arg.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
    }
}
