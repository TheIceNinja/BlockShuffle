package net.theiceninja.blockshuffle.game.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.theiceninja.blockshuffle.game.Game;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@RequiredArgsConstructor
public class GameTickTask extends BukkitRunnable {

    @Setter @Getter private int timeLeftUntilRoundOver = (60 * 5);
    private final Game game;

    @Override
    public void run() {
        if (timeLeftUntilRoundOver <= 0) {
            startNewRound();
            return;
        }

        for (UUID playerUUID : game.getPlayers()) {
            Player player = game.getPlugin().getServer().getPlayer(playerUUID);
            if (player == null) continue;

            if (game.getPlayerTaskHandler().isBlockTask(player)) {
                game.getPlayerTaskHandler().finishTask(player);

                if (game.getPlayerTaskHandler().getPlayersTask().isEmpty()) {
                    startNewRound();
                    return;
                }
            }
        }

        if (timeLeftUntilRoundOver <= 10) {
            game.sendActionBar("&#FF4646Round ends in&8: &#ABEEFF" + timeLeftUntilRoundOver);
            game.playSound(Sound.BLOCK_NOTE_BLOCK_FLUTE);
        }

        game.updateScoreboard();
        timeLeftUntilRoundOver--;
    }

    private void startNewRound() {
        if (!game.getPlayerTaskHandler().getPlayersTask().isEmpty()) {
            if (game.getPlayerTaskHandler().getPlayersTask().size() == game.getPlayers().size()) {
                game.sendMessage("&#FC5E5ENobody found his block, moving to the next round!");
                game.playSound(Sound.ENTITY_VILLAGER_NO);
            } else
                for (UUID playerUUID : game.getPlayerTaskHandler().getPlayersTask().keySet()) {
                    Player player = game.getPlugin().getServer().getPlayer(playerUUID);
                    if (player == null) continue;

                    game.removePlayer(player);
                }
        }

        game.getPlayerTaskHandler().giveTasks();
        game.setRound(game.getRound() + 1);

        timeLeftUntilRoundOver = (60 * 5);
    }
}
