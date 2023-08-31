package net.theiceninja.blockshuffle.game.managers;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.title.Title;
import net.theiceninja.blockshuffle.game.Game;
import net.theiceninja.blockshuffle.utils.ColorUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class PlayerTaskHandler {

    private final Game game;
    private final Map<UUID, Material> playersTask = new HashMap<>();

    public void chooseRandomBlock(final @NotNull Player player) {
        List<Material> blocks = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .filter(material -> !(
                        material == Material.CHAIN_COMMAND_BLOCK ||
                                material == Material.BARRIER ||
                                material == Material.COMMAND_BLOCK ||
                                material == Material.VOID_AIR ||
                                material == Material.STRUCTURE_BLOCK ||
                                material == Material.STRUCTURE_VOID
                ))
                .toList();

        Material material = blocks.get(ThreadLocalRandom.current().nextInt(0, blocks.size()));
        playersTask.put(player.getUniqueId(), material);

        player.sendMessage(ColorUtil.color(
                "&#FFEA69You need to stand on&8: &#8AFB11&l" + material
        ).clickEvent((ClickEvent.clickEvent(
                ClickEvent.Action.OPEN_URL,
                "https://www.google.com/search?q=minecraft+" + material + "&rlz=1C1KNTJ_enIL1065IL1065&oq=minecraft+anvil&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIHCAEQABiABDIHCAIQABiABDIHCAMQABiABDIHCAQQABiABDIHCAUQABiABDIHCAYQABiABDIHCAcQABiABDIHCAgQABiABDIHCAkQABiABNIBCDYzNTJqMGo3qAIAsAIA&sourceid=chrome&ie=UTF-8"
        ))).hoverEvent(ColorUtil.color("&#FAE06CClick to see that block!")));

        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.showTitle(Title.title(
                ColorUtil.color("&#FDE67C&lBlock&#FD7CDE&lShuffle"),
                ColorUtil.color("&#FFEA69You need to stand on&8: &#8AFB11&l" + material)
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
