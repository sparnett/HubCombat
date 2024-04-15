package queryys.hubcombat.score;

import org.bukkit.entity.Player;
import queryys.hubcombat.HubCombat;

import java.util.HashMap;
import java.util.Map;

public class CombatScoreManager {

    private final Map<Player, Integer> kills = new HashMap<>();
    private final Map<Player, Integer> deaths = new HashMap<>();

    public CombatScoreManager(HubCombat hubCombat) {

    }

    public void addKill(Player player) {
        int currentKills = kills.getOrDefault(player, 0);
        kills.put(player, currentKills + 1);
    }

    public void addDeath(Player player) {
        int currentDeaths = deaths.getOrDefault(player, 0);
        deaths.put(player, currentDeaths + 1);
    }

    public int getKills(Player player) {
        return kills.getOrDefault(player, 0);
    }

    public int getDeaths(Player player) {
        return deaths.getOrDefault(player, 0);
    }

    public int getScore(Player player) {
        int playerKills = getKills(player);
        int playerDeaths = getDeaths(player);

        // Calcola lo score in base al numero di uccisioni e morti
        return playerKills - playerDeaths;
    }
}
