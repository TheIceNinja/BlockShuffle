package net.theiceninja.blockshuffle.game.states;

import lombok.Getter;
import net.theiceninja.blockshuffle.BlockShufflePlugin;
import net.theiceninja.blockshuffle.game.GameState;
import net.theiceninja.blockshuffle.game.tasks.CountdownTask;

@Getter
public class CountdownGameState extends GameState {

    private CountdownTask countdownTask;

    @Override
    public void onEnable(BlockShufflePlugin plugin) {
        super.onEnable(plugin);

        plugin.getServer().getOnlinePlayers().forEach(getGame()::addPlayer);

        if (countdownTask != null) countdownTask.cancel();

        countdownTask = new CountdownTask(getGame());
        countdownTask.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (countdownTask != null)
            countdownTask.cancel();
    }
}
