package queryys.hubcombat.score;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import queryys.hubcombat.HubCombat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatScoreManager {

    private final HubCombat plugin;
    private final Map<Player, Integer> kills = new HashMap<>();
    private final Map<Player, Integer> deaths = new HashMap<>();
    private final File dataFile;

    public CombatScoreManager(HubCombat plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "scores.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loadScores();
    }

    private void loadScores() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        for (String key : config.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            int playerKills = config.getInt(key + ".kills");
            int playerDeaths = config.getInt(key + ".deaths");

            kills.put(plugin.getServer().getPlayer(playerId), playerKills);
            deaths.put(plugin.getServer().getPlayer(playerId), playerDeaths);
        }
    }

    public void saveData() {
        YamlConfiguration config = new YamlConfiguration();

        for (Player player : kills.keySet()) {
            UUID playerId = player.getUniqueId();
            config.set(String.format("%s.kills", playerId.toString()), kills.get(player));
            config.set(String.format("%s.deaths", playerId.toString()), deaths.get(player));
        }

        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
