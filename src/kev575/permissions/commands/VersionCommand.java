package kev575.permissions.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import kev575.permissions.PermissionsConstants;
import kev575.permissions.PermissionsPlugin;

public class VersionCommand extends PermissionsExecutor {

	public VersionCommand() {
		super("version", null);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		PluginDescriptionFile pdf = PermissionsPlugin.getInstance().getDescription();
		sender.sendMessage(PermissionsConstants.PREFIX + "Version " + pdf.getVersion() + " created by " + pdf.getAuthors().get(0));
	}
	
	@Override
	public String getPermission() {
		return "kp.version";
	}

	@Override
	public List<String> tabComplete(CommandSender arg0, Object[] remove) {
		// stays empty!
		return null;
	}

}
