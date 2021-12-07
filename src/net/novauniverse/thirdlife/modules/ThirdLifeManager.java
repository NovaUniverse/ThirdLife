package net.novauniverse.thirdlife.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.novauniverse.thirdlife.ThirdLife;
import net.novauniverse.thirdlife.data.PlayerData;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class ThirdLifeManager extends NovaModule implements Listener {
	private static ThirdLifeManager instance;
	private Task task;

	@Override
	public String getName() {
		return "ThirdLifeManager";
	}

	public static ThirdLifeManager getInstance() {
		return ThirdLifeManager.instance;
	}

	@Override
	public void onLoad() {
		ThirdLifeManager.instance = this;
		task = new SimpleTask(ThirdLife.getInstance(), new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getOnlinePlayers().forEach(player -> {
					if (player.getGameMode() == GameMode.SPECTATOR) {
						Location location = player.getLocation();

						if (location.getY() < 0) {
							location.setY(0);
							player.teleport(location);
						}
					}
				});
			}
		}, 10L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStartTask(task);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();

		PlayerData data = DataManager.getInstance().getData(player);

		if (data.getLives() > 0) {
			data.setLives(data.getLives() - 1);
		}

		if (ThirdLife.getInstance().getConfiguration().shouldMessagePlayer()) {
			switch (data.getLives()) {
			case 2:
				player.sendMessage(ChatColor.YELLOW + "You are now a yellow player");
				break;

			case 1:
				player.sendMessage(ChatColor.RED + "You are now a red player. Your goal is to attack and kill green and yellow players");
				break;

			case 0:
				player.sendMessage(ChatColor.GRAY + "You are now a spectator");
				break;

			default:
				break;
			}
		}

		data.save();

		this.updatePlayer(e.getEntity());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		this.updatePlayer(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			PlayerData playerData = DataManager.getInstance().getData(player);

			if (playerData.getLives() == 0) {
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player player = (Player) e.getEntity();
			Player attacker = (Player) e.getDamager();

			PlayerData playerData = DataManager.getInstance().getData(player);
			PlayerData attackerData = DataManager.getInstance().getData(attacker);

			// Remove red player protection
			if (attackerData.getLives() == 1 && attackerData.isProtected()) {
				attackerData.setProtected(false);
				if (ThirdLife.getInstance().getConfiguration().isRedProtection()) {
					attacker.sendMessage(ChatColor.RED + "You are no longer protected since you attacked another player");
				}
				attackerData.save();
				this.updatePlayer(attacker);
			}

			// Prevent yellow and green from attacking each other
			if (attackerData.getLives() > 1 && playerData.getLives() > 1) {
				attacker.sendMessage(ChatColor.RED + "You cannot attack friendly players");
				e.setCancelled(true);
				return;
			}

			// Red player protection
			if (playerData.getLives() == 1 && playerData.isProtected() && ThirdLife.getInstance().getConfiguration().isRedProtection()) {
				attacker.sendMessage(ChatColor.RED + "You cant attack this player since they have not yet attacked soneone");
				e.setCancelled(true);
				return;
			}
		}
	}

	public void updatePlayer(Player player) {
		PlayerData data = DataManager.getInstance().getData(player);

		ChatColor color;

		switch (data.getLives()) {
		case 3:
			color = ChatColor.GREEN;
			break;

		case 2:
			color = ChatColor.YELLOW;
			break;

		case 1:
			color = ChatColor.RED;
			break;

		default:
			color = ChatColor.GRAY;
			break;
		}

		NetherBoardScoreboard.getInstance().setPlayerNameColor(player, color);
		NetherBoardScoreboard.getInstance().setPlayerLine(0, player, ChatColor.GOLD + "Lives: " + color + "" + data.getLives());

		if (data.getLives() == 1) {
			if (ThirdLife.getInstance().getConfiguration().isRedProtection()) {
				boolean protection = data.isProtected();
				NetherBoardScoreboard.getInstance().setPlayerLine(1, player, ChatColor.GOLD + "Protected: " + (protection ? ChatColor.GREEN + "yes" : ChatColor.RED + "no"));
			}
		} else {
			NetherBoardScoreboard.getInstance().setPlayerLine(1, player, "");
		}

		player.setPlayerListName(color + player.getName());
		player.setDisplayName(color + player.getName() + ChatColor.RESET);

		if (data.getLives() <= 0) {
			player.setGameMode(GameMode.SPECTATOR);
		}
	}
}