package net.novauniverse.thirdlife.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ThirdLifeConfig {
	private boolean messagePlayer;
	private boolean redProtection;
	private boolean allowTransferLife;
	private boolean allowRevive;

	public boolean shouldMessagePlayer() {
		return messagePlayer;
	}

	public boolean isRedProtection() {
		return redProtection;
	}

	public boolean isAllowTransferLife() {
		return allowTransferLife;
	}

	public boolean isAllowRevive() {
		return allowRevive;
	}

	private ThirdLifeConfig(boolean messagePlayer, boolean redProtection, boolean allowTransferLife, boolean allowRevive) {
		this.messagePlayer = messagePlayer;
		this.redProtection = redProtection;
		this.allowTransferLife = allowTransferLife;
		this.allowRevive = allowRevive;
	}

	public static ThirdLifeConfig parse(FileConfiguration config) {
		boolean messagePlayer = config.getBoolean("message_player");
		boolean redProtection = config.getBoolean("protect_red_players");
		boolean allowTransferLife = config.getBoolean("allow_transfer_life");
		boolean allowRevive = config.getBoolean("allow_revive");

		return new ThirdLifeConfig(messagePlayer, redProtection, allowTransferLife, allowRevive);
	}
}