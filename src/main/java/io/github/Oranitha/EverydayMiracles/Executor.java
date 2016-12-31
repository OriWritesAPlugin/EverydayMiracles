package io.github.Oranitha.EverydayMiracles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Executor {

	public static boolean follow(EverydayMiracles plugin, CommandSender sender, String[] args) {
		// TODO: datahandler as arg
		// Check if not player
		if (!(sender instanceof Player)) {
			sender.sendMessage("The console obeys no one!");
			return true;
		}

		Player player = (Player) sender;
		DataHandler dh = new DataHandler();
		String status = dh.getPlayerDeity(sender);

		// Check if deity assigned
		if (status != null) {
			player.sendMessage("You are already a follower of " + status + "!");
			return true;
		}

		ArrayList<String> responses = dh.getDeities();
		if (args.length > 1) {
			String maybeDeity = args[1];
			boolean realDeity = false;
			for (String deity : responses) {
				if (args[1].equals(deity)) {
					realDeity = true;
				}
			}
			if (realDeity) {
				plugin.getPlayerData().set(sender.getName() + ".deity", maybeDeity);
				plugin.savePlayerData();
				player.sendMessage("You have been recognized as a follower of " + maybeDeity + "!");
				plugin.setNickname(player);
				return true;
			}
		}
		StringBuilder out = new StringBuilder("Your choices are:  ");
		for (String deity : responses) {
			out.append(deity + ", ");
		}
		;
		out.setLength(out.length() - 2);
		player.sendMessage(out.toString());
		return true;
	}

	public static boolean chat(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh) {
		// Check if not player
		if (!(sender instanceof Player)) {
			sender.sendMessage("The console has no higher power to speak to!");
			return true;
		}

		String deity = dh.getPlayerDeity(sender);

		// Check if no deity assigned
		if (deity == null) {
			sender.sendMessage("You have not yet chosen who to follow!");
			return true;
		}

		FileConfiguration deityConfig = dh.getDeity(deity);
		try {
			Map<String, Object> greetingsList = deityConfig.getConfigurationSection(deity + ".chatter")
					.getValues(false);
			Object[] values = greetingsList.values().toArray();
			Random generator = new Random();
			sender.sendMessage(ChatColor.ITALIC + ((String) values[generator.nextInt(values.length)]));
		} catch (NullPointerException e) {
			sender.sendMessage(ChatColor.RED + "Your deity has no chatter set up...contact your dev!");
			return false;
		}
		return true;
	}

	public static boolean quest(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh) {
		// Check if not player
		if (!(sender instanceof Player)) {
			sender.sendMessage("The console has no higher power to speak to!");
			return false;
		}
		String deity = dh.getPlayerDeity(sender);
		Player player = (Player) sender;
		String name = player.getName();

		// Check if no deity assigned
		if (deity == null) {
			player.sendMessage("You have not yet chosen who to follow!");
			return true;
		}

		// Check if quest already assigned
		if (plugin.getPlayerData().getString(sender.getName() + ".questDesc") != null) {
			player.sendMessage(
					"You already have a quest! Use /edm forfeit to abandon your current quest for 3% of your current points.");
			return true;
		}

		// If above pass, assign quest
		FileConfiguration deityConfig = dh.getDeity(deity);
		// try{
		Map<String, Object> questList = deityConfig.getConfigurationSection(deity + ".quests").getValues(false);
		List<String> keys = new ArrayList<String>(questList.keySet());
		Random generator = new Random();
		String randomKey = keys.get(generator.nextInt(keys.size()));
		ConfigurationSection quest = (ConfigurationSection) questList.get(randomKey);
		plugin.log("The quest string: " + quest.getString("text"));
		resetQuestData(plugin, name);
		// try{
		// transfer data from quest to player. Chunky. Better way?
		ConfigurationSection playerData = plugin.getPlayerData();
		ConfigurationSection itemList = quest.getConfigurationSection("items");
		ConfigurationSection statList = quest.getConfigurationSection("stats");
		// ConfigurationSection entityList =
		// quest.getConfigurationSection("entities");
		if (!(itemList == null)) {
			playerData.set(name + ".questItems", itemList);
		}
		if (!(statList == null)) {
			Map<String, Object> statSection = statList.getValues(false);
			List<String> statKeys = new ArrayList<String>(statSection.keySet());

			for (String statEntry : statKeys) {
				plugin.log(statEntry);
				ConfigurationSection statAccess = statList.getConfigurationSection(statEntry);
				int statAmountNew = statAccess.getInt(".statAmount")
						+ player.getStatistic(Statistic.valueOf(statEntry));
				playerData.set(name + ".questStats." + statEntry + ".statAmount", statAmountNew);
			}
		}
		String questDesc = quest.getString("text");
		playerData.set(name + ".questDesc", questDesc);
		playerData.set(name + ".questStatistic", quest.getString("statistic"));
		playerData.set(name + ".questWorth", quest.getInt("points"));
		plugin.savePlayerData();
		player.sendMessage(ChatColor.ITALIC + questDesc);
		// } catch (NullPointerException e){
		// player.sendMessage(ChatColor.RED+"One of your leader's quests is
		// malformed...contact your dev! Quest "+quest.toString());
		// return false;
		// }
		// } catch(NullPointerException e) {
		// player.sendMessage(ChatColor.RED+"Your leader has no quests set
		// up...contact your dev!");
		// return false;
		// }
		return true;
	}

	public static boolean enquire(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh) {
		// Check if not player
		if (!(sender instanceof Player)) {
			sender.sendMessage("The console cannot accept quests, it is too powerful!");
			return true;
		}

		String deity = dh.getPlayerDeity(sender);

		// Check if no deity assigned
		if (deity == null) {
			sender.sendMessage("You have not yet chosen who to follow!");
			return true;
		}
		String questDesc = plugin.getPlayerData().getString(sender.getName() + ".questDesc");
		if (questDesc != null) {
			sender.sendMessage("You recall what " + deity + " said: " + ChatColor.ITALIC + questDesc);
		} else {
			sender.sendMessage("You have no outstanding quests");
		}
		return true;
	}

	public static boolean submit(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh) {
		boolean statComplete = true;
		boolean itemComplete = true;
		// Check if not player
		if (!(sender instanceof Player)) {
			sender.sendMessage("The console cannot accept quests, it is too powerful!");
			return true;
		}

		Player player = (Player) sender;
		String deity = dh.getPlayerDeity(player);
		String name = player.getName();

		// Check if no deity assigned
		if (deity == null) {
			sender.sendMessage("You have not yet chosen who to follow!");
			return true;
		}

		FileConfiguration playerData = plugin.getPlayerData();
		ConfigurationSection statList = playerData.getConfigurationSection(name + ".questStats");
		ConfigurationSection itemList = playerData.getConfigurationSection(name + ".questItems");
		PlayerInventory inventory = player.getInventory();
		StringBuilder missingItems = new StringBuilder("You do not have all necessary items! Still needed: ");

		// All above passing, first we check for stats...
		try {
			if (!(statList.getValues(false) == null)) {
				Map<String, Object> statSection = statList.getValues(false);
				List<String> statKeys = new ArrayList<String>(statSection.keySet());
				for (String statEntry : statKeys) {
					ConfigurationSection statAccess = statList.getConfigurationSection(statEntry);
					int playerStat = player.getStatistic(Statistic.valueOf(statEntry));
					int neededStat = statAccess.getInt("statAmount");
					if (neededStat > playerStat) {
						statComplete = false;
						break;
					}
				}
			}
		} catch (NullPointerException e) {
			// Rare occasion this happens, it means something's wonky with MC,
			// but no stats are needed. Just let it go.
		}

		// Check if items part of quest is done
		try {
			if (!(itemList.getValues(false) == null)) {
				Map<String, Object> itemSection = itemList.getValues(false);
				List<String> itemKeys = new ArrayList<String>(itemSection.keySet());
				ItemStack heldItem = inventory.getItemInHand();
				if (heldItem != null) {
					for (String itemEntry : itemKeys) {
						ConfigurationSection itemAccess = itemList.getConfigurationSection(itemEntry);
						int itemAmount = itemAccess.getInt("itemAmount");
						if (itemAmount > 0) {
							String damageVal = itemAccess.getString("damageVal");
							if (damageVal != null) {
								if (heldItem != null && heldItem.getType() == Material.valueOf(itemEntry)
										&& heldItem.getDurability() == Short.parseShort(damageVal)) {
									int playerAmount = heldItem.getAmount();
									if(heldItem.getAmount()>itemAmount){
										player.getInventory().getItemInHand()
										.setAmount(player.getInventory().getItemInHand().getAmount() - itemAmount);
										itemAccess.set("itemAmount", 0);
									//Minecraft's handling of tools is...weird. So we have this block just in case.
									} else if(heldItem.getAmount()==itemAmount){
										player.setItemInHand(null);
										itemAccess.set("itemAmount", 0);
									} else {
										player.setItemInHand(null);
										itemAccess.set("itemAmount", itemAmount-playerAmount);
									}
								}
							} else {
								//Just a slightly altered version of the previous block
								//remember to change this if you change that and vice-versa.
								if (heldItem != null && heldItem.getType() == Material.valueOf(itemEntry)) {
									int playerAmount = heldItem.getAmount();
									if(heldItem.getAmount()>itemAmount){
										player.getInventory().getItemInHand()
										.setAmount(player.getInventory().getItemInHand().getAmount() - itemAmount);
										itemAccess.set("itemAmount", 0);
									} else if(heldItem.getAmount()==itemAmount){
										player.setItemInHand(null);
										itemAccess.set("itemAmount", 0);
									} else {
										player.setItemInHand(null);
										itemAccess.set("itemAmount", itemAmount-playerAmount);
									}
								}
							}
						}
					}
				}
				// Now that we've taken items, check to see if we're done yet...
				// Basically we look at each item and, if it needs 0 items more, it passes.
				for (String itemEntry : itemKeys) {
					ConfigurationSection itemAccess = itemList.getConfigurationSection(itemEntry);
					int itemAmount = itemAccess.getInt("itemAmount");
					if (itemAmount > 0) {
						itemComplete = false;
						missingItems.append(itemEntry+" ("+(itemAmount)+"), ");
					}
				}
			}
		} catch (NullPointerException e) {
			plugin.log("Autoclear items");
		}

		//Now that we've decided whether things are complete, respond:
		if (!statComplete) {
			player.sendMessage("You have not yet completed a portion of your task!");
		}
		
		if (!itemComplete) {
			player.sendMessage(missingItems.toString()
					.substring(0, missingItems.toString().length()-2)); //chop off comma
		}
		if (statComplete && itemComplete) {
			FileConfiguration deityConfig = dh.getDeity(deity);
			try {
				Map<String, Object> completionList = deityConfig.getConfigurationSection(deity + ".quest complete")
						.getValues(false);
				Object[] values = completionList.values().toArray();
				Random generator = new Random();
				sender.sendMessage(ChatColor.ITALIC + ((String) values[generator.nextInt(values.length)]));
			} catch (NullPointerException e) {
				sender.sendMessage(
						ChatColor.RED + "Your leader has no dialog set up for complete quests...contact your dev!");
				return false;
			}
			int pointsEarned = playerData.getInt(name + ".questWorth");
			player.sendMessage("You have earned " + pointsEarned + " points!");
			int newPoints = playerData.getInt(name + ".points") + pointsEarned;
			playerData.set(name + ".points", newPoints);
			dh.deityPointsSet(deity, dh.deityPointsGet(deity) + pointsEarned);
			plugin.savePlayerData();
			resetQuestData(plugin, player.getName());
			return true;
		}
		return true;
	}

	public static boolean forfeit(EverydayMiracles plugin, CommandSender sender, DataHandler dh) {
		// Check if not player
		if (!(sender instanceof Player)) {
			sender.sendMessage("The console never surrenders!");
			return true;
		}
		String deity = dh.getPlayerDeity(sender);
		// Check if no deity assigned
		if (deity == null) {
			sender.sendMessage("You have not yet chosen who to follow!");
			return true;
		}
		FileConfiguration playerData = plugin.getPlayerData();
		int points = playerData.getInt(sender.getName() + ".points");
		int lostPoints = (int) (points * 0.03);
		playerData.set(sender.getName() + ".points", points - lostPoints);
		plugin.savePlayerData();
		resetQuestData(plugin, sender.getName());
		sender.sendMessage("You have forfeited your current quest and lost " + lostPoints + " points.");
		return true;
	}

	public static boolean points(EverydayMiracles plugin, CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("The console is pointless. Wait...");
			return true;
		}
		sender.sendMessage("You have " + plugin.getPlayerData().getInt(sender.getName() + ".points") + " points.");
		return true;
	}

	public static void resetQuestData(EverydayMiracles plugin, String name) {
		FileConfiguration playerData = plugin.getPlayerData();
		playerData.set(name + ".questDesc", null);
		playerData.set(name + ".questStats", null);
		playerData.set(name + ".questStatStart", null);
		playerData.set(name + ".questStatNeed", null);
		playerData.set(name + ".questItems", null);
		playerData.set(name + ".questItemAmount", null);
		playerData.set(name + ".questWorth", null);
		playerData.set(name + ".questWorth", null);
		plugin.savePlayerData();
	}

	public static boolean request(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh) {
		// Check if not player
		if (!(sender instanceof Player)) {
			sender.sendMessage("The console already owns everything!");
			return true;
		}

		String deity = dh.getPlayerDeity(sender);

		// Check if no deity assigned
		if (deity == null) {
			sender.sendMessage("You have not yet chosen who to follow!");
			return true;
		}
		FileConfiguration deityConfig = dh.getDeity(deity);
		Map<String, Object> requestList = deityConfig.getConfigurationSection(deity + ".requests").getValues(false);
		if (args.length > 1 && Requester.checkRequest(args[1].toUpperCase())
				&& requestList.containsKey(args[1].toLowerCase())) {
			Requester.invoke(plugin, sender, args[1].toUpperCase());
			return true;
		} else {
			Set<String> spells = requestList.keySet();
			StringBuilder out = new StringBuilder("Available requests (and costs) are: ");
			for (String spell : spells) {
				spell = spell.toUpperCase();
				if (Requester.checkRequest(spell)) {
					out.append(spell.toLowerCase() + "(" + Requester.requestCost(plugin, sender, spell) + "), ");
				} else {
					plugin.log("You cannot make that request to your deity!");
				}
			}
			sender.sendMessage(out.toString());
			return true;
		}
	}

	public static boolean rankings(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh) {
		@SuppressWarnings("unchecked") // TODO How 2 assert
		List<String> rankings = (List<String>) plugin.getConfig().get(".Rankings");
		sender.sendMessage("-----------------------------------------------------");
		StringBuilder currentRanking = new StringBuilder("Current ranking: ");
		if (rankings != null) {
			for (String deity : rankings) {
				currentRanking
						.append(ChatColor.valueOf(dh.getDeity(deity).getString(deity + ".chatcolor")) + deity + ", ");
			}
		} else {
			currentRanking.append("None on record!");
		}
		currentRanking.setLength(currentRanking.length() - 2);
		sender.sendMessage(currentRanking.toString());
		sender.sendMessage("-----------------------------------------------------");
		StringBuilder upcomingRanking = new StringBuilder("Upcoming ranking: ");
		List<String> ranking = new ArrayList<String>(dh.deitiesByPoints().values());
		for (String deity : ranking) {
			plugin.log("Deity in rankings: " + deity);
			upcomingRanking
					.append(ChatColor.valueOf(dh.getDeity(deity).getString(deity + ".chatcolor")) + deity + ", ");
		}
		upcomingRanking.setLength(upcomingRanking.length() - 2);
		sender.sendMessage(upcomingRanking.toString());
		sender.sendMessage("-----------------------------------------------------");
		return true;
	}

	@SuppressWarnings("deprecation")
	public static boolean givePoints(EverydayMiracles plugin, CommandSender sender, String[] args) {
		if (args.length > 2 && NumberUtils.isNumber(args[2])) {
			int addPoints = Integer.parseInt(args[2]);
			FileConfiguration playerdata = plugin.getPlayerData();
			int newPoints = playerdata.getInt(args[1] + ".points") + addPoints;
			if (newPoints < 0) {
				newPoints = 0;
			}
			;
			playerdata.set(args[1] + ".points", newPoints);
			plugin.savePlayerData();
			Player player = plugin.getServer().getPlayer(args[1]);
			if (addPoints >= 0) {
				player.sendMessage("You have been granted " + addPoints + " points!");
			} else {
				player.sendMessage("You have had " + addPoints + " points deducted!");
			}
			sender.sendMessage("Player " + args[1] + "given points: " + addPoints);
		} else {
			plugin.displayCommandsA(sender);
		}
		return true;
	}

	public static boolean setDeity(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh) {
		if (args.length > 2) {
			ArrayList<String> deities = dh.getDeities();
			if (deities.contains(args[2])) {
				FileConfiguration playerdata = plugin.getPlayerData();
				playerdata.set(args[1] + ".deity", args[2]);
				plugin.savePlayerData();
			} else {
				sender.sendMessage("Valid deities: " + deities);
			}
		} else {
			plugin.displayCommandsA(sender);
		}
		return true;
	}

	public static boolean getPoints(EverydayMiracles plugin, CommandSender sender, String[] args) {
		if (args.length > 1) {
			FileConfiguration playerdata = plugin.getPlayerData();
			int points = playerdata.getInt(args[1] + ".points");
			sender.sendMessage(args[1] + " has " + points + " points.");
		} else {
			plugin.displayCommandsA(sender);
		}
		return true;
	}

	public static boolean conquest(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh) {
		FileConfiguration config = plugin.getConfig();
		List<String> values = new ArrayList<String>(dh.deitiesByPoints().values());
		if (values.size() > 3) {
			config.set(".Rankings", values.subList(0, 3));
		} else {
			config.set(".Rankings", values);
		}
		plugin.saveConfig();
		List<String> deities = dh.getDeities();
		for (String deity : deities) {
			dh.deityPointsSet(deity, 0);
		}
		return true;
	}

	public static boolean setIslands(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh) {
		if (args.length > 3 && NumberUtils.isNumber(args[2]) && NumberUtils.isNumber(args[3])) {
			FileConfiguration playerdata = plugin.getPlayerData();
			int newProtected = playerdata.getInt(args[1] + ".protectedIslands") + Integer.parseInt(args[3]);
			int newTotal = playerdata.getInt(args[1] + ".totalIslands") + Integer.parseInt(args[3])
					+ Integer.parseInt(args[2]);
			playerdata.set(args[1] + ".protectedIslands", newProtected);
			playerdata.set(args[1] + ".totalIslands", newTotal);
			plugin.savePlayerData();
			Player player = plugin.getServer().getPlayer(args[1]);
			sender.sendMessage(
					"Player " + args[1] + "island counts now protected: " + newProtected + ", total: " + newTotal);
		} else {
			plugin.displayCommandsA(sender);
		}
		return true;
	}
}
