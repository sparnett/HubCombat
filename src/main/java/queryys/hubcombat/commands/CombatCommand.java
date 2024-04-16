package queryys.hubcombat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import queryys.hubcombat.HubCombat;
import queryys.hubcombat.managers.CombatManager;

public class CombatCommand implements CommandExecutor {

    private HubCombat plugin;
    private final CombatManager combatManager;

    public CombatCommand(CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Questo comando può essere eseguito solo da un giocatore.");
            return true;
        }

        Player player = (Player) sender;

        // Controlla se il giocatore ha specificato un bersaglio per il combattimento
        if (args.length == 0) {
            // Se il giocatore non specifica un bersaglio, entra in combattimento
            handleCombat(player);
        } else {
            // Se il giocatore specifica un bersaglio, cerca di entrare in combattimento con quel bersaglio
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("Il giocatore specificato non è online.");
                return true;
            }
            handleCombatWithTarget(player, target);
        }

        return true;
    }

    // Gestisce l'entrata in combattimento per il giocatore
    private void handleCombat(Player player) {
        // Verifica se il giocatore è già in combattimento
        if (combatManager.isInCombat(player)) {
            player.sendMessage("Sei già in modalità combattimento.");
            return;
        }

        // Entra in combattimento
        combatManager.enterCombat(player);
        player.sendMessage("Sei entrato in modalità combattimento.");

        // Avvia un cooldown per il combattimento
        combatManager.startCooldown(player);
    }

    // Gestisce l'entrata in combattimento con un bersaglio specifico
    private void handleCombatWithTarget(Player player, Player target) {
        // Verifica se il giocatore è già in combattimento
        if (combatManager.isInCombat(player)) {
            player.sendMessage("Sei già in modalità combattimento.");
            return;
        }

        // Verifica se il bersaglio è già in combattimento
        if (combatManager.isInCombat(target)) {
            player.sendMessage("Il bersaglio è già in modalità combattimento.");
            return;
        }

        // Entra in combattimento con il bersaglio
        combatManager.enterCombat(player);
        combatManager.enterCombat(target);
        player.sendMessage("Sei entrato in modalità combattimento con " + target.getName());

        // Avvia un cooldown per il combattimento
        combatManager.startCooldown(player);
        combatManager.startCooldown(target);
    }
}
