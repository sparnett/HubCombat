package queryys.hubcombat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import queryys.hubcombat.Data.PlayerData;
import queryys.hubcombat.HubCombat;
import queryys.hubcombat.score.CombatScoreManager;

public class StatsCommand implements CommandExecutor {

    private final HubCombat plugin;
    private final CombatScoreManager combatScoreManager;
    private final PlayerData playerData;

    public StatsCommand(HubCombat plugin, CombatScoreManager combatScoreManager, PlayerData playerData) {
        this.plugin = plugin;
        this.combatScoreManager = combatScoreManager;
        this.playerData = playerData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Questo comando può essere eseguito solo da un giocatore.");
            return true;
        }

        Player player = (Player) sender;
        int kills = combatScoreManager.getKills(player);
        int deaths = combatScoreManager.getDeaths(player);
        int score = combatScoreManager.getScore(player);

        player.sendMessage("Statistiche di combattimento:");
        player.sendMessage("Uccisioni: " + kills);
        player.sendMessage("Morti: " + deaths);
        player.sendMessage("Punteggio: " + score);

        return true;
    }
}
