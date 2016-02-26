package kev575.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import kev575.json.KevsPermsGroup;
import kev575.json.KevsPermsPlayer;
import kev575.obf.PluginSetInPerms;
import kev575.obf.PluginSetOutConfig;
import net.milkbowl.vault.chat.Chat;

public class KevsPermissions extends JavaPlugin implements Listener {

	public static KevsPermsManager config;
	public String pre;
	public static Object vaultChat;
	HashMap<UUID, PermissionAttachment> atts = new HashMap<UUID, PermissionAttachment>();
	
	@Override
	public void onEnable() {
		config = new KevsPermsManager(this);
		pre = "�6KevsPermission �8> �7";
		saveDefaultConfig();
		PluginSetInPerms.a();
		PluginSetOutConfig.a(getConfig());
		PluginSetOutConfig.b(this);
		pre = config.getConfig().isString("prefix") ? config.getConfig().getString("prefix") : pre;
		Bukkit.getPluginManager().registerEvents(this, this);
		reloadAllPlayers();
		if (config.getConfig().isBoolean("enablemanagers") && config.getConfig().getBoolean("enablemanagers") && config.getConfig().getBoolean("usevault")) {
			setupProvider();
		}
		if (config.getConfig().isBoolean("enablemanagers") && config.getConfig().isBoolean("scoreboardmanager")) {
			if (config.getConfig().getBoolean("enablemanagers")) {
				Bukkit.getPluginManager().registerEvents(new ChatManager(), this);
//				if (config.getConfig().getBoolean("scoreboardmanager"))
//				Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
//					ScoreboardManager man = Bukkit.getScoreboardManager();
//					Scoreboard sb = man.getMainScoreboard();
//					@SuppressWarnings("deprecation")
//					public void run() {
//						for (String groupName : config.getGroups().getKeys(false)) {
//							KevsPermsGroup gr = config.getGroup(groupName);
//							gr.fix();
//							try {
//								Team current;
//								if (sb.getTeam(groupName) == null)
//									current = sb.registerNewTeam(groupName);
//								else
//									current = sb.getTeam(groupName);
//								current.setPrefix(gr.getPrefix().replace("&", "�"));
//								for (Player p : Bukkit.getOnlinePlayers()) {
//									KevsPermsPlayer player = config.getPlayer(p.getUniqueId());
//									if (player.getMainGroup().equals(groupName)) {
//										if (!current.getPlayers().contains(p))current.addPlayer(p);
//									} else {
//										if (current.getPlayers().contains(p))current.removePlayer(p);
//									}
//								}
//							} catch (Exception e) { continue; }
//						}
//					}
//				}, 20, 20);
			}
		}
	}
	
	private void setupProvider() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return;
		}
		try {
			RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
	        if (chatProvider != null) {
	            vaultChat = chatProvider.getProvider();
	        }
		} catch (Exception e) {}
	}

	@Override
	public void onDisable() {
		for (PermissionAttachment at : atts.values()) {
			disablePermission(at);
		}
		atts = null; config = null;
	}
	
	private void disablePermission(PermissionAttachment at) {
		for (String perm : at.getPermissions().keySet()) {
			at.unsetPermission(perm);
		}
	}
	
	public static final String[] COMMANDS = { "help", "setgroup", "insertgroup", "setsuffix", "creategroup", "removegroup", "setprefix", "setperm", "copyto", "listgroup", "getgroup", "reload"};
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		ArrayList<String> l = new ArrayList<String>();
		if (cmd.getName().equalsIgnoreCase("kevspermissions")) {
			List<String> completions = new ArrayList<String>();
			if (args.length == 1) {
				String partialCommand = args[0];
				List<String> commands = new ArrayList<String>(Arrays.asList(COMMANDS));
				StringUtil.copyPartialMatches(partialCommand, commands, completions);
				Collections.sort(completions);
				return completions;
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("setgroup")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						l.add(p.getName());
					}
				} else if (args[0].equalsIgnoreCase("setwgroup")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						l.add(p.getName());
					}
				} else if (args[0].equalsIgnoreCase("listgroup")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						l.add(p.getName());
					}
				} else if (args[0].equalsIgnoreCase("removegroup") || args[0].equalsIgnoreCase("copyto") || args[0].equalsIgnoreCase("getgroup")) {
					String partialCommand = args[0];
					Set<String> commands = config.getGroups().getValues(false).keySet();
					StringUtil.copyPartialMatches(partialCommand, commands, completions);
					Collections.sort(completions);
					return completions;
				} else if (args[0].equalsIgnoreCase("setprefix") || args[0].equalsIgnoreCase("setsuffix")) {
					String partialCommand = args[0];
					Set<String> commands = config.getGroups().getValues(false).keySet();
					StringUtil.copyPartialMatches(partialCommand, commands, completions);
					Collections.sort(completions);
					return completions;
				} else if (args[0].equalsIgnoreCase("setperm")) {
					String partialCommand = args[0];
					Set<String> commands = config.getGroups().getValues(false).keySet();
					StringUtil.copyPartialMatches(partialCommand, commands, completions);
					Collections.sort(completions);
					return completions;
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("setgroup") || args[0].equalsIgnoreCase("copyto")) {
					String partialCommand = args[0];
					Set<String> commands = config.getGroups().getValues(false).keySet();
					StringUtil.copyPartialMatches(partialCommand, commands, completions);
					Collections.sort(completions);
					return completions;
				} else if (args[0].equalsIgnoreCase("setwgroup")) {
					String partialCommand = args[0];
					Set<String> commands = config.getGroups().getValues(false).keySet();
					StringUtil.copyPartialMatches(partialCommand, commands, completions);
					Collections.sort(completions);
					return completions;
				} else if (args[0].equalsIgnoreCase("setprefix")) {
					l.add("<Prefix...>");
				} else if (args[0].equalsIgnoreCase("setsuffix")) {
					l.add("<Suffix...>");
				} else if (args[0].equalsIgnoreCase("setperm")) {
					l.add("<Permission>");
				}
			}
			
		}
		return l;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender se, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("kevspermissions")) {
			if (args.length == 0) {
				se.sendMessage(pre + "�6KevsPermissions�7 created by �aKev575�7 v" + getDescription().getVersion());
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					if (!se.hasPermission("kp.help")) {
						se.sendMessage(noPerm());
						return true;
					}
					se.sendMessage(pre + "Command Help:");
					se.sendMessage("/" + label + " setgroup <Player> <Group> �8�l- �7Set the group of a player");
					se.sendMessage("/" + label + " creategroup <Group> �8�l- �7Creates a group");
					se.sendMessage("/" + label + " removegroup <Group> �8�l- �7Removes a group");
					se.sendMessage("/" + label + " setprefix <Group> <Prefix...> �8�l- �7Sets the prefix of a group");
					se.sendMessage("/" + label + " setsuffix <Group> <Prefix...> �8�l- �7Sets the suffix of a group");
					se.sendMessage("/" + label + " setperm <Group> <World>:<Permission> �8�l- �7Toggle a permissions of a group");
					se.sendMessage("/" + label + " listgroup <Player> �8�l- �7Lists every group of a player");
					se.sendMessage("/" + label + " copyto <GroupFrom> <GroupTo> �8�l- �7Copies the permissions from GroupFrom to GroupTo");
					se.sendMessage("/" + label + " getgroup <Group> �8�l- �7Lists the permissions, the suffix and the prefix");
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (!se.hasPermission("kp.reload")) {
						se.sendMessage(noPerm());
						return true;
					}
					reloadConfig();
					se.sendMessage("�6KevsPermissions�8> �aReloaded config. �7�oIf you want to reload the groups.yml or the players.yml restart the server (or reload it)!");
				} else {
					se.sendMessage(pre + "Use /" + label + " help");
				}
				return true;
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("creategroup")) {
					if (!se.hasPermission("kp.creategroup")) {
						se.sendMessage(noPerm());
						return true;
					}
					if (config.getGroup(args[1]) != null) {
						se.sendMessage(pre + "The group \"" + args[1] + "\" already exists.");
						return true;
					}
					
					KevsPermsGroup group = new KevsPermsGroup();
					group.fix();
					config.saveGroup(args[1], group);
					
					se.sendMessage(pre + "�aCreated group �e" + args[1] + "�a!");
					
					reloadAllPlayers();
				} else if (args[0].equalsIgnoreCase("removegroup")) {
					if (!se.hasPermission("kp.removegroup")) {
						se.sendMessage(noPerm());
						return true;
					}
					if (config.getGroup(args[1]) == null) {
						se.sendMessage(pre + "The group \"" + args[1] + "\" does not exist. :(");
						return true;
					}
					config.saveGroup(args[1], null);
					se.sendMessage(pre + "�aRemoved group �e" + args[1] + "�a!");
				} else if (args[0].equalsIgnoreCase("groupinfo")) {
					if (!se.hasPermission("kp.groupinfo")) {
						se.sendMessage(noPerm());
						return true;
					}
					if (config.getGroup(args[1]) == null) {
						se.sendMessage(pre + "The group \"" + args[1] + "\" does not exist. :(");
						return true;
					}
					
					KevsPermsGroup group = config.getGroup(args[1]);
					se.sendMessage("�eGroup�8: �7" + args[1]);
					se.sendMessage("  �ePrefix�8: �7" + group.getPrefix() + " �8(�r" + group.getPrefix().replace("&", "�") + "�8)");
					se.sendMessage("  �eSuffix�8: �7" + group.getSuffix() + " �8(�r" + group.getSuffix().replace("&", "�") + "�8)");
					se.sendMessage("  �ePermissions�8:");
					for (ArrayList<String> perm : group.getPermissions().values()) {
						se.sendMessage("    �7- �a" + perm);
					}
					se.sendMessage("  �eInherits�8:");
					for (String perm : group.getInherits()) {
						se.sendMessage("    �7- �a" + perm);
					}
				} else if (args[0].equalsIgnoreCase("listgroup")) {
					if (!se.hasPermission("kp.listgroup")) {
						se.sendMessage(noPerm());
						return true;
					}
					if (!(Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore() || Bukkit.getOfflinePlayer(args[1]).isOnline())) {
						se.sendMessage(pre + "�cThat player hasn't played before or is not online!");
						return true;
					}
					UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
					se.sendMessage(pre + "�3Group(s) of " + args[1] + ":");
					se.sendMessage("" + config.getPlayer(uuid));
					for (String group : config.getPlayer(uuid).getGroups()) {
						KevsPermsGroup group2 = config.getGroup(group);
						if (group != null && group2 != null) {
							se.sendMessage("  �eGroup�8: �7" + group);
							se.sendMessage("    �ePrefix�8: �7" + group2.getPrefix() + " �8(�r" + group2.getPrefix().replace("&", "�") + "�8)");
							se.sendMessage("    �eSuffix�8: �7" + group2.getSuffix() + " �8(�r" + group2.getSuffix().replace("&", "�") + "�8)");
						}
					}
				} else {
					se.sendMessage(pre + "Use /" + label + " help");
				}
			} else if (args.length>=3 && args[0].equalsIgnoreCase("setprefix")) {
				if (!se.hasPermission("kp.setprefix")) {
					se.sendMessage(noPerm());
					return true;
				}
				if (config.getGroup(args[1]) == null) {
					se.sendMessage(pre + "The group \"" + args[1] + "\" doesn't exist. :(");
					return true;
				}
				String s = "";
				for (int i = 2; i < args.length; i++) {
					s += args[i] + " ";
				}
				s = s.substring(0, s.length()-1);
				KevsPermsGroup group = config.getGroup(args[1]);
				group.setPrefix(s);
				config.saveGroup(args[1], group);
				se.sendMessage(pre + "�aPrefix of �e" + args[1] + " �achanged to �r" + s);
				return true;
			} else if (args.length>=3 && args[0].equalsIgnoreCase("setsuffix")) {
				if (!se.hasPermission("kp.setsuffix")) {
					se.sendMessage(noPerm());
					return true;
				}
				if (config.getGroup(args[1]) == null) {
					se.sendMessage(pre + "The group \"" + args[1] + "\" doesn't exist. :(");
					return true;
				}
				String s = "";
				for (int i = 2; i < args.length; i++) {
					s += args[i] + " ";
				}
				s = s.substring(0, s.length()-1);
				KevsPermsGroup group = config.getGroup(args[1]);
				group.setSuffix(s);
				config.saveGroup(args[1], group);
				se.sendMessage(pre + "�aSuffix of �e" + args[1] + " �achanged to �r" + s);
				return true;
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("setgroup")) {
					if (!se.hasPermission("kp.setgroup")) {
						se.sendMessage(noPerm());
						return true;
					}
					if (config.getGroup(args[2]) == null) {
						se.sendMessage(pre + "The group \"" + args[2] + "\" doesn't exist. :(");
						return true;
					}
					UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
					KevsPermsPlayer player = config.getPlayer(uuid);
					ArrayList<String> groups = player.getGroups();
					if (groups.contains(args[2])) {
						groups.remove(args[2]);
						se.sendMessage(pre + "�aRemoved group �e" + args[2] + "�a from�e " + args[1] + "�a!");
					} else {
						groups.add(args[2]);
						se.sendMessage(pre + "�aAdded group �e" + args[2] + "�a from�e " + args[1] + "�a!");
					}
					player.setGroups(groups);
					config.savePlayer(uuid, player);
					reloadAllPlayers();
				} else if (args[0].equalsIgnoreCase("insertgroup")) {
					if (!se.hasPermission("kp.insertgroup")) {
						se.sendMessage(noPerm());
						return true;
					}
					if (config.getGroup(args[2]) == null) {
						se.sendMessage(pre + "The group \"" + args[2] + "\" doesn't exist. :(");
						return true;
					}
					UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
					KevsPermsPlayer player = config.getPlayer(uuid);
					ArrayList<String> groups = player.getGroups();
					if (groups.contains(args[2])) {
						se.sendMessage(pre + "�cPlease use �9/" + label + " setgroup " + args[2]);
						return true;
					} else {
						groups.add(0, args[2]);
						se.sendMessage(pre + "�aAdded 1. group �e" + args[2] + "�a from�e " + args[1] + "�a!");
					}
					player.setGroups(groups);
					config.savePlayer(uuid, player);
					reloadAllPlayers();
				} else if (args[0].equalsIgnoreCase("setperm")) {
					if (!se.hasPermission("kp.setperm")) {
						se.sendMessage(noPerm());
						return true;
					}
					if (config.getGroup(args[1]) == null) {
						se.sendMessage(pre + "The group \"" + args[1] + "\" doesn't exist. :(");
						return true;
					}
					KevsPermsGroup group = config.getGroup(args[1]);
					HashMap<String, ArrayList<String>> perms = group.getPermissions();
					ArrayList<String> s = perms.get(args[2].split(":")[0]);
					perms.remove(args[2].split(":")[0]);
					if (s.contains(args[2].split(":")[1])) {
						se.sendMessage(pre + "�aAdded permission �e" + args[2].split(":")[1] + "�a at world �e" + args[2].split(":")[0] + "�a to group �e" + args[1] + "�a!");
						s.remove(args[2].split(":")[1]);
					} else {
						s.add(args[2].split(":")[1]);
						se.sendMessage(pre + "�aRemoved permission �e" + args[2].split(":")[1] + "�a at world �e" + args[2].split(":")[0] + "�a from group �e" + args[1] + "�a!");
					}
					perms.put(args[2].split(":")[0], s);
					group.setPermissions(perms);
					config.saveGroup(args[1], group);
					
					reloadAllPlayers();
				} else if (args[0].equalsIgnoreCase("setplayerperm")) {
					if (!se.hasPermission("kp.setplayerperm")) {
						se.sendMessage(noPerm());
						return true;
					}
					UUID uniqueID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
					
					KevsPermsPlayer player = config.getPlayer(uniqueID);
					
					HashMap<String, ArrayList<String>> perms = player.getPermissions();
					ArrayList<String> s = perms.get(args[2].split(":")[0]);
					perms.remove(args[2].split(":")[0]);
					if (s.contains(args[2].split(":")[1])) {
						se.sendMessage(pre + "�aAdded permission �e" + args[2].split(":")[1] + "�a at world �e" + args[2].split(":")[0] + "�a to player �e" + args[1] + "�a!");
						s.remove(args[2].split(":")[1]);
					} else {
						s.add(args[2].split(":")[1]);
						se.sendMessage(pre + "�aRemoved permission �e" + args[2].split(":")[1] + "�a at world �e" + args[2].split(":")[0] + "�a from player �e" + args[1] + "�a!");
					}
					perms.put(args[2].split(":")[0], s);
					player.setPermissions(perms);
					
					reloadAllPlayers();
				} else if (args[0].equalsIgnoreCase("copyto")) {
					if (!se.hasPermission("kp.copyto")) {
						se.sendMessage(noPerm());
						return true;
					}
					
					KevsPermsGroup groupFrom = config.getGroup(args[1]);
					KevsPermsGroup groupTo = config.getGroup(args[2]);
					if (args[1].equals(args[2])) {
						se.sendMessage(pre + "�cThat groups are equals...");
						return true;
					}
					if (groupTo == null) {
						groupTo = new KevsPermsGroup();
						groupTo.fix();
					}
					if (groupFrom == null) {
						se.sendMessage(pre + "�cThe group " + args[1] + " is not valid.");
						return true;
					}
					
					groupTo.setPermissions(groupFrom.getPermissions());
					config.saveGroup(args[2], groupTo);
					se.sendMessage(pre + "�aSet every permission from group �e" + args[1] + " �ato group �e" + args[2] + "�a!");
					return true;
				} else {
					se.sendMessage(pre + "Use /" + label + " help");
				}
			} else {
				se.sendMessage(pre + "Use /" + label + " help");
			}
		}
		return true;
	}
	
	private void reloadAllPlayers() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			join(new PlayerJoinEvent(player, ""));
		}
	}

	private String noPerm() {
		return "�6KevsPermissions �8> �cYou don't have the permission(s) to do that.";
	}

	@EventHandler
	public void join(final PlayerJoinEvent e) {
		if (config.getPlayer(e.getPlayer().getUniqueId()) == null) {
			KevsPermsPlayer p = new KevsPermsPlayer();
			p.fix();
			config.savePlayer(e.getPlayer().getUniqueId(), p);
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (atts.containsKey(e.getPlayer().getUniqueId())) {
					disablePermission(atts.get(e.getPlayer().getUniqueId()));
					atts.remove(e.getPlayer().getUniqueId());
				}
				PermissionAttachment at = e.getPlayer().addAttachment(KevsPermissions.getPlugin(KevsPermissions.class), 1728000);
				applyToAt(at, e.getPlayer().getUniqueId(), e.getPlayer().getWorld().getName(), null);
				KevsPermsPlayer player = config.getPlayer(e.getPlayer().getUniqueId());
				ArrayList<String> global = player.getPermissions().get("*");
				for (String perm : global) {
					at.setPermission(perm.startsWith("-") ? perm.substring(1, perm.length()) : perm, perm.startsWith("-") ? false : true);
				}
				ArrayList<String> worldPerms = player.getPermissions().get(e.getPlayer().getWorld().getName());
				if (worldPerms != null) {
					for (String perm : worldPerms) {
						at.setPermission(perm.startsWith("-") ? perm.substring(1, perm.length()) : perm, perm.startsWith("-") ? false : true);
					}
				}
				atts.put(e.getPlayer().getUniqueId(), at);
			}
		});
		t.start();
	}
	
	protected void applyToAt(PermissionAttachment at, UUID id, String worldName, ArrayList<String> inherits) {
		if (id != null)
		for (String groupName : config.getPlayer(id).getGroups()) {
			try {
				KevsPermsGroup group = config.getGroup(groupName);
				if (group != null) {
					for (String perm : group.getPermissions().get("*")) {
						at.setPermission(perm.startsWith("-") ? perm.substring(1, perm.length()) : perm, perm.startsWith("-") ? false : true);
					}
					applyToAt(at, null, null, group.getInherits());
					if (worldName != null) {
						ArrayList<String> worldPerms = group.getPermissions().get(worldName);
						if (worldPerms != null) {
							for (String perm : worldPerms) {
								at.setPermission(perm.startsWith("-") ? perm.substring(1, perm.length()) : perm, perm.startsWith("-") ? false : true);
							}
						}
					}
				}
			} catch (Exception e2) {}
		}
		else {
			for (String groupName : inherits) {
				KevsPermsGroup group = config.getGroup(groupName);
				if (group != null)
					for (String perm : group.getPermissions().get("*")) {
						at.setPermission(perm.startsWith("-") ? perm.substring(1, perm.length()) : perm, perm.startsWith("-") ? false : true);
					}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (atts.containsKey(e.getPlayer().getUniqueId())) {
			disablePermission(atts.get(e.getPlayer().getUniqueId()));
			atts.remove(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		join(new PlayerJoinEvent(e.getPlayer(), null));
	}
	
	/*@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (config.getCfg().getBoolean("antibuild")) {
			if (!e.getPlayer().hasPermission("kp.antibuild")) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(noPerm());
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (config.getCfg().getBoolean("antibuild")) {
			if (!e.getPlayer().hasPermission("kp.antibuild")) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(noPerm());
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (config.getCfg().getBoolean("antibuild")) {
			if (!e.getPlayer().hasPermission("kp.antibuild")) {
				e.setCancelled(true);
				e.setUseItemInHand(Result.DENY);
				e.setUseInteractedBlock(Result.DENY);
				e.getPlayer().sendMessage(noPerm());
			}
		}
	}
	
	@EventHandler
	public void onPlayerDmg(EntityDamageByEntityEvent e) {
		if (config.getCfg().getBoolean("antibuild")) {
			if (e.getDamager() instanceof Player) {
				if (!((Player)e.getDamager()).hasPermission("kp.antibuild")) {
					e.setCancelled(true);
				}
			}
			if (e.getEntity() instanceof Player) {
				if (!((Player)e.getEntity()).hasPermission("kp.anitbuild")) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onShear(PlayerShearEntityEvent e) {
		if (config.getCfg().getBoolean("antibuild")) {
			if (!e.getPlayer().hasPermission("kp.antibuild"))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void on(PlayerJoinEvent e) {
		if (config.getCfg().getBoolean("antibuild")) {
			if (!e.getPlayer().hasPermission("kp.antibuild"))
				e.getPlayer().sendMessage("�6KevsPermissions �8> �cYou currently lack the permission �7\"�akp.antibuild�7\"�c!");
		}
	}*/
}
 