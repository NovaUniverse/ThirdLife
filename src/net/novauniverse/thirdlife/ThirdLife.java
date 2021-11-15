package net.novauniverse.thirdlife;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.novauniverse.thirdlife.config.ThirdLifeConfig;
import net.novauniverse.thirdlife.modules.DataManager;
import net.novauniverse.thirdlife.modules.ThridLifeManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;

public class ThirdLife extends JavaPlugin {
	private static ThirdLife instance;
	private ThirdLifeConfig configuration;

	public static ThirdLife getInstance() {
		return instance;
	}
	
	public ThirdLifeConfig getConfiguration() {
		return configuration;
	}

	@Override
	public void onEnable() {
		ThirdLife.instance = this;

		saveDefaultConfig();
		
		new File(this.getDataFolder().getAbsolutePath() + File.separator + "playerdata").mkdir();

		this.configuration = ThirdLifeConfig.parse(this.getConfig());
		
		ModuleManager.require(NetherBoardScoreboard.class);
		
		NetherBoardScoreboard.getInstance().setDefaultTitle("Stats");
		NetherBoardScoreboard.getInstance().setLineCount(2);
		
		ModuleManager.loadModule(DataManager.class, true);
		ModuleManager.loadModule(ThridLifeManager.class, true);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getServer().getScheduler().cancelTasks(this);
	}
}