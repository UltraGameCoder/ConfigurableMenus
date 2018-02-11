package configurablemenus;

import org.bukkit.plugin.java.JavaPlugin;

import API.MenuManager;

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
	
	@Override
	public void onDisable() {
		MenuManager.getManager().saveMenus();
	}
	
	public static ConfigurableMenus getInstance() {
		return instance;
	}
}
