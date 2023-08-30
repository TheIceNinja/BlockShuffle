package net.theiceninja.blockshuffle.game.managers;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.title.Title;
import net.theiceninja.blockshuffle.game.Game;
import net.theiceninja.blockshuffle.utils.ColorUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class PlayerTaskHandler {

    private final Game game;
    private final Map<UUID, Material> playersTask = new HashMap<>();

    public void chooseRandomBlock(final @NotNull Player player) {
        List<Material> blocks = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .filter(material -> !(material == Material.BARRIER || material == Material.COMMAND_BLOCK))
                .toList();
        playersTask.put(player.getUniqueId(), blocks.get(ThreadLocalRandom.current().nextInt(0, blocks.size())));

        player.sendMessage(ColorUtil.color("&#FFEA69You need to stand on&8: &#8AFB11&l" + playersTask.get(player.getUniqueId())));
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.showTitle(Title.title(
                ColorUtil.color("&#FDE67C&lBlock&#FD7CDE&lShuffle"),
                ColorUtil.color("&#FFEA69You need to stand on&8: &#8AFB11&l" + playersTask.get(player.getUniqueId()))
        ));
    }

    public void giveTasks() {
        for (UUID playerUUID : game.getPlayers()) {
            Player player = game.getPlugin().getServer().getPlayer(playerUUID);
            if (player == null) continue;

            chooseRandomBlock(player);
        }
    }

    public void finishTask(final @NotNull Player player) {
        if (!playersTask.containsKey(player.getUniqueId())) return;

        playersTask.remove(player.getUniqueId());
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        game.sendMessage("&#2AE636" + player.getName() + " &#F6ED63found his block.");
    }

    public Material getPlayersTask(final @NotNull Player player) {
        return playersTask.get(player.getUniqueId());
    }

    public boolean isBlockTask(final @NotNull Player player) {
        return player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == getPlayersTask(player);
    }

    public void clearTasks() {
        playersTask.clear();
    }

    public Map<UUID, Material> getPlayersTask() {
        return playersTask;
    }
}
