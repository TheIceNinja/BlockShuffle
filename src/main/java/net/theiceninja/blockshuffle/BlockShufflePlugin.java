package net.theiceninja.blockshuffle;

import net.theiceninja.blockshuffle.commands.BlockShuffleCommand;
import net.theiceninja.blockshuffle.game.Game;
import net.theiceninja.blockshuffle.game.states.CommonGameListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockShufflePlugin extends JavaPlugin {

    private Game game;

    @Override
    public void onEnable() {
        init();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CommonGameListener(game), this);
    }

    private void registerCommands() {
        BlockShuffleCommand blockShuffleCommand = new BlockShuffleCommand(game);
        getCommand("blockshuffle").setExecutor(blockShuffleCommand);
        getCommand("blockshuffle").setTabCompleter(blockShuffleCommand);
    }

    private void init() {
        game = new Game(this);

        registerListeners();
        registerCommands();
    }
}
