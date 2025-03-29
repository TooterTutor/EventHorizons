package io.github.tootertutor.eventhorizons.items.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.github.tootertutor.eventhorizons.EventHorizons;
import io.github.tootertutor.eventhorizons.interfaces.AutoRegisterItem;
import io.github.tootertutor.eventhorizons.items.Item;

/**
 * A Special Multi-tool that swaps to the tool best suited to mine a block.
 */
public class Paxel extends Item implements Listener, AutoRegisterItem {

    private final Set<Material> pickaxeBlocks = getMaterials(Tag.MINEABLE_PICKAXE);
    private final Set<Material> axeBlocks = getMaterials(Tag.MINEABLE_AXE);
    private final Set<Material> shovelBlocks = getMaterials(Tag.MINEABLE_SHOVEL);
    private final Set<Material> hoeBlocks = getMaterials(Tag.MINEABLE_HOE);

    private Set<Material> getMaterials(Tag<Material> tag) {
        return new HashSet<>(tag.getValues());
    }

    public Paxel(EventHorizons plugin) {
        super(plugin, new NamespacedKey(plugin, "paxel"));
        super.displayName = "Paxel";
        super.nameColor = "#33FFBB";
        super.lore = Arrays.asList("The True Multi-Tool", "Automatically switches to the right tool",
                "for the job!");
        super.loreColor = Arrays.asList("#00BBCC", "#00BBCC", "#00BBCC");
        super.material = Material.DIAMOND_PICKAXE;
        super.itemStack = new ItemStack(material);

        applyMetadata();
    }

    private Material getToolMaterial(ItemStack item, Material blockType) {
        boolean isNetherite = item.getType().toString().contains("NETHERITE");
        boolean isGold = item.getType().toString().contains("GOLDEN");
        boolean isIron = item.getType().toString().contains("IRON");

        Material toolMaterial = null;

        if (shovelBlocks.contains(blockType)) {
            toolMaterial = isNetherite ? Material.NETHERITE_SHOVEL
                    : isGold ? Material.GOLDEN_SHOVEL : isIron ? Material.IRON_SHOVEL : Material.DIAMOND_SHOVEL;
        } else if (axeBlocks.contains(blockType)) {
            toolMaterial = isNetherite ? Material.NETHERITE_AXE
                    : isGold ? Material.GOLDEN_AXE : isIron ? Material.IRON_AXE : Material.DIAMOND_AXE;
        } else if (pickaxeBlocks.contains(blockType)) {
            toolMaterial = isNetherite ? Material.NETHERITE_PICKAXE
                    : isGold ? Material.GOLDEN_PICKAXE : isIron ? Material.IRON_PICKAXE : Material.DIAMOND_PICKAXE;
        } else if (hoeBlocks.contains(blockType)) {
            toolMaterial = isNetherite ? Material.NETHERITE_HOE
                    : isGold ? Material.GOLDEN_HOE : isIron ? Material.IRON_HOE : Material.DIAMOND_HOE;
        }

        return toolMaterial; // Returns null if not a valid block type for Paxel
    }

    public void onInteract(PlayerInteractEvent event) {
        // No special interaction needed, block damage will handle tool switching
        event.setCancelled(false);
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isItem(item)) {
            return;
        }

        // Switch to axe for entity combat
        String type = item.getType().toString();
        Material axeType = Material.DIAMOND_AXE; // Default Value

        if (type.contains("NETHERITE")) {
            axeType = Material.NETHERITE_AXE;
        } else if (type.contains("GOLDEN")) {
            axeType = Material.GOLDEN_AXE;
        } else if (type.contains("IRON")) {
            axeType = Material.IRON_AXE;
        }

        // Only switch if not already an axe
        if (item.getType() != axeType) {
            ItemStack newAxe = item.withType(axeType);
            copyItemMeta(item, newAxe);
            player.getInventory().setItemInMainHand(newAxe);
        }

    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();

        if (!isItem(item))
            return;

        Material blockType = event.getBlock().getType();
        Material toolType = getToolMaterial(item, blockType);

        if (toolType != null && toolType != item.getType()) {
            // ItemStack newTool = this.withType(toolType);
            // player.getInventory().setItemInMainHand(newTool);
            item.setType(toolType);
        }

    }
}
