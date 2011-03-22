package org.maats.madcap.bukkit.SimpleAlias;

import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionsListener extends ServerListener {

	// listen for changes to the Permissions plugin
	
    @SuppressWarnings("unused")
	private SimpleAlias mainPlugin;

    public PermissionsListener(SimpleAlias thisPlugin) {
        mainPlugin = thisPlugin;
    }

    @Override
    public void onPluginEnabled(PluginEvent event) {
    	// if permissions gets enabled, set it for usage in the main plugin
    	String pluginName = event.getPlugin().getDescription().getName();
    	if(pluginName.equals("Permissions")){
    		Plugin permPlugin = event.getPlugin();
    		if (permPlugin != null && permPlugin.isEnabled()) {
    			SimpleAlias.permissions = (PermissionHandler) ((Permissions) permPlugin).getHandler();
    			SimpleAlias.print("Permissions plugin is enabled, using permission node SimpleAlias.* for /alias.");
            }
        }
    }

    public void onPluginDisabled(PluginEvent event) {
            String pluginName = event.getPlugin().getDescription().getName();
        	if(pluginName.equals("Permissions")){
        		Plugin permPlugin = event.getPlugin();
        		// not sure on all possible states of a plugin, can getPlugin return null?
        		if ((permPlugin != null && !permPlugin.isEnabled()) || permPlugin == null) {
        			if(SimpleAlias.permissions!=null){
        				// permissions was enabled but has become disabled.
        				SimpleAlias.print("Permissions plugin has been disabled, no longer requiring any permissions for /alias.");       			
        			}
        			SimpleAlias.permissions = null;
                }
            }
        }
	
}
