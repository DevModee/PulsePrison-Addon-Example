package dev.devmodee.pulseprisonaddon.enchants;

import dev.aeros.pulseprison.api.PulsePrisonProvider;
import dev.aeros.pulseprison.enchants.base.BaseEnchant;
import dev.aeros.pulseprison.enchants.base.EnchantContext;
import dev.aeros.pulseprison.enchants.base.EnchantRarity;
import dev.aeros.pulseprison.enchants.base.EnchantTrigger;
import dev.aeros.pulseprison.enchants.base.EnchantType;
import dev.aeros.pulseprison.enchants.registry.EnchantLoader.EnchantParams;
import dev.aeros.pulseprison.enchants.utils.ChanceCalculator;

import java.util.List;
import java.util.Map;

/**
 * Example of an enchantment programmed directly in Java.
 * Extends BaseEnchant and provides its initial configuration and activation logic.
 */
public final class SoulHarvestEnchant extends BaseEnchant {

    public SoulHarvestEnchant(EnchantParams p) {
        super(p.id, p.displayName, p.type, p.trigger, p.rarity,
              p.maxLevel, p.baseCost, p.costMultiplier, p.currency,
              p.requiredPickaxeLevel, p.conflictsWith, p.requires,
              p.guiSlot, p.guiItem, p.levelValues, p.levelChances,
              p.descriptionES, p.descriptionEN);
    }

    // Configure the default values for the enchant
    // These will be merged with the server configuration (in case an admin edits them)
    public static EnchantParams params() {
        EnchantParams p = new EnchantParams();
        p.id = "soul_harvest";
        p.displayName = "&5Soul Harvest";
        p.type = EnchantType.PICKAXE;
        // Triggers AFTER breaking the block (ideal for giving rewards without altering drops)
        p.trigger = EnchantTrigger.POST_BREAK;
        p.rarity = EnchantRarity.EPIC;
        p.maxLevel = 50;
        p.baseCost = 100000;
        p.costMultiplier = 1.08;
        p.currency = "tokens"; // Uses the 'tokens' currency defined in currencies.yml
        p.requiredPickaxeLevel = 20;
        p.guiSlot = 43;
        p.guiItem = "WITHER_SKELETON_SKULL";
        
        // The value it provides at level 1 and at level 50
        p.levelValues = Map.of(1, 1.0, 50, 25.0);
        // The probability of it triggering at level 1 and at level 50
        p.levelChances = Map.of(1, 0.5, 50, 5.0);
        
        p.descriptionEN = List.of("&7Level {level}: &f{value} souls ({chance}%)");
        p.descriptionES = List.of("&7Nivel {level}: &f{value} almas ({chance}%)");
        return p;
    }

    @Override
    public boolean activate(EnchantContext context) {
        int level = context.getEnchantLevel();
        
        // Evaluate if the enchant triggers based on its base probability and the player's bonuses
        if (!ChanceCalculator.roll(getLevelChance(level))) {
            return false;
        }

        // If it triggers, we can do whatever we want. In this example, we add balance
        // to a custom currency "souls", verifying that it exists first.
        if (PulsePrisonProvider.get().currencies().exists("souls")) {
            PulsePrisonProvider.get().currencies().give(
                    context.getPlayer().getUniqueId(), 
                    "souls", 
                    getLevelValue(level)
            );
        } else {
            // If the "souls" currency doesn't exist, we give them gems using the native economy.
            // Instead of giving them instantly, we add them to the context (summary),
            // which applies the player's gem multipliers automatically.
            context.addGemBonus((long) getLevelValue(level));
        }

        // Returning 'true' tells the engine that the enchant took effect,
        // this is used to trigger ActionBar messages and add to mastery statistics.
        return true;
    }
}
