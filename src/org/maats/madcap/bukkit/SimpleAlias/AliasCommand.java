package org.maats.madcap.bukkit.SimpleAlias;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class AliasCommand implements CommandExecutor {

	private final SimpleAlias plugin;
	
	public AliasCommand(SimpleAlias plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		Player player = null;
		String name = "";
		String alias = "";

		if (cmd.getName().compareToIgnoreCase("alias") == 0)
		{
			// do nothing if /alias was used by a non-player
			if(!(sender instanceof Player)){
				sender.sendMessage("Only players can use this command!");
				return true;
			}

			player = (Player)sender;
			name=player.getName();

			if(SimpleAlias.permissions != null){
				// if permissions is not null, look in the node SimpleAlias.*
				if (!SimpleAlias.permissions.has(player, "SimpleAlias.*")) {
					player.sendMessage("You do not have permission to use "+commandLabel+".");
					return true;
				}
			}

			// no args means clear any aliases			
			if(args.length==0)
			{
				if(plugin.names.remove(player.getName())!=null){
					player.setDisplayName(player.getName());
					plugin.saveAliases();
					plugin.getServer().broadcastMessage("Player "+name+" is no longer using an alias.");
					SimpleAlias.print(name+" has cleared their alias.");
				}
				else{
					player.sendMessage("No current alias to clear. Use /alias <NICKNAME> to set your alias.");
				}
				return true;
			}

			if(args.length==1){

				alias = args[0];
				
				// for now limit aliases to 12 characters
				if(alias.length()>12){
					player.sendMessage("Alias Failure: Aliases are limited to 12 characters.");
					return true;
				}
				
				// make sure that alias isn't already in use (either as an alias or full login name)
				if(plugin.names.containsValue(alias)){
					// special case, if that player is already using that alias
					if(plugin.names.get(name).equalsIgnoreCase(alias)){
						player.sendMessage("Alias Failure: You are already using "+alias+" as an alias.");
						return true;
					}
					
					// otherwise, it's in use by someone else
					player.sendMessage("Alias Failure: That alias is already in use.");
					return true;
				} 

				// make sure no one has that alias as a login  name
				if(plugin.playerDir!=null && plugin.playerDir.exists() && plugin.isPlayerName(alias)){
					player.sendMessage("Alias Failure: You are not allowed to use that alias.");
					return true;
				}
				
				// check alias against list of banned aliases in config file
				if(plugin.isBanned(alias)){
					player.sendMessage("Alias Failure: You are not allowed to use that alias.");
					return true;
				}
				
				// only alphanumeric aliases are allowed for now
				if(alias.matches("[a-zA-Z0-9]*")) {
					player.setDisplayName(alias);
					plugin.names.put(name, alias);
					plugin.saveAliases();
					plugin.getServer().broadcastMessage("Player "+name+" is now aliased as "+alias);
					//player.sendMessage("Alias updated to "+alias);
					SimpleAlias.print(name+" has set their alias to "+alias);
					return true;
				}
				else{
					player.sendMessage("Alias Failure: Aliases must be only one word and must be alphanumeric.");
					return true;
				}
			}

			// invalid usage if more than 1 argument
			if(args.length>1){
				player.sendMessage("Unrecognized /alias use. Use /alias to clear your alias");
				player.sendMessage(" or say /alias <NICKNAME> to set your alias.");
				player.sendMessage(" Aliases must be one word and must be alphanumeric.");
				return true;
			}

			SimpleAlias.print("this shouldn't happen, please notify developer");
			return true;
		}

		return false;
	}

	
}
