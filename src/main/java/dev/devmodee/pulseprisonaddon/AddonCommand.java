package dev.devmodee.pulseprisonaddon;

import dev.aeros.pulseprison.api.PulsePrisonAPI;
import dev.aeros.pulseprison.market.MarketTrend;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Example command demonstrating how to fetch data from Progression,
 * Market, and Economy services in real-time.
 */
public class AddonCommand implements CommandExecutor {

    private final PulsePrisonAPI prison;

    public AddonCommand(PulsePrisonAPI prison) {
        this.prison = prison;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is for players only.");
            return true;
        }

        player.sendMessage("§8§m----------------------------------------");
        player.sendMessage("§6§lPulsePrison Addon §8| §fLive Data");
        
        // --- PROGRESSION SERVICE EXAMPLE ---
        // Fetch the player's rank, prestige, rebirth, and percentage progress to the next rank.
        String rank = prison.progression().getRank(player.getUniqueId());
        int prestige = prison.progression().getPrestige(player.getUniqueId());
        int rebirth = prison.progression().getRebirth(player.getUniqueId());
        double progress = prison.progression().getRankupProgress(player.getUniqueId());

        player.sendMessage("§eProgression:");
        player.sendMessage(" §7Rank: §f" + rank + " §8(§a" + String.format("%.1f", progress * 100) + "%§8)");
        player.sendMessage(" §7Prestige: §f" + prestige);
        player.sendMessage(" §7Rebirth: §f" + rebirth);

        // --- MARKET SERVICE EXAMPLE ---
        // We can query the dynamic market for specific materials.
        // It's safe to call even if the market module is disabled (it will return base values).
        if (prison.isModuleEnabled("market")) {
            Material target = Material.DIAMOND_ORE;
            double currentPrice = prison.market().getCurrentPrice(target);
            MarketTrend trend = prison.market().getTrend(target);
            
            String trendColor = switch (trend) {
                case RISING, SURGING -> "§a";
                case FALLING, CRASHING -> "§c";
                default -> "§7";
            };

            player.sendMessage(" ");
            player.sendMessage("§eMarket (Diamond Ore):");
            player.sendMessage(" §7Current Price: §f$" + String.format("%.2f", currentPrice));
            player.sendMessage(" §7Trend: " + trendColor + trend.name());
            
            // Example of how to report external sales to influence the dynamic market:
            // If your addon has a custom NPC that sells blocks, you should report the volume 
            // sold so the market engine reacts to the supply increase!
            // prison.market().recordSale(Material.DIAMOND_ORE, 128L);
        }

        // --- CURRENCY API EXAMPLE ---
        // This queries the dynamic currencies.yml system.
        // We can safely ask if a currency exists before reading or modifying it.
        player.sendMessage(" ");
        player.sendMessage("§eCustom Currencies:");
        if (prison.currencies().exists("shards")) {
            double shards = prison.currencies().balance(player.getUniqueId(), "shards");
            player.sendMessage(" §7Shards: §b" + prison.currencies().format("shards", shards));
        } else {
            player.sendMessage(" §7Shards: §cCurrency not created in currencies.yml");
        }

        player.sendMessage("§8§m----------------------------------------");
        return true;
    }
}
