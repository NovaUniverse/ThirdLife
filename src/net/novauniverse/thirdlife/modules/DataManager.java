package net.novauniverse.thirdlife.modules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import net.novauniverse.thirdlife.ThirdLife;
import net.novauniverse.thirdlife.data.PlayerData;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.commons.utils.UUIDUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class DataManager extends NovaModule implements Listener {
	private static DataManager instance;

	public static DataManager getInstance() {
		return instance;
	}

	private List<PlayerData> playerData;

	@Override
	public String getName() {
		return "ThirdLifeDataManager";
	}

	@Override
	public void onLoad() {
		DataManager.instance = this;
		playerData = new ArrayList<>();
	}

	@Override
	public void onEnable() throws Exception {
		playerData.clear();
		Bukkit.getServer().getOnlinePlayers().forEach(player -> this.getData(player));
	}

	@Override
	public void onDisable() throws Exception {
		playerData.forEach(data -> data.save());
		playerData.clear();
	}

	public PlayerData getData(Player player) {
		return this.getData(player.getUniqueId());
	}

	public PlayerData getData(UUID uuid) {
		for (PlayerData data : playerData) {
			if (UUIDUtils.isSame(data.getUuid(), uuid)) {
				return data;
			}
		}

		File playerDataFile = this.getPlayerDataFile(uuid);
		if (playerDataFile.exists()) {
			try {
				JSONObject json = JSONFileUtils.readJSONObjectFromFile(playerDataFile);

				int lives = json.getInt("lives");
				boolean protection = json.getBoolean("protected");

				PlayerData data = new PlayerData(uuid, lives, protection);

				playerData.add(data);

				return data;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new PlayerData(uuid);
	}

	public File getPlayerDataFile(UUID uuid) {
		return new File(ThirdLife.getInstance().getDataFolder().getAbsolutePath() + File.separator + "playerdata" + File.separator + uuid.toString() + ".json");
	}

	public boolean savePlayerData(PlayerData data) {
		File dataFile = this.getPlayerDataFile(data.getUuid());

		JSONObject json = new JSONObject();

		json.put("uuid", data.getUuid().toString());
		json.put("lives", data.getLives());
		json.put("protected", data.isProtected());

		try {
			JSONFileUtils.saveJson(dataFile, json, 4);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		this.getData(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerData data = this.getData(e.getPlayer());
		data.save();
		playerData.remove(data);
	}
}