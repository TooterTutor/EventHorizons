package io.github.tootertutor.eventhorizons.items.weapons;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import io.github.tootertutor.eventhorizons.EventHorizons;
import io.github.tootertutor.eventhorizons.interfaces.AutoRegisterItem;
import io.github.tootertutor.eventhorizons.interfaces.IInvAction;
import io.github.tootertutor.eventhorizons.items.Item;
import io.papermc.paper.event.player.PlayerPickItemEvent;

public class Shockwave extends Item implements IInvAction, Listener, AutoRegisterItem {
    private final Random random = new Random();

    private static final int MAX_RADIUS = 8; // Maximum radius for the second ring
    private static final int BLOCK_HEIGHT = 0; // Height at which blocks will be spawned
    private static final int RINGS = 8; // Number of rings to spawn
    private static final double RING_OFFSET = 1.25; // Offset for the first ring
    private static final int RANDOM_POINTS = 5; // Number of random points in the first ring
    private static final long SPIN_DELAY = 2L; // Delay for spinning the armor stand
    private static final int BLOCKS_PER_RING = 8; // Reduced number of blocks per ring
    private static final long DELAY_BETWEEN_BLOCKS = 2L; // Delay between each block spawn
    // private static final long COOLDOWN_TIME = 12000; // Cooldown time in milliseconds (10 seconds)
    // private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    protected Shockwave(EventHorizons plugin) {
        super(plugin, new NamespacedKey(plugin, "shockwave"));
        super.displayName = "Shockwave";
        super.nameColor = "#12EADC";
        super.lore = Arrays.asList("Cause an earthquake at your feet");
        super.loreColor = Arrays.asList("#43F0C0");
        super.material = Material.GOAT_HORN;
        super.itemStack = new ItemStack(material);

        applyMetadata();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (isItem(item)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                player.playSound(player, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1, 0.4F);

                // Create and manage the spinning armor stand
                spawnSpinningArmorStand(player);
            }
        }
    }

    private void spawnSpinningArmorStand(Player player) {
        Location center = player.getLocation();
        ArmorStand armorStand = player.getWorld().spawn(center, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true); // Makes it a small, invisible marker

        new BukkitRunnable() {
            int currentRing = 1;

            @Override
            public void run() {
                if (currentRing <= RINGS) {
                    // Rotate the armor stand around the player
                    double angle = (currentRing * Math.PI / 4); // Adjust the speed of rotation
                    double x = Math.cos(angle) * 2; // Distance from the player
                    double z = Math.sin(angle) * 2;
                    armorStand.teleport(center.clone().add(x, 0, z));

                    // Spawn the shockwave at the armor stand's location
                    spawnRandomPointsAroundPlayer(armorStand.getLocation(), player);

                    currentRing++;
                } else {
                    armorStand.remove(); // Remove the armor stand after the effect
                    cancel(); // Stop the task
                }
            }
        }.runTaskTimer(EventHorizons.getInstance(), 0L, SPIN_DELAY); // Spin the armor stand every 2 ticks
    }

    private void spawnRandomPointsAroundPlayer(Location center, Player player) {
        Set<LivingEntity> pushedEntities = new HashSet<>();

        for (int i = 0; i < RANDOM_POINTS; i++) {
            // Generate a random angle
            double angle = random.nextDouble() * 2 * Math.PI;
            // Calculate random offset within the smaller radius
            double offsetX = (random.nextDouble() * 2 - 1) * RING_OFFSET;
            double offsetZ = (random.nextDouble() * 2 - 1) * RING_OFFSET;

            // Calculate the random point location
            double x = Math.cos(angle) * RING_OFFSET + offsetX;
            double z = Math.sin(angle) * RING_OFFSET + offsetZ;
            Location randomPoint = center.clone().add(x, BLOCK_HEIGHT, z);

            // Spawn the falling blocks around this random point
            spawnFallingBlocksAroundPlayer(randomPoint, pushedEntities, player);
        }
    }

    private void spawnFallingBlocksAroundPlayer(Location center, Set<LivingEntity> pushedEntities, Player player) {
        new BukkitRunnable() {
            int currentRing = 1; // Start with the first ring
            int currentBlock = 0; // Track the current block being spawned

            @Override
            public void run() {
                if (currentRing <= MAX_RADIUS) {
                    if (currentBlock < BLOCKS_PER_RING) {
                        // Calculate the position for the current block
                        double angle = (2 * Math.PI / BLOCKS_PER_RING) * currentBlock;
                        double x = currentRing * Math.cos(angle);
                        double z = currentRing * Math.sin(angle);
                        Location blockLocation = center.clone().add(x, BLOCK_HEIGHT, z);

                        Block ground = findGround(blockLocation.getBlock());
                        Block blockAbove = ground.getRelative(BlockFace.UP);

                        if (blockAbove.getType().isAir()) {
                            createJumpingBlock(ground, blockAbove);
                        }

                        // Push entities if they are within range
                        for (Entity entity : ground.getChunk().getEntities()) {
                            if (entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())
                                    && pushedEntities.add((LivingEntity) entity)) {
                                pushEntity(player, (LivingEntity) entity, ground.getLocation());
                            }
                        }

                        currentBlock++; // Move to the next block
                    } else {
                        currentRing++; // Move to the next ring
                        currentBlock = 0; // Reset block counter for the next ring
                    }
                } else {
                    cancel(); // Stop the task when all rings are processed
                }
            }
        }.runTaskTimer(EventHorizons.getInstance(), 0L, DELAY_BETWEEN_BLOCKS); // Delay between each block spawn
    }

    private Block findGround(Block block) {
        // Logic to find the ground block
        while (block.getType() == Material.AIR && block.getY() > 0) {
            block = block.getRelative(BlockFace.DOWN);
        }
        return block;
    }

    @ParametersAreNonnullByDefault
    private void createJumpingBlock(Block ground, Block blockAbove) {
        Location location = blockAbove.getLocation().add(0.5, 0.0, 0.5);
        ground.getWorld().spawn(location, FallingBlock.class, fallingBlock -> {
            fallingBlock.setBlockData(ground.getBlockData());
            fallingBlock.setDropItem(false);
            fallingBlock.setCancelDrop(true);
            fallingBlock.setVelocity(new Vector(0, 0.4, 0));
            fallingBlock.setMetadata("shockwave",
                    new FixedMetadataValue(EventHorizons.getInstance(), "ruptured_block"));
        });
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        if (event.getEntity().getType() == EntityType.FALLING_BLOCK && event.getEntity().hasMetadata("shockwave")) {
            event.setCancelled(true);
            event.getEntity().removeMetadata("shockwave", EventHorizons.getInstance());
            event.getEntity().remove();
        }
    }

    private void pushEntity(Player player, LivingEntity entity, Location location) {
        // Calculate the direction vector from the player to the entity
        Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

        // Set the push strength
        double pushStrength = 1.5; // Adjust this value as needed
        direction.multiply(pushStrength);

        // Add an upward component to the velocity
        direction.setY(0.5); // Adjust this value for how high you want the entity to be thrown

        // Apply the velocity to the entity
        entity.setVelocity(direction);

        // Damage the entity
        entity.damage(6);
    }

    @Override
    public void onDropItem(PlayerDropItemEvent event) {
    }

    @Override
    public void onPickItem(PlayerPickItemEvent event) {
    }

    @Override
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
    }

}
