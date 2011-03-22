package org.maats.madcap.bukkit.SimpleAlias;

import java.util.Map;

import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;





/**
 * Handle events for all Player related events
 * @author madcap
 */
public class SimpleAliasPlayerListener extends PlayerListener {
	
	private final SimpleAlias plugin;

	public SimpleAliasPlayerListener(SimpleAlias instance) {
		plugin = instance;
	}    

	@Override
    public void onPlayerJoin(PlayerEvent event) {
    	// when a player joins, if they have an alias set, update their display name
        Player player = event.getPlayer();
        String alias = plugin.names.get(player.getName());
            
        if(alias!=null){

        	// make sure the alias is still allowed
        	// this could happen if a player sets their alias and then a player with that
        	// actual name joins the server for the first time later
        	// or the name was added to the banned aliases list after being set by the player
        	if((plugin.playerDir!=null && plugin.playerDir.exists() && plugin.isPlayerName(alias)) || plugin.isBanned(alias)){
        		plugin.names.remove(player.getName());
        		plugin.saveAliases();
        		//plugin.getServer().broadcastMessage("Player "+player.getName()+" is no longer using an alias.");
        		player.sendMessage("Alias Failure: You are no longer allowed allowed to use "+alias+" as an alias.");
        		SimpleAlias.print(player.getName()+" has had their alias "+alias+" cleared because it is no longer allowed.");

        		// this doesn't work if you call it too early, set up a delay to change the display name
        		//player.setDisplayName(player.getName());
        		
        		// 20 ticks is aprox 1 second
        		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new DelaySetDisplayName(player), 60);
        	}
        	
			if(SimpleAlias.permissions == null){
				// permissions not required, go ahead and set the alias
				player.setDisplayName(alias);
	        	SimpleAlias.print(player.getName()+" has connected, aliased as "+alias);
			}
			else{
				// if permissions is not null, look in the node SimpleAlias.*
				if (SimpleAlias.permissions.has(player, "SimpleAlias.*")){
					// if player has permissions, set the alias
					player.setDisplayName(alias);
					SimpleAlias.print(player.getName()+" has connected, aliased as "+alias);
				}
				else{
					// edge case, player specified an alias, then lost permisssions to use alias command
					// just keep alias on file, player may get permissions back
					SimpleAlias.print(player.getName()+" has connected, alias would be "+alias+" but does not have permission to use the /alias command.");
				}
			}
        }
    }

	
	@Override
	public void onPlayerCommandPreprocess(PlayerChatEvent event){
		
		String newCmd = "";
		
		Player player = event.getPlayer();
		
		// split the command
		String[] args = event.getMessage().split(" ");
		
		if(args.length>0 && args[0].startsWith("/") && !args[0].equalsIgnoreCase("/alias")){

			// rebuild the command replacing any aliases with full names

			int i, count=0;
			String newStr, alias, name;

			if(!plugin.names.isEmpty()){

				for(i=0;i<args.length;i++){

					newStr = args[i];

					for (Map.Entry<String, String> pairs : plugin.names.entrySet()) {

						alias = pairs.getValue();
						name = pairs.getKey();

						if(alias.compareToIgnoreCase(newStr)==0){
							// assume first match is the one (aliases can't be re-used)
							newStr = name;
							count++;
							break;
						}
					}

					if(newCmd.length()>0)
						newCmd = newCmd + " ";

					newCmd = newCmd + newStr;

				}

			}

			if(count>0){
				// send newly constructed command instead of original command

				SimpleAlias.print("Translated '"+event.getMessage()+"' into '"+newCmd+"'.");

				// this doesn't actually work like it should
				//event.setMessage(newCmd);
				
				event.setCancelled(true);
				player.chat(newCmd);


			}

		}

	}
	
		
}

