package net.theiceninja.blockshuffle.game.tasks;

import lombok.Getter;
import lombok.Setter;
import net.theiceninja.blockshuffle.game.Game;
import net.theiceninja.blockshuffle.game.handlers.PlayerTaskHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GameTickTask extends BukkitRunnable {

    @Setter @Getter private int timeLeftUntilRoundOver = (60 * 5);
    private final Game game;
    private final PlayerTaskHandler playerTaskHandler;

    public GameTickTask(Game game) {
        this.game = game;
        this.playerTaskHandler = game.getPlayerTaskHandler();
    }

    @Override
    public void run() {
        if (timeLeftUntilRoundOver <= 0) {
            startNewRound();
            return;
        }

        for (UUID playerUUID : game.getPlayers()) {
            Player player = game.getPlugin().getServer().getPlayer(playerUUID);
            if (player == null) continue;

            if (playerTaskHandler.isBlockTask(player)) {
                playerTaskHandler.finishTask(player);

                if (playerTaskHandler.getPlayersTask().isEmpty()) {
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
        if (!playerTaskHandler.getPlayersTask().isEmpty()) {
            if (playerTaskHandler.getPlayersTask().size() == game.getPlayers().size()) {
                game.sendMessage("&#FC5E5ENobody found his block, moving to the next round!");
                game.playSound(Sound.ENTITY_VILLAGER_NO);
            } else
                for (UUID playerUUID : playerTaskHandler.getPlayersTask().keySet()) {
                    Player player = game.getPlugin().getServer().getPlayer(playerUUID);
                    if (player == null) continue;

                    game.removePlayer(player);
                }
        }

        playerTaskHandler.giveTasks();
        game.setRound(game.getRound() + 1);

        timeLeftUntilRoundOver = (60 * 5);
    }
}
