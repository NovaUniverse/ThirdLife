package net.novauniverse.thirdlife.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ThirdLifeConfig {
	private boolean messagePlayer;
	
	public boolean shouldMessagePlayer() {
		return messagePlayer;
	}
	
	private ThirdLifeConfig(boolean messagePlayer) {
		this.messagePlayer = messagePlayer;
	}
	
	public static ThirdLifeConfig parse(FileConfiguration config) {
		boolean messagePlayer = config.getBoolean("message_player");
		
		return new ThirdLifeConfig(messagePlayer);
	}
}