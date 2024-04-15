package queryys.hubcombat.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import queryys.hubcombat.managers.CombatManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {

    private final CombatManager combatManager;

    public PlayerListener(JavaPlugin plugin) {
        this.combatManager = new CombatManager(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.DIAMOND_SWORD) {
            Player player = event.getPlayer();
            if (!combatManager.isInCombat(player)) {
                combatManager.enterCombat(player);
                combatManager.startCooldown(player);
            }
        }
    }
}
