package net.theiceninja.blockshuffle.game.states;

import lombok.Getter;
import net.theiceninja.blockshuffle.BlockShufflePlugin;
import net.theiceninja.blockshuffle.game.GameState;
import net.theiceninja.blockshuffle.game.states.tasks.GameTickTask;
import net.theiceninja.blockshuffle.utils.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class ActiveGameState extends GameState {

    @Getter private GameTickTask gameTickTask;
    private BlockShufflePlugin plugin;

    @Override
    public void onEnable(BlockShufflePlugin plugin) {
        super.onEnable(plugin);

        this.plugin = plugin;

        getGame().sendMessage("&#97FEABThe game is now active!");
        if (gameTickTask != null) gameTickTask.cancel();

        gameTickTask = new GameTickTask(getGame());
        gameTickTask.runTaskTimer(plugin, 0, 20);
        getGame().getPlayerTaskHandler().giveTasks();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (gameTickTask != null)
            gameTickTask.cancel();

        for (UUID playerUUID : getGame().getPlayers()) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            getGame().cleanupPlayer(player, true, true);
        }

        for (UUID playerUUID : getGame().getSpectators()) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            getGame().cleanupPlayer(player, true, true);
        }

        getGame().getSpectators().clear();
        getGame().getPlayers().clear();
        getGame().getPlayerTaskHandler().clearTasks();
        getGame().setRound(0);
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.deathMessage(ColorUtil.color("&#FF9494" + event.getPlayer().getName() + " &#FFED94died!"));
    }
}
