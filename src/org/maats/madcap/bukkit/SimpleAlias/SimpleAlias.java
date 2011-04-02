package org.maats.madcap.bukkit.SimpleAlias;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;


/**
 * SimpleAlias for Bukkit
 *
 * @author madcap
 */
public class SimpleAlias extends JavaPlugin {
	private final SimpleAliasPlayerListener playerListener = new SimpleAliasPlayerListener(this);
	//private final SimpleAliasBlockListener blockListener = new SimpleAliasBlockListener(this);
	
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

	protected File dataDir = null;
	protected final String ALIASES_FILE = "aliases.txt";
	protected List<String> bannedAliases = null;
	protected File playerDir = null;
	protected Properties props = null;
	protected final String CONFIG_FILE = "alias_config.yml";
	protected Configuration config = null;
	
	// store a mapping of names->aliases
	protected HashMap<String, String> names = null;
	
	// permissions plugin use
	protected static PermissionHandler permissions = null;
	protected PermissionsListener permListener = new PermissionsListener(this);
	
	protected static void print(String s){
		System.out.println("SimpleAlias: "+s);
	}
	

	public void onEnable() {
		
		// set up a command executor for the /alias command
		getCommand("alias").setExecutor(new AliasCommand(this));
		
		// get server properties so we can get the players dir
		props = new Properties();
		File propFile = new File("server.properties");
		if(propFile.exists()){
			try{
				this.props.load(new FileInputStream(propFile));
				playerDir = new File(props.getProperty("level-name")+File.separator+"players");
				//print("players dir found: "+playerDir.getAbsolutePath());
			}
			catch(Exception e){
				print("server.properties file was not found!");
			}
		}
		
		// register events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, permListener, Priority.Monitor, this);
		
		this.dataDir = this.getDataFolder();
		
		// check for this plugin's data directory
		if(!dataDir.exists())
		{
			print("Plugin data folder is missing, creating "+dataDir.getAbsolutePath());
			dataDir.mkdir();
		}
		
		// check for yml file
		File configFile = new File(dataDir.getAbsolutePath() + File.separator + CONFIG_FILE);
		if(!configFile.exists()){
			print("Configuration file "+CONFIG_FILE+" is missing!");
			config = null;
			bannedAliases = null;
			
			// TODO make the config file
		}
		else{
			// load config yml file
			config = new Configuration(configFile);
			config.load();
			bannedAliases = config.getStringList("banned-aliases", null);
			//print("Configuration file "+YML_FILE+" loaded successfully.");
			
			// if the list in the config file doesn't exist, banned Aliases is actually an empty list, set to null instead
			if(bannedAliases.isEmpty())
				bannedAliases = null;
			
			// backwards compatible for spelling error in earlier version
			if(bannedAliases == null){
				bannedAliases = config.getStringList("banned-aliaes", null);
				if(bannedAliases.isEmpty())
					bannedAliases = null;
			}
		}

		// set up a new place to hold aliases
		names = new HashMap<String, String>();
		
		// check for aliases file
		File aliasFile = new File(dataDir.getAbsolutePath() + File.separator + ALIASES_FILE);
		if(!aliasFile.exists())
		{
			try
			{
				aliasFile.createNewFile();
				print("Alias file is missing, creating "+aliasFile.getAbsolutePath());
			}
			catch ( IOException ex )
			{
				print("Alias file is missing, failed to create new one.");
			}
		}

		// load aliases from aliases file
		if(loadAliases())
			print("Alias file loaded.");
		else
			print("Alias file could not be loaded.");

		
		// it's possible this plugin was disabled, 
		// players joined that should have gotten aliases, 
		// then the plugin was re-enabled
		// these player need to have their aliases updated
		// TODO implement this
		
		
		// check for permissions plugin:
		// http://forums.bukkit.org/threads/5974
		// it could already be enabled
		Plugin permPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		if (permPlugin != null && permPlugin.isEnabled()) {
			SimpleAlias.permissions = (PermissionHandler) ((Permissions) permPlugin).getHandler();
			print("Permissions plugin is enabled, using permission node SimpleAlias.* for access to /alias.");
        }
		
		// print startup message
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled." );
		
	}
	
	public void onDisable() {
		
		// make sure we don't attempt to use permissions any more
		permissions = null;
		
		// save aliases to aliases file
		if(saveAliases())
			print("Aliases written to file.");
		else
			print("Aliases could not be writtren to file.");

		names = null;
		bannedAliases = null;
		playerDir = null;
		dataDir = null;
		props = null;
		config = null;
		
		print("Plugin shutting down.");
	}

	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}

	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}

	protected boolean loadAliases(){
		// load aliases from file
		try
		{	
			FileReader fr = new FileReader((dataDir.getAbsolutePath() + File.separator + ALIASES_FILE));
			BufferedReader reader = new BufferedReader(fr);
			String line = reader.readLine();
			while ( line != null )
			{
				String[] values = line.split("=");
				if ( values.length == 2 )
				{
					names.put(values[0], values[1]);
				}
				line = reader.readLine();
			}
			reader.close();
			fr.close();
			return true;
		}
		catch (Exception ex)
		{
			names = null;
			return false;
		}
	}

	protected boolean saveAliases()
	{
		// write aliases to file
		try
		{
			FileWriter fw = new FileWriter(dataDir.getAbsolutePath() + File.separator + ALIASES_FILE);
			BufferedWriter writer = new BufferedWriter(fw);
			for ( Entry<String, String> entry : names.entrySet() )
			{
				writer.write(entry.getKey() + "=" + entry.getValue());
				writer.newLine();
			}
			writer.close();
			fw.close();
			return true;
		}
		catch (Exception ex)
		{
			//ex.printStackTrace();
			return false;
		}
	}	

	
	// check alias against all names in the world/players directory
	protected boolean isPlayerName(String alias){

		if(playerDir != null && playerDir.exists()){
			String loginName;
			String[] playerArray = this.playerDir.list();
			for (int i=0; i<playerArray.length; i++){
				//print(playerArray[i]);

				loginName = playerArray[i];

				// trim off .dat
				loginName = loginName.replaceAll("\\.dat", "");

				if(alias.compareToIgnoreCase(loginName)==0){
					return true;
				}
			}
		}
		return false;
	}
	
	
	// check alias against list of banned aliases in config file
	protected boolean isBanned(String alias){
		Iterator<String> it;
		String notAllowed;
		if(this.bannedAliases!=null){
			it = this.bannedAliases.iterator();
			while (it.hasNext()){
				notAllowed=(String) it.next();
				if(alias.compareToIgnoreCase(notAllowed)==0){
					return true;
				}
			}
		}
		return false;
	}
			

}

