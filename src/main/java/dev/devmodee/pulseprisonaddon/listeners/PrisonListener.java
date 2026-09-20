package dev.devmodee.pulseprisonaddon.listeners;

import dev.aeros.pulseprison.api.PulsePrisonAPI;
import dev.aeros.pulseprison.api.events.MarketPriceChangeEvent;
import dev.aeros.pulseprison.api.events.MineResetEvent;
import dev.aeros.pulseprison.api.events.PlayerLevelUpEvent;
import dev.aeros.pulseprison.api.events.PlayerRebirthEvent;
import dev.aeros.pulseprison.api.events.RobotProduceEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PrisonListener implements Listener {

    private final PulsePrisonAPI prison;

    public PrisonListener(PulsePrisonAPI prison) {
        this.prison = prison;
    }

    /**
     * Cancel a player's rebirth if they want to go past level 10 without a specific permission.
     * This shows how we can intervene in the core's internal systems.
     */
    @EventHandler
    public void onRebirth(PlayerRebirthEvent event) {
        Player player = event.getPlayer();
        
        if (event.getNewRebirth() > 10 && !player.hasPermission("addon.rebirth.extended")) {
            event.setCancelled(true);
            player.sendMessage("§cYou need the Elite rank to rebirth past level 10.");
        }
    }

    /**
     * React to a player leveling up in any track (levels.yml).
     * Great for shooting fireworks or playing custom sounds!
     */
    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        Player player = event.getPlayer();
        player.sendTitle("§aLevel Up!", "§7Track: " + event.getTrack() + " §8| §7Level: " + event.getNewLevel(), 10, 40, 10);
    }

    /**
     * React to a mine reset.
     * Useful when we want to despawn custom entities, notify users,
     * or prevent the reset under certain conditions.
     */
    @EventHandler
    public void onMineReset(MineResetEvent event) {
        // In this simple example, we don't cancel it, we just log or notify.
        // It can be cancelled using: event.setCancelled(true);
        // If we want to know which engine is resetting: event.getEngine()
    }

    /**
     * Intercept what a robot generates upon collection.
     * Useful if you want to send collections to your own shop or external bank.
     */
    @EventHandler
    public void onRobotProduce(RobotProduceEvent event) {
        // We get how many blocks this robot generated.
        long generated = event.getBlocksProduced();
        
        // The event allows us to change the production BEFORE it finishes registering
        // event.setBlocksProduced(generated * 2); // (e.g., a global double robot production event)
    }

    /**
     * Listen to the dynamic market fluctuating.
     * This event triggers when a block price is recalculated.
     */
    @EventHandler
    public void onMarketMove(MarketPriceChangeEvent event) {
        double delta = (event.getNewMultiplier() - event.getPreviousMultiplier()) * 100.0D;
        
        // Let's log heavily crashing or surging items.
        if (Math.abs(delta) > 40.0D) {
            System.out.println("[PulsePrisonAddon] Huge market shift for " + event.getMaterial() + "! Delta: " + delta + "%");
        }
    }
}
