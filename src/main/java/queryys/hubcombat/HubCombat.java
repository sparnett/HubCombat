package queryys.hubcombat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import queryys.hubcombat.commands.CombatCommand;
import queryys.hubcombat.commands.StatsCommand;
import queryys.hubcombat.managers.CombatManager;
import queryys.hubcombat.score.CombatScoreManager;

public class HubCombat extends JavaPlugin implements Listener, CommandExecutor {

    private CombatManager combatManager;
    private CombatScoreManager combatScoreManager;

    @Override
    public void onEnable() {
        getLogger().info("Enabling HubCombat plugin...");

        // Initialize managers
        combatScoreManager = new CombatScoreManager(this);
        combatManager = new CombatManager(this);

        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        // Register commands
        registerCommands();

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
            statsCommand.setExecutor(new StatsCommand(combatScoreManager));
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
            if (sender instanceof Player) {
                Player player = (Player) sender;
                // Logica per il comando /hubcombat per i giocatori
                player.sendMessage(ChatColor.GREEN + "Hai eseguito il comando /hubcombat!");
            } else {
                // Logica per il comando /hubcombat per il console
                Bukkit.getLogger().info("Il comando /hubcombat può essere eseguito solo da un giocatore.");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("stats")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                // Logica per il comando /stats per i giocatori
                player.sendMessage(ChatColor.GREEN + "Ecco le tue statistiche:");
                player.sendMessage(ChatColor.YELLOW + "Kills: " + combatScoreManager.getKills(player));
                player.sendMessage(ChatColor.YELLOW + "Deaths: " + combatScoreManager.getDeaths(player));
            } else {
                // Logica per il comando /stats per il console
                Bukkit.getLogger().info("Il comando /stats può essere eseguito solo da un giocatore.");
            }
            return true;
        }
        // Aggiungi altri comandi qui
        return false;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Player join logic
        player.sendMessage(ChatColor.GREEN + "Benvenuto, " + player.getName() + "!");
        // Esempio: Assegna la spada al giocatore al momento del join
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        player.getInventory().addItem(sword);
    }


    @Override
    public void onDisable() {
        // Esempio di clean-up logic: salvare i dati prima di disabilitare il plugin
        combatScoreManager.saveData();
        getLogger().info("HubCombat plugin disabilitato. Dati salvati.");
    }


    @EventHandler
    public void onPlayerAttack(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isCombatSword(item)) {
                // Verifica se il giocatore ha la spada da combattimento
                Player target = getTargetPlayer(player);
                if (target != null) {
                    // Se il giocatore ha colpito un altro giocatore
                    player.sendMessage(ChatColor.GREEN + "Sei entrato in combattimento!");
                    target.sendMessage(ChatColor.RED + "Sei stato colpito da " + player.getName() + "!");
                } else {
                    // Se il giocatore ha colpito l'aria o un blocco senza colpire un giocatore
                    player.sendMessage(ChatColor.RED + "Non hai colpito nessun giocatore!");
                }
            }
        }
    }

    private Player getTargetPlayer(Player player) {
        // Ottiene la direzione dello sguardo del giocatore
        Location eyeLocation = player.getEyeLocation();
        @NotNull Vector direction = eyeLocation.getDirection();

        // Esegui un raycast per trovare il giocatore colpito
        double maxDistance = 100; // Distanza massima per il raycast
        Location targetLocation = eyeLocation.clone();
        for (double distance = 0; distance <= maxDistance; distance += 0.5) {
            targetLocation.add(direction);
            Block block = targetLocation.getBlock();
            if (block.getType().isSolid()) {
                // Se il raycast colpisce un blocco solido, interrompi il loop
                break;
            }

            // Controlla se c'è un giocatore nel blocco colpito
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target != player && target.getLocation().getBlock().equals(block)) {
                    // Se il giocatore non è quello che ha eseguito il raycast e si trova nel blocco colpito, restituisci il giocatore
                    return target;
                }
            }
        }

        // Se nessun giocatore è stato colpito, restituisci null
        return null;
    }

    private boolean isCombatSword(ItemStack item) {
        // Verifica se l'oggetto è una spada
        if (item.getType() != Material.DIAMOND_SWORD && item.getType() != Material.IRON_SWORD &&
                item.getType() != Material.GOLDEN_SWORD && item.getType() != Material.STONE_SWORD &&
                item.getType() != Material.WOODEN_SWORD) {
            return false;
        }

        // Verifica se la spada ha un nome personalizzato che la identifica come spada da combattimento
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.RED + "Spada da Combattimento")) {
            return true;
        }

        // Verifica se la spada ha un enchantment specifico che la identifica come spada da combattimento
        return meta != null && meta.hasEnchants() && meta.getEnchants().containsKey(Enchantment.KNOCKBACK);

        // Aggiungi ulteriori condizioni di identificazione della spada da combattimento qui

        // Se non si soddisfa nessuna delle condizioni sopra, restituisci false
    }

}
