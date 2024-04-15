package queryys.hubcombat.Data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import queryys.hubcombat.HubCombat;

import java.io.File;
import java.io.IOException;

public class PlayerData {

    private final HubCombat plugin;
    private final File playerDataFile;
    private final FileConfiguration playerDataConfig;

    public PlayerData(HubCombat plugin) {
        this.plugin = plugin;
        this.playerDataFile = new File(plugin.getDataFolder(), "player_data.yml");
        this.playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public void savePlayerData(Player player) {
        String playerName = player.getName();
        // Esempio di salvataggio di dati per il giocatore
        playerDataConfig.set(playerName + ".kills", getPlayerKills(player));
        playerDataConfig.set(playerName + ".deaths", getPlayerDeaths(player));

        // Salva i dati su file
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Impossibile salvare i dati del giocatore " + playerName);
        }
    }

    public int getPlayerKills(Player player) {
        // Restituisci il numero di uccisioni del giocatore
        return playerDataConfig.getInt(player.getName() + ".kills", 0);
    }

    public void setPlayerKills(Player player, int kills) {
        // Imposta il numero di uccisioni del giocatore
        playerDataConfig.set(player.getName() + ".kills", kills);
    }

    public int getPlayerDeaths(Player player) {
        // Restituisci il numero di morti del giocatore
        return playerDataConfig.getInt(player.getName() + ".deaths", 0);
    }

    public void setPlayerDeaths(Player player, int deaths) {
        // Imposta il numero di morti del giocatore
        playerDataConfig.set(player.getName() + ".deaths", deaths);
    }
}
