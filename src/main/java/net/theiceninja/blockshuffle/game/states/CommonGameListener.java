package net.theiceninja.blockshuffle.game.states;

import lombok.RequiredArgsConstructor;
import net.theiceninja.blockshuffle.game.Game;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class CommonGameListener implements Listener {

    private final Game game;

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (game.getState() instanceof ActiveGameState) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent event) {
        if (game.getState() instanceof ActiveGameState) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        if (game.getState() instanceof ActiveGameState) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if (game.getState() instanceof ActiveGameState) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (game.getState() instanceof ActiveGameState) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if (block.getType().equals(Material.CHEST))
            event.setCancelled(true);
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (game.getState() instanceof ActiveGameState) return;

        event.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler
    private void onItemPickup(EntityPickupItemEvent event) {
        if (game.getState() instanceof ActiveGameState) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        if (game.getState() instanceof DefaultGameState) return;

        game.removePlayer(event.getPlayer());
        event.joinMessage(null);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (game.getState() instanceof DefaultGameState) return;

        game.removePlayer(event.getPlayer());
        event.quitMessage(null);
    }
}
