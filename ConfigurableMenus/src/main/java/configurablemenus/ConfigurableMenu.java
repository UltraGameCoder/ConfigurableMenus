package configurablemenus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfigurableMenu implements Listener {
	
	private String title;
	private int rows;
	private Inventory inv;
	private List<Integer> notClickable;
	
	public ConfigurableMenu(String title, int rows) {
		this.title = title;
		if (rows <= 0) { rows = 1; }
		if (rows > 6) { rows = 6; }
		this.rows = rows;
		
		notClickable = new ArrayList<Integer>();
		inv = Bukkit.createInventory(null, rows*9, title);
		
		ConfigurableMenus.getInstance().getServer().getPluginManager().registerEvents(this, ConfigurableMenus.getInstance());
	}

	public String getName() {
		return ChatColor.stripColor(title);
	}
	
	public String getTitle() {
		return title;
	}

	public int getRows() {
		return rows;
	}

	public ItemStack getSlot(int index) {
		return inv.getItem(index);
	}

	public void fill(int from, int till, ItemStack item) {
		for (int i = from; i <= till; i++) {
			setSlot(i,item);
		}
	}

	public void setSlot(int index, ItemStack item) {
		inv.setItem(index, item);
		if (notClickable.contains(index)) {
			notClickable.remove(index);
		}
	}
	
	public void fill(int from, int till, ItemStack item, boolean clickable) {
		for (int i = from; i <= till; i++) {
			setSlot(i,item, clickable);
		}
	}

	public void setSlot(int index, ItemStack item, boolean clickable) {
		inv.setItem(index, item);
		if (notClickable.contains(index)) {
			notClickable.remove(index);
		}
		
		if (!clickable) {
			notClickable.add(index);
		}
	}

	public boolean isClickable(int index) {
		return !(notClickable.contains(index));
	}

	public void open(Player player) {
		player.closeInventory();
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null || !e.getClickedInventory().getTitle().equalsIgnoreCase(inv.getTitle()))return;
		ItemStack item = e.getCurrentItem();
		if (item == null || item.getType() == Material.AIR) {
			return;
		}
		int slot = e.getSlot();
		if (notClickable.contains(slot)) {
			e.setCancelled(true);
		}
	}

}
