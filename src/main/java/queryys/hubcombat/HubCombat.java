package queryys.hubcombat;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
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

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HubCombat extends JavaPlugin implements Listener, CommandExecutor {

    private final Map<UUID, Long> lastAttack = new HashMap<>();
    private CombatManager combatManager;
    private CombatScoreManager combatScoreManager;
    private PlayerData playerData;

    @Override
    public void onEnable() {
        getLogger().info("Enabling HubCombat plugin...");

        // Initialize managers
        combatScoreManager = new CombatScoreManager(this);
        combatManager = new CombatManager(this);
        playerData = new PlayerData(this);

        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        // Register commands
        registerCommands();

        // Save default config
        saveDefaultConfig();

        getLogger().info("HubCombat plugin enabled successfully.");
    }

    private void registerCommands() {
        PluginCommand hubCombatCommand = getCommand("hubcombat");
        if (hubCombatCommand != null) {
            hubCombatCommand.setExecutor(this);
        } else {
            getLogger().warning("Command 'hubcombat' not found!");
        }

        PluginCommand statsCommand = getCommand("stats");
        if (statsCommand != null) {
            statsCommand.setExecutor(new StatsCommand(combatScoreManager, playerData));
        } else {
            getLogger().warning("Command 'stats' not found!");
        }

        PluginCommand combatCommand = getCommand("combat");
        if (combatCommand != null) {
            combatCommand.setExecutor(new CombatCommand(combatManager));
        } else {
            getLogger().warning("Command 'combat' not found!");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hubcombat")) {
            // Implementa la logica del comando /hubcombat qui
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Questo comando può essere eseguito solo da un giocatore.");
                return true;
            }

            Player player = (Player) sender;
            // Logica del comando /hubcombat per il giocatore
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Comando sconosciuto.");
            return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemMeta meta = sword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Spada da combattimento");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            NamespacedKey key = new NamespacedKey(this, "combat_sword");
            meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, System.currentTimeMillis());
            sword.setItemMeta(meta);
            player.getInventory().setItem(0, sword);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
        } else {
            getLogger().warning("Impossibile creare il meta dell'oggetto per il giocatore " + player.getName());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling HubCombat plugin...");
        // Implementa la logica di pulizia qui
        combatScoreManager.saveData(); // Salvataggio dei dati prima dello spegnimento
        getLogger().info("HubCombat plugin disabled.");
    }

    @EventHandler
    public void onPlayerAttack(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isCombatSword(item)) {
                // Implementa la logica di attacco del giocatore qui
                player.sendMessage(ChatColor.RED + "La logica di attacco con la spada da combattimento deve essere implementata.");
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
