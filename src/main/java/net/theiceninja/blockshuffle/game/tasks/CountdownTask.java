package net.theiceninja.blockshuffle.game.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.theiceninja.blockshuffle.game.Game;
import net.theiceninja.blockshuffle.game.states.ActiveGameState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class CountdownTask extends BukkitRunnable {

    @Getter private int timeLeft = 10;
    private final Game game;

    @Override
    public void run() {
        if (timeLeft <= 0) {
            cancel();
            game.playSound(Sound.BLOCK_NOTE_BLOCK_BIT);
            game.setState(new ActiveGameState());
            return;
        }

        game.sendTitle("&#59F714The game will start in&8: &#97F2FE" + timeLeft);
        game.updateScoreboard();
        game.playSound(Sound.BLOCK_NOTE_BLOCK_BELL);

        timeLeft--;
    }
}
