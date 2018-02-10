package configurablemenus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuManager implements CommandExecutor {
	
	private static MenuManager instance;
	private File menuConfigFile;
	private List<ConfigurableMenu> menus;

	public static MenuManager getManager() {
		if (instance == null) {
			instance = new MenuManager();
		}
		return instance;
	}
	
	private MenuManager() {
		menus = new ArrayList<ConfigurableMenu>();
		
		if (!ConfigurableMenus.getInstance().getDataFolder().exists()) {
			ConfigurableMenus.getInstance().getDataFolder().mkdirs();
		}
		
		menuConfigFile = new File(ConfigurableMenus.getInstance().getDataFolder(), "MenusConfig.yml");
		if (!menuConfigFile.exists()) {
			try {
				menuConfigFile.createNewFile();
				setupConfigDefaults();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		loadMenus();
	}
	
	@SuppressWarnings("deprecation")
	private void setupConfigDefaults() {
		//Create some menu's to save onto the default config
		ConfigurableMenu MenuExample1 = new ConfigurableMenu(ChatColor.translateAlternateColorCodes('&', "&8&lMenuExample&r&61"), 3);
		ItemStack glassBorder = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 1);
		ItemMeta meta = glassBorder.getItemMeta();
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(" ");
		glassBorder.setItemMeta(meta);
		MenuExample1.fill(0,8,glassBorder, false);
		MenuExample1.setSlot(9, glassBorder, false);
		MenuExample1.setSlot(17, glassBorder, false);
		MenuExample1.fill(18,26,glassBorder, false);
		
		MenuExample1.setSlot(17, glassBorder);
		
		menus.add(MenuExample1);
		//Save the menu's to the config
		saveMenus();
		menus = new ArrayList<ConfigurableMenu>();//Clear the menu's
	}

	@SuppressWarnings("deprecation")
	private void saveMenus() {
		FileConfiguration config = getConfig();
		
		
		List<String> menuList = new ArrayList<String>();
		for (ConfigurableMenu menu : menus) {
			String menuName = menu.getName();
			menuList.add(menuName);
		}
		
		config.set("MenusList", menuList);
		
		saveConfig(config);
		
		for (ConfigurableMenu menu : menus) {
			ConfigurationSection menuSection = config.createSection("Menus."+menu.getName());
			menuSection.set("title", menu.getTitle());
			menuSection.set("rows", menu.getRows());
			List<Integer> slotsList = new ArrayList<Integer>();
			for (int i = 0; i < menu.getRows()*9; i++) {
				slotsList.add(i);
			}
			menuSection.set("slots", slotsList);
			for (int index = 0; index < slotsList.size(); index++) {
				ConfigurationSection slotSection = menuSection.createSection("slots."+index);
				ItemStack slotItem = menu.getSlot(index);
				if (slotItem == null)continue;
				ItemMeta slotMeta = slotItem.getItemMeta();
				
				boolean clickable = menu.isClickable(index);
				slotSection.set("Clickable", clickable);
				slotSection.set("Material", slotItem.getType().toString());
				slotSection.set("Amount", slotItem.getAmount());
				slotSection.set("Data", slotItem.getData().getData());
				slotSection.set("Durability", slotItem.getDurability());
				slotSection.set("DisplayName", (slotMeta != null)? slotMeta.getDisplayName() : "NULL");
				
				Map<Enchantment,Integer> slotEnchantments = slotItem.getEnchantments();
				List<String> enchantsList = new ArrayList<String>();
				for (Entry<Enchantment, Integer> entry : slotEnchantments.entrySet()) {
					enchantsList.add(entry.getKey().toString());
				}
				slotSection.set("Enchantments", enchantsList);
				for (Entry<Enchantment, Integer> entry : slotEnchantments.entrySet()) {
					slotSection.set("Enchantments."+entry.getKey().getName(), entry.getValue());
				}
				List<String> flagsList = new ArrayList<String>();
				if (slotMeta != null) {
					Set<ItemFlag> slotFlags = slotMeta.getItemFlags();
					for (ItemFlag flag : slotFlags) {
						flagsList.add(flag.toString());
					}
				}
				slotSection.set("ItemFlags", flagsList);
				
				List<String> loreList = new ArrayList<String>();;
				if (slotMeta != null && slotMeta.hasLore()) {
					loreList = slotMeta.getLore();
				}
				slotSection.set("Lore", loreList);
			}
			saveConfig(config);
		}
		
		saveConfig(config);
	}

	@SuppressWarnings("deprecation")
	private void loadMenus() {
		FileConfiguration config = getConfig();
		
		List<String> menuList = config.getStringList("MenusList");
		
		for (String menu : menuList) {
			ConfigurationSection menuSection = config.getConfigurationSection("Menus."+menu);
			String title = menuSection.getString("title");
			int rows = menuSection.getInt("rows");
			ConfigurableMenu m = new ConfigurableMenu(title, rows);
			for (int index = 0; index < rows * 9; index++) {
				ConfigurationSection slotSection = menuSection.getConfigurationSection("slots."+index);
				
				boolean clickable = slotSection.getBoolean("Clickable");
				Material material = Material.getMaterial(slotSection.getString("Material"));
				if (material == null) continue;
				int amount = slotSection.getInt("Amount");
				byte data = (byte) slotSection.getInt("Data");
				short durability =  (short) slotSection.getInt("Durability");
				
				String displayName = slotSection.getString("DisplayName");
				
				ItemStack slotItem = new ItemStack(material, amount, durability, data);
				ItemMeta meta = slotItem.getItemMeta();
				
				if (meta != null) {
					if (displayName != null && !displayName.isEmpty()) {
						displayName = ChatColor.translateAlternateColorCodes('&', displayName);
					}
					meta.setDisplayName(displayName);
				}
				
				ConfigurationSection enchantsList = slotSection.getConfigurationSection("Enchantments");
				for (String enchant : enchantsList.getKeys(false)) {
					meta.addEnchant(Enchantment.getByName(enchant), slotSection.getInt("Enchantments."+enchant), true);
				}
				
				if (meta != null) {
					List<String> flagsList = slotSection.getStringList("ItemFlags");
					for (String flag : flagsList) {
						meta.addItemFlags(ItemFlag.valueOf(flag));
					}
				}
				
				if (meta != null) {
					List<String> loreList = slotSection.getStringList("Lore");
					meta.setLore(loreList);
				}
				
				if (meta != null) {
					slotItem.setItemMeta(meta);
				}
				
				m.setSlot(index, slotItem, clickable);
			}
			menus.add(m);
		}
	}

	private FileConfiguration getConfig() {
		return YamlConfiguration.loadConfiguration(menuConfigFile);
	}
	
	private void saveConfig(FileConfiguration config) {
		try {
			config.save(menuConfigFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		
		if (label.equalsIgnoreCase("openmenu")) {
			if (args.length <= 0) {
				printHelp(player);
				return false;
			}
			String menuName = args[0];
			
			ConfigurableMenu m = null;
			for (ConfigurableMenu menu : menus) {
				if (menu.getName().equalsIgnoreCase(menuName)) {
					m = menu;
					break;
				}
			}
			
			if (m == null) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cWe can't find a menu named: "+menuName));
				return false;
			}
			m.open(player);
			return true;
		}
		
		if (label.equalsIgnoreCase("menulist")) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Here is a list of all loaded menu's!"));
			for (ConfigurableMenu menu : menus) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7 - "+menu.getName()));
			}
			return true;
		}
		
		if (label.equalsIgnoreCase("refreshmenus")) {
			saveMenus();
			menus = new ArrayList<ConfigurableMenu>();
			loadMenus();
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Refeshed &6"+ menus.size() +"&7 menu's!"));
			return true;
		}
		
		printHelp(player);
		
		return false;
	}

	private void printHelp(Player player) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/openmenu <Menu> | Opens the specified Menu!"));
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/menulist | Shows all the loaded Menu's!"));
	}

}
