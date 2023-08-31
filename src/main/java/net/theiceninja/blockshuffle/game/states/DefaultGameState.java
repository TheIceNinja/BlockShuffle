package net.theiceninja.blockshuffle.game.states;

import net.theiceninja.blockshuffle.BlockShufflePlugin;
import net.theiceninja.blockshuffle.game.GameState;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DefaultGameState extends GameState {

    @Override
    public void onEnable(BlockShufflePlugin plugin) {
        super.onEnable(plugin);

        plugin.getServer().getOnlinePlayers().forEach(getGame()::setScoreboard);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        getGame().cleanupPlayer(event.getPlayer());
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
        getGame().setScoreboard(event.getPlayer());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
    }

}
