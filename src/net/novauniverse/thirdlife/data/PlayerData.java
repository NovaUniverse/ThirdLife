package net.novauniverse.thirdlife.data;

import java.util.UUID;

import net.novauniverse.thirdlife.modules.DataManager;

public class PlayerData {
	private UUID uuid;
	private int lives;
	private boolean protection;

	public PlayerData(UUID uuid) {
		this(uuid, 3, true);
	}

	public PlayerData(UUID uuid, int lives, boolean protection) {
		this.uuid = uuid;
		this.lives = lives;
		this.protection = protection;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getLives() {
		return lives;
	}
	
	public void setLives(int lives) {
		this.lives = lives;
	}

	public boolean isProtected() {
		return protection;
	}
	
	public void setProtected(boolean protection) {
		this.protection = protection;
	}
	
	public void save() {
		DataManager.getInstance().savePlayerData(this);
	}
}