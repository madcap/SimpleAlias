Simple Alias

SimpleAlias will allow players to give themselves an alias (nickname) that displays when they use chat.

For Example, my login name is madcap_magician. By using the command `/alias Madcap` now whenever I use chat I will appear as Madcap.

You can clear your alias by issuing the `/alias` command with no arguments.

Aliases are currently limited to 12 characters, must be only 1 word and must be alphanumeric. Aliases are stored locally in a flat file. Aliases in player submitted commands are automatically replaced with the full player name. All alias activity is logged in case of miss-use.

The plugin now uses alias_config.yml in the plugin's data folder. It will work without this file but some features won't exist. In the YML file you can specify a list of names which are not allowed for aliases. I've provided a few already.

Sample config file:

## list any names here that you do not want your players using (not case sensitive)
banned-aliases:
  - "notch"
  - "admin"
  - "administrator"
  - "server"
  
  
This plugin now supports the Permissions plugin (http://forums.bukkit.org/threads/5974). If the permissions plugin exists and is enabled then the node 'SimpleAlias.*' is required for access to the /alias command, otherwise all players will have access to the /alias command.
Here is an example granting permission to use /alias to the 'Players' group:

    Players:
        default: false
        info:
            prefix: ''
            suffix: ''
            build: true
        inheritance:
        permissions:
            - 'SimpleAlias.*'


  
Changelog:
Version 1.0.0
	Added support for permissions
	Player issued commands containing aliases are automatically translated into full login names
Version 0.2.1
	Changed yml file to no longer require a world directory (now it comes from server.properties).
Version 0.2.0
	Added checking alias name against player login names.
	Added list of aliases players aren't allowed to use (such as Notch, Admin etc).
	Added .yml file for plugin configuration.
	Fixed stupidly long constructor warning.
Version 0.1.0
	Release
