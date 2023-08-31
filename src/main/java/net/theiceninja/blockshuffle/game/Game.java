package net.theiceninja.blockshuffle.game;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.title.Title;
import net.theiceninja.blockshuffle.BlockShufflePlugin;
import net.theiceninja.blockshuffle.game.managers.PlayerTaskHandler;
import net.theiceninja.blockshuffle.game.states.ActiveGameState;
import net.theiceninja.blockshuffle.game.states.CountdownGameState;
import net.theiceninja.blockshuffle.game.states.DefaultGameState;
import net.theiceninja.blockshuffle.utils.ColorUtil;
import net.theiceninja.blockshuffle.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class Game {

    @Setter private int round;
    @Setter private Location spawnLocation;
    private GameState state;

    private final Set<UUID> players = new HashSet<>();
    private final Set<UUID> spectators = new HashSet<>();

    private final BlockShufflePlugin plugin;
    private final PlayerTaskHandler playerTaskHandler;

    public Game(BlockShufflePlugin plugin) {
        this.plugin = plugin;
        playerTaskHandler = new PlayerTaskHandler(this);
        setState(new DefaultGameState());
    }

    public void setState(GameState state) {
        if (this.state != null) this.state.onDisable();

        this.state = state;
        state.setGame(this);
        state.onEnable(plugin);
    }

    public void addPlayer(final @NotNull Player player) {
        cleanupPlayer(player, true, true);
        players.add(player.getUniqueId());
    }

    public void removePlayer(final @NotNull Player player) {
        if (isSpectating(player)) return;
        if (isPlaying(player)) players.remove(player.getUniqueId());

        cleanupPlayer(player, false, true);
        spectators.add(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);
        sendMessage("&#FFD56EThe player &#FF706E" + player.getName() + " &#FFD56Elost the round");
        playSound(Sound.ENTITY_BLAZE_HURT);

        if (players.size() <= 1) {
            Player winner = plugin.getServer().getPlayer(players.stream().toList().get(0));
            String winnerName = (winner == null ? "&#FF806Enot found" : winner.getName());
            sendMessage("&#6EFF96" + winnerName + " &#FFD56Ewon the game!");
            sendTitle("&#6EFF96" + winnerName + " &#FFD56Ewon the game!");

            setState(new DefaultGameState());
        }
    }

    public void revivePlayer(final @NotNull Player player) {
        addPlayer(player);
        spectators.remove(player.getUniqueId());
        sendMessage("&#FBE36DThe player &#77FB16" + player.getName() + " &#FBE36returned to life.");
    }

    public void sendMessage(@NotNull String str) {
        for (UUID playerUUID : players) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            player.sendMessage(ColorUtil.color("&#FDE67C&lBlock&#FD7CDE&lShuffle &8» " + str));
        }

        for (UUID playerUUID : spectators) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            player.sendMessage(ColorUtil.color("&#FDE67C&lBlock&#FD7CDE&lShuffle &8» " + str));
        }
    }

    public void playSound(@NotNull Sound sound) {
        for (UUID playerUUID : players) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            player.playSound(player, sound, 1, 1);
        }

        for (UUID playerUUID : spectators) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            player.playSound(player, sound, 1, 1);
        }
    }

    public void sendTitle(@NotNull String str) {
        for (UUID playerUUID : players) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            player.showTitle(Title.title(
                    ColorUtil.color("&#FDE67C&lBlock&#FD7CDE&lShuffle"),
                    ColorUtil.color(str),
                    Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(3),
                            Duration.ofSeconds(0)
                    )
            ));
        }

        for (UUID playerUUID : spectators) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            player.showTitle(Title.title(
                    ColorUtil.color("&#FDE67C&lBlock&#FD7CDE&lShuffle"),
                    ColorUtil.color(str),
                    Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(3),
                            Duration.ofSeconds(0)
                    )
            ));
        }
    }

    public void sendActionBar(@NotNull String str) {
        for (UUID playerUUID : players) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            player.sendActionBar(ColorUtil.color(str));
        }

        for (UUID playerUUID : spectators) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            player.sendActionBar(ColorUtil.color(str));
        }
    }

    public void setScoreboard(final @NotNull Player player) {
        ScoreboardManager manager = plugin.getServer().getScoreboardManager();

        Scoreboard scoreboard = manager.getNewScoreboard();
        List<String> scoreboardLines = new ArrayList<>();
        Objective objective = scoreboard.registerNewObjective(
                "ice",
                Criteria.DUMMY,
                ColorUtil.color("&#FDE67C&lBlock&#FD7CDE&lShuffle &7| &fblock shuffle")
        );

        scoreboardLines.add("&r");
        if (state instanceof DefaultGameState) {
            scoreboardLines.add("&fName&8: &#F6DE63" + player.getName());
            scoreboardLines.add("&fState&8: &#FF746EOffline");
        } else if (state instanceof CountdownGameState countdownGameState) {
            scoreboardLines.add("&fGame starting in&8: &#6ED7FF" + countdownGameState.getCountdownTask().getTimeLeft());
            scoreboardLines.add("&fAlive players&8: &#6EFFA2" + players.size());
        } else if (state instanceof ActiveGameState activeGameState) {
            scoreboardLines.add("&fRound&8: &#FE828A" + round);
            scoreboardLines.add("&r");
            scoreboardLines.add("&fYour block&8: &#8AFB11" + (playerTaskHandler.getPlayersTask(player) == null ? "&#A9FF94found" : StringUtil.formatMaterialName(playerTaskHandler.getPlayersTask(player))));
            scoreboardLines.add("&fTime left&8: " + StringUtil.formatTimer(activeGameState.getGameTickTask().getTimeLeftUntilRoundOver()));
            scoreboardLines.add("&r");
            scoreboardLines.add("&fAlive players&8: &#6EFFA2" + players.size());
        }

        scoreboardLines.add("&r");
        scoreboardLines.add("&#8AEFFDplay.iceninja.us.to");

        for (int i = 0; i < scoreboardLines.size(); i++) {
            Team team = scoreboard.getTeam("line" + scoreboardLines.get(i));
            if (team == null)
                team = scoreboard.registerNewTeam("line" + scoreboardLines.get(i));

            String lineEntry = ChatColor.translateAlternateColorCodes('&', "&" + i);

            team.addEntry(lineEntry);
            team.prefix(ColorUtil.color(scoreboardLines.get(i)));
            objective.getScore(lineEntry).setScore(scoreboardLines.size() - i);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard() {
        for (UUID playerUUID : players) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            setScoreboard(player);
        }

        for (UUID playerUUID : spectators) {
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player == null) continue;

            setScoreboard(player);
        }
    }

    public void cleanupPlayer(final @NotNull Player player, boolean setGameMode, boolean teleport) {
        if (setGameMode)
            player.setGameMode(GameMode.SURVIVAL);
        if (teleport)
            player.teleport(spawnLocation);

        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGlowing(false);
        player.setExp(0);
        player.setLevel(0);
    }

    public boolean isPlaying(final @NotNull Player player) {
        return players.contains(player.getUniqueId());
    }

    public boolean isSpectating(final @NotNull Player player) {
        return spectators.contains(player.getUniqueId());
    }
}
