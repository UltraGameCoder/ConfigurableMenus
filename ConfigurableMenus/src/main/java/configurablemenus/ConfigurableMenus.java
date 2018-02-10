package configurablemenus;

import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigurableMenus extends JavaPlugin {

	private static ConfigurableMenus instance;
	
	@Override
	public void onEnable() {
		instance = this;
		MenuManager.getManager();//Initialize MenuManager.
		getCommand("openmenu").setExecutor(MenuManager.getManager());//Connect Command with MenuManager
		getCommand("menulist").setExecutor(MenuManager.getManager());//Connect Command with MenuManager
		getCommand("refreshmenus").setExecutor(MenuManager.getManager());//Connect Command with MenuManager
	}
	
	public static ConfigurableMenus getInstance() {
		return instance;
	}
}
