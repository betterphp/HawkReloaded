package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

/**
 * Represents a container transaction as created in {@MonitorInventoryListener}
 * @author oliverw92
 */
public class ContainerEntry extends DataEntry {

	public ContainerEntry() { }

	public ContainerEntry(Player player, Location location, String diff) {
		data = diff;
		setInfo(player, DataType.CONTAINER_TRANSACTION, location);
	}
	public ContainerEntry(String player, Location location, String diff) {
		data = diff;
		setInfo(player, DataType.CONTAINER_TRANSACTION, location);
	}

	@Override
	public String getStringData() {
		if (data.contains("&")) data = InventoryUtil.updateInv(data); //For OLD entries
		return InventoryUtil.dataToString(data);
	}

	@Override
	public boolean rollback(Block block) {
		BlockState blockState = block.getState();
		if (!(blockState instanceof InventoryHolder)) return false;
		Inventory inv = ((InventoryHolder) blockState).getInventory();
		if (data.contains("&")) data = InventoryUtil.updateInv(data); //For OLD entries
		for (String s : data.split("@")) {
			if (s.startsWith("+")) {
				inv.removeItem(InventoryUtil.uncompressItem(s));
			} else if (s.startsWith("-")) {
				inv.addItem(InventoryUtil.uncompressItem(s));
			} else return false;
		}
		return true;
	}

	@Override
	public boolean rebuild(Block block) {
		BlockState blockState = block.getState();
		if (!(blockState instanceof InventoryHolder)) return false;
		Inventory inv = ((InventoryHolder) blockState).getInventory();
		if (data.contains("&")) data = InventoryUtil.updateInv(data); //For OLD entries
		for (String s : data.split("@")) {
			if (s.startsWith("+")) {
				inv.addItem(InventoryUtil.uncompressItem(s));
			} else if (s.startsWith("-")) {
				inv.removeItem(InventoryUtil.uncompressItem(s));
			} else return false;
		}
		return true;
	}
}
