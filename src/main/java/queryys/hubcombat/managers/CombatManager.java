package queryys.hubcombat.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CombatManager {

    private final HashMap<Player, Long> combatCooldowns = new HashMap<>();
    private final JavaPlugin plugin;

    public CombatManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isInCombat(Player player) {
        return combatCooldowns.containsKey(player);
    }

    public void enterCombat(Player player) {
        combatCooldowns.put(player, System.currentTimeMillis() + 10000L);
    }

    public void exitCombat(Player player) {
        combatCooldowns.remove(player);
    }

    public void startCooldown(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                exitCombat(player);
            }
        }.runTaskLater(plugin, 200);
    }
}
