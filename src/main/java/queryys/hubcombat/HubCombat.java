package queryys.hubcombat;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import queryys.hubcombat.commands.CombatCommand;
import queryys.hubcombat.commands.StatsCommand;
import queryys.hubcombat.managers.CombatManager;
import queryys.hubcombat.score.CombatScoreManager;
import queryys.hubcombat.Data.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

public class HubCombat extends JavaPlugin implements Listener, CommandExecutor {

    private final Map<UUID, Long> lastAttack = new HashMap<>();
    public CombatManager combatManager;
    public PlayerData playerData;

    @Override
    public void onEnable() {
        // Initialize managers
        CombatScoreManager combatScoreManager = new CombatScoreManager(this);
        combatManager = new CombatManager(this);
        playerData = new PlayerData(this);

        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        // Register commands
        Objects.requireNonNull(getCommand("hubcombat")).setExecutor(this);
        Objects.requireNonNull(getCommand("stats")).setExecutor(new StatsCommand(this, combatScoreManager, playerData));
        Objects.requireNonNull(getCommand("combat")).setExecutor(new CombatCommand(this, combatManager));

        // Save default config
        saveDefaultConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemMeta meta = sword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Combat Sword");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            NamespacedKey key = new NamespacedKey(this, "combat_sword");
            meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, System.currentTimeMillis());
            sword.setItemMeta(meta);
            player.getInventory().setItem(0, sword);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            player.spigot().sendMessage(fromLegacyText(ChatColor.GOLD + "You now have a Combat Sword in your inventory!"));
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to create item meta.");
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemMeta meta = sword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Combat Sword");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            NamespacedKey key = new NamespacedKey(this, "combat_sword");
            meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, System.currentTimeMillis());
            sword.setItemMeta(meta);
            player.getInventory().setItem(0, sword);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
        } else {
            getLogger().warning("Failed to create item meta for player " + player.getName());
        }
    }

    @Override
    public void onDisable() {
        // Clean up
    }

    @EventHandler
    public void onPlayerAttack(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isCombatSword(item)) {
                // Implement PvP logic here
                player.sendMessage(ChatColor.RED + "The PvP logic has not been implemented yet.");
            }
        }
    }

    private boolean isCombatSword(ItemStack item) {
        if (item != null && item.getType() == Material.DIAMOND_SWORD) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                NamespacedKey key = new NamespacedKey(this, "combat_sword");
                return meta.getPersistentDataContainer().has(key, PersistentDataType.LONG);
            }
        }
        return false;
    }
}
