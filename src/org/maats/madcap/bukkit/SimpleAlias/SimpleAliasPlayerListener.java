package org.maats.madcap.bukkit.SimpleAlias;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.*;
import org.bukkit.material.MaterialData;
import org.bukkit.*;





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
        	player.setDisplayName(alias);
        	SimpleAlias.print(player.getName()+" has connected, aliased as "+alias);
        }
    }
	
	
	// This is a work in progress
	
//	@Override
//	public void onPlayerCommandPreprocess(PlayerChatEvent event){
//		
//		String newCmd = "";
//		
//		// split the command
//		String[] args = event.getMessage().split(" ");
//		
//		if(args.length>0 && args[0].startsWith("/")){
//
//			// rebuild the command replacing any aliases with full names
//
//			int i, count=0;
//			String newStr, alias, name;
//
//			if(!plugin.names.isEmpty()){
//
//				for(i=0;i<args.length;i++){
//
//					newStr = args[i];
//
//					for (Map.Entry<String, String> pairs : plugin.names.entrySet()) {
//
//						alias = pairs.getValue();
//						name = pairs.getKey();
//
//						if(alias.compareToIgnoreCase(newStr)==0){
//							// assume first match is the one (aliases can't be re-used)
//							newStr = name;
//							count++;
//							break;
//						}
//					}
//
//					if(newCmd.length()>0)
//						newCmd = newCmd + " ";
//
//					newCmd = newCmd + newStr;
//
//				}
//
//			}
//
//			if(count>0){
//				// send newly construct command instead of original command
//				SimpleAlias.print("Translated '"+event.getMessage()+"' into '"+newCmd+"'.");
//				event.setMessage(newCmd);
//
//			}
//
//		}
//
//	}
	
		
}

