package queryys.hubcombat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import queryys.hubcombat.Data.PlayerData;
import queryys.hubcombat.score.CombatScoreManager;

public class StatsCommand implements CommandExecutor {

    public final CombatScoreManager combatScoreManager;
    public PlayerData playerData = null;

    public StatsCommand(CombatScoreManager combatScoreManager) {
        this.combatScoreManager = combatScoreManager;
        this.playerData = playerData;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
