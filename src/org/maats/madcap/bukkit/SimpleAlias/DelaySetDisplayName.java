package org.maats.madcap.bukkit.SimpleAlias;

import org.bukkit.entity.Player;

public class DelaySetDisplayName implements Runnable
{

	// sets the display name of a player to be equal to their getName
	private Player player = null;
	
	protected DelaySetDisplayName(Player p){
		player = p;
	}
	
	
	public void run()
	{
		if(player.isOnline())
			player.setDisplayName(player.getName());
	}
}
