package dev.devmodee.pulseprisonaddon;

import dev.aeros.pulseprison.api.PulsePrisonAPI;
import dev.aeros.pulseprison.api.PulsePrisonProvider;
import dev.devmodee.pulseprisonaddon.enchants.SoulHarvestEnchant;
import dev.devmodee.pulseprisonaddon.listeners.PrisonListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class PulsePrisonAddon extends JavaPlugin {

    private PulsePrisonAPI prisonAPI;

    @Override
    public void onEnable() {
        // Safely obtain the API instance.
        // Since we have "depend: [PulsePrison]" in plugin.yml, we always load after it.
        if (!PulsePrisonProvider.isAvailable()) {
            getLogger().severe("PulsePrison is not available, disabling addon.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        prisonAPI = PulsePrisonProvider.get();
        getLogger().info("Successfully hooked into PulsePrison v" + prisonAPI.getVersion());

        // 1. Register our Listener for PulsePrison events
        getServer().getPluginManager().registerEvents(new PrisonListener(prisonAPI), this);

        // 2. Register our Example Command (Market and Progression fetching)
        if (getCommand("prisonaddon") != null) {
            getCommand("prisonaddon").setExecutor(new AddonCommand(prisonAPI));
        }

        // 3. Register a custom bracket action [heal] to use in menus and rewards
        // Usage in PulsePrison YAML files: - "[heal] 5"
        prisonAPI.actions().register("heal", (player, arg) -> {
            try {
                double amount = Double.parseDouble(arg);
                double newHealth = Math.min(player.getHealth() + amount, player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
                player.setHealth(newHealth);
                player.sendMessage("§aYou were healed for §2" + amount + " §ahearts!");
            } catch (NumberFormatException e) {
                player.sendMessage("§cError in heal action: The argument is not a valid number.");
            }
        });

        // 4. Register our custom enchantment written entirely in Java
        // This will automatically inject it into pickaxe menus, masteries, and the mining pipeline.
        prisonAPI.pickaxe().registerEnchant(new SoulHarvestEnchant(SoulHarvestEnchant.params()));

        getLogger().info("Example Addon has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        // It's important to unregister the enchant when disabling the addon 
        // to prevent PulsePrison from attempting to run an orphaned enchant.
        if (prisonAPI != null) {
            prisonAPI.pickaxe().unregisterEnchant("soul_harvest");
        }
        getLogger().info("Addon disabled.");
    }
}
