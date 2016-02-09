package net.thecobix.openwsk.kitsystem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author Postremus / pro_crafting
 *
 */
public interface KitProvider {

	boolean existsKit(String kitName);
	ItemStack[] getItems(String kitName);
	void distribute(String kitName, Player player);
	
}
