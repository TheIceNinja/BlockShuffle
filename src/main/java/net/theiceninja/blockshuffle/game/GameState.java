package net.theiceninja.blockshuffle.game;

import lombok.Getter;
import lombok.Setter;
import net.theiceninja.blockshuffle.BlockShufflePlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class GameState implements Listener {

    @Setter @Getter private Game game;

    public void onEnable(BlockShufflePlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

}
