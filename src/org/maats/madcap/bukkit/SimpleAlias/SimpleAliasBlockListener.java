package org.maats.madcap.bukkit.SimpleAlias;

import org.bukkit.block.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;

/**
 * SimpleAlias block listener
 * @author madcap
 */
public class SimpleAliasBlockListener extends BlockListener {
    private final SimpleAlias plugin;

    public SimpleAliasBlockListener(final SimpleAlias plugin) {
        this.plugin = plugin;
    }
    
   
    
}
