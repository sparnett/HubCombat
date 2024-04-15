package queryys.hubcombat;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

public class HubCombat extends JavaPlugin implements Listener, CommandExecutor {

    private final Map<UUID, Long> lastAttack = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("hubcombat").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
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
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
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
        // Vuoto
    }

    public Map<UUID, Long> getLastAttack() {
        return lastAttack;
    }
}
