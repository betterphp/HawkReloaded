package uk.co.oliwali.HawkEye.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

/**
 * Contains utilities for manipulating blocks without losing data
 * @author oliverw92
 */
public class BlockUtil {

	/**
	 * Gets the block in 'string form'.
	 * e.g. blockid:datavalue
	 * @param block BlockState of the block you wish to convert
	 * @return string representing the block
	 */
	public static String getBlockString(Block block) {
		return getBlockString(block.getState());
	}
	public static String getBlockString(BlockState block) {
		if (block.getRawData() != 0)
			return block.getTypeId() + ":" + block.getRawData();
		return Integer.toString(block.getTypeId());
	}

	public static String getToolString(Block block) {
		return getToolString(block.getState());
	}
	public static String getToolString(BlockState block) {
		//This is a temp fix for the placement Log face data
		if ((block.getRawData() != 0) && (block.getTypeId() != 17))
			return block.getTypeId() + ":" + block.getRawData();
		return Integer.toString(block.getTypeId());
	}

	/**
	 * Same as getBlockString() except for ItemStack
	 * @param stack ItemStack you wish to convert
	 * @return string representing the item
	 */
	public static String getItemString(ItemStack stack) {
		int data = stack.getData().getData();
		int type = stack.getTypeId();
		if (type == 373) 
			data = stack.getDurability();
		if (stack.getData() != null && data != 0)
			return type + ":" + data;
		return Integer.toString(type);
	}

	/**
	 * Converts an item string into an ItemStack
	 * @param item item string representing the material and data
	 * @param amount
	 * @return an ItemStack
	 */
	public static ItemStack itemStringToStack(String item, Integer amount) {
		String[] itemArr = item.split(":");
		if (itemArr.length > 1)
			return new ItemStack(Integer.parseInt(itemArr[0]), amount, (itemArr[1].length() <= 3 ? Byte.parseByte(itemArr[1]): ((short) Integer.parseInt(itemArr[1]))));
		return new ItemStack(Integer.parseInt(itemArr[0]), amount);
	}

	/**
	 * Returns the name of the block, with its data if applicable
	 * @param blockData
	 * @return
	 */
	public static String getBlockStringName(String blockData) {
		String[] blockArr = blockData.split(":");
		if (!Util.isInteger(blockArr[0])) return blockData;
		if (blockArr.length > 1)
			return Material.getMaterial(Integer.parseInt(blockArr[0])).name() + ":" + blockArr[1];
		else return Material.getMaterial(Integer.parseInt(blockArr[0])).name();
	}

	/**
	 * Sets the block type and data to the inputted block string
	 * @param block Block to be changed
	 * @param blockData string form of a block
	 */
	public static void setBlockString(Block block, String blockData) {
		String[] blockArr = blockData.split(":");
		if (!Util.isInteger(blockArr[0])) return;
		int type = Integer.parseInt(blockArr[0]);
		int data = (blockArr.length > 1) ? Integer.parseInt(blockArr[1]) : 0;
		if (isPlant(type)) {
			Block downrel = block.getRelative(BlockFace.DOWN);
			downrel.setType(Material.SOIL);
			downrel.setData((byte) 1);
		} 

		if (type == 64 || type == 71) {
			block.setTypeId(type);
			block.setData((byte) data);

			Block rel = block.getRelative(BlockFace.UP);
			placeDoorTop(block, rel, data, type);
			return;
		} else if (type == 26) { 
			placeBed(block, type, (byte)data);
		}
		block.setTypeIdAndData(type, (byte) data, true);
	}

	/**
	 * Returns ID section of a block string
	 * @param string
	 * @return int ID
	 */
	public static int getIdFromString(String string) {
		if (!Util.isInteger(string.split(":")[0])) return 0;
		return Integer.parseInt(string.split(":")[0]);
	}

	/**
	 * Returns data section of a block string
	 * @param string
	 * @return int data
	 */
	public static byte getDataFromString(String string) {
		if (string.split(":").length == 1) return 0;
		return (byte)Integer.parseInt(string.split(":")[1]);
	}

	/**
	 * Returns true if the block will be destroyed
	 * prior to baseblock removal
	 * @param int
	 * @return boolean
	 */
	public static boolean itemOnTop(int block) {
		switch(block){
		case 6:
		case 27:
		case 28:
		case 31:
		case 32:
		case 81:
		case 37:
		case 38:
		case 39:
		case 40:
		case 50:
		case 55:
		case 59:
		case 63:
		case 66:
		case 70:
		case 72:
		case 76:
		case 83:
		case 93:
		case 94:
		case 104:
		case 115:
		case 132:
		case 140:
		case 105:
		case 78:
		case 141:
		case 142:
		case 157:
		case 147:
		case 148:
		case 149:
		case 150:
		case 404:
		case 356:
		case 64:
		case 71:
		case 69:
			return true;
		default:
			return false;
		}
	}

	public static void placeBed(Block block, int type, byte data ){
		int beddata = 0;
		Block bed = null;

		if (data == 0) {
			bed = block.getRelative(BlockFace.SOUTH);
			beddata = 8;
		}
		if (data == 1) {
			bed = block.getRelative(BlockFace.WEST);
			beddata = 9;
		}
		if (data == 2) {
			bed = block.getRelative(BlockFace.NORTH);
			beddata = 10;
		}
		if (data == 3) {
			bed = block.getRelative(BlockFace.EAST);
			beddata = 11;
		}
		if (bed != null) {
			bed.setTypeId(type);
			bed.setData((byte)beddata);
		}
	}

	public static BlockFace getBedFace(Block block) {
		int Data = block.getData();
		switch(Data){
		case 8: return BlockFace.NORTH;
		case 9: return BlockFace.EAST;
		case 10: return BlockFace.SOUTH;
		case 11: return BlockFace.WEST;
		}
		return null;
	}

	//Does the block depend on another?
	public static boolean isDepend(int type) {
		if (isItemAttached(type) || itemOnTop(type)) {
			return true;
		}
		return false;
	}

	public static boolean isItemAttached(int block) {
		switch(block){
		case 50:
		case 65:
		case 69:
		case 75:
		case 76:
		case 77:
		case 96:
		case 127:
		case 131:
			return true;
		}
		return false;
	}

	public static boolean isInventoryHolder(int block) {
		switch(block){
		case 54:
		case 146:
		case 61:
		case 62:
		case 23:
			return true;
		}
		return false;
	}

	public static boolean isPlant(int block) {
		switch(block){
		case 59:
		case 105:
		case 104:
		case 141:
		case 142:
		case 131:
			return true;
		}
		return false;
	}

	public static void placeDoorTop(Block b, Block block, int dd, int type) {
		Block side = null;
		Block oside = null;
		if (dd == 0) {
			side = b.getRelative(BlockFace.NORTH);
			oside = b.getRelative(BlockFace.SOUTH);
		} else if (dd == 1) {
			side = b.getRelative(BlockFace.EAST);
			oside = b.getRelative(BlockFace.WEST);
		} else if (dd == 2) {
			side = b.getRelative(BlockFace.SOUTH);
			oside = b.getRelative(BlockFace.NORTH);
		} else {
			side = b.getRelative(BlockFace.WEST);
			oside = b.getRelative(BlockFace.EAST);
		}

		int id = side.getTypeId();
		int oid = oside.getTypeId();
		if (id == 64 || id == 71) {
			block.setTypeIdAndData(type, (byte)9, true);
		} else if (oid == 64 || oid == 71) {
			oside.getRelative(BlockFace.UP).setTypeIdAndData(type, (byte)9, true);
			block.setTypeIdAndData(type, (byte)8, true);
		} else {
			block.setTypeIdAndData(type, (byte)8, true);
		}
	}
}
