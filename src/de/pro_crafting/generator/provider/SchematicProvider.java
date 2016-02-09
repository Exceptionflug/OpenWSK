package de.pro_crafting.generator.provider;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.bukkit.Material;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import de.pro_crafting.common.Point;
import de.pro_crafting.common.Size;
import de.pro_crafting.generator.BlockData;
import de.pro_crafting.generator.criteria.Criteria;

public class SchematicProvider implements SizeProvider{
	private Criteria criteria;
	private Schematic schem;
	
	public SchematicProvider(Criteria criteria, File file) {
		this.criteria = criteria;
		
		try {
			this.schem = load(file);
		} catch (Exception e) {	
			e.printStackTrace();
		}
	}
	
	private Schematic load(File file) throws Exception {
		NBTInputStream nbtStream = new NBTInputStream(new FileInputStream(file));

	    Point origin = new Point(0, 0, 0);
	    Point offset = new Point(0, 0, 0);
	
	    // Schematic tag
	    CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
	    nbtStream.close();
	    if (!schematicTag.getName().equals("Schematic")) {
	        throw new Exception("Tag \"Schematic\" does not exist or is not first");
	    }
	
	    // Check
	    Map<String, Tag> schematic = schematicTag.getValue();
	    if (!schematic.containsKey("Blocks")) {
	        throw new Exception("Schematic file is missing a \"Blocks\" tag");
	    }
	
	    // Get information
	    short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
	    short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
	    short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
	
	    try {
	        int originX = getChildTag(schematic, "WEOriginX", IntTag.class).getValue();
	        int originY = getChildTag(schematic, "WEOriginY", IntTag.class).getValue();
	        int originZ = getChildTag(schematic, "WEOriginZ", IntTag.class).getValue();
	        origin = new Point(originX, originY, originZ);
	    } catch (Exception e) {
	        // No origin data
	    }
	
	    try {
	        int offsetX = getChildTag(schematic, "WEOffsetX", IntTag.class).getValue();
	        int offsetY = getChildTag(schematic, "WEOffsetY", IntTag.class).getValue();
	        int offsetZ = getChildTag(schematic, "WEOffsetZ", IntTag.class).getValue();
	        offset = new Point(offsetX, offsetY, offsetZ);
	    } catch (Exception e) {
	        // No offset data
	    }
	
	    // Check type of Schematic
	    String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
	    if (!materials.equals("Alpha")) {
	        throw new Exception("Schematic file is not an Alpha schematic");
	    }
	
	    // Get blocks
	    byte[] blockId = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
	    byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
	    byte[] addId = new byte[0];
	    short[] blocks = new short[blockId.length]; // Have to later combine IDs
	
	    // We support 4096 block IDs using the same method as vanilla Minecraft, where
	    // the highest 4 bits are stored in a separate byte array.
	    if (schematic.containsKey("AddBlocks")) {
	        addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
	    }
	
	    // Combine the AddBlocks data with the first 8-bit block ID
	    for (int index = 0; index < blockId.length; index++) {
	        if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
	            blocks[index] = (short) (blockId[index] & 0xFF);
	        } else {
	            if ((index & 1) == 0) {
	                blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
	            } else {
	                blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
	            }
	        }
	    }
	
	    /*// Need to pull out tile entities
	    List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class)
	            .getValue();
	    Map<BlockVector, Map<String, Tag>> tileEntitiesMap =
	            new HashMap<BlockVector, Map<String, Tag>>();
	
	    for (Tag tag : tileEntities) {
	        if (!(tag instanceof CompoundTag)) continue;
	        CompoundTag t = (CompoundTag) tag;
	
	        int x = 0;
	        int y = 0;
	        int z = 0;
	
	        Map<String, Tag> values = new HashMap<String, Tag>();
	
	        for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
	            if (entry.getKey().equals("x")) {
	                if (entry.getValue() instanceof IntTag) {
	                    x = ((IntTag) entry.getValue()).getValue();
	                }
	            } else if (entry.getKey().equals("y")) {
	                if (entry.getValue() instanceof IntTag) {
	                    y = ((IntTag) entry.getValue()).getValue();
	                }
	            } else if (entry.getKey().equals("z")) {
	                if (entry.getValue() instanceof IntTag) {
	                    z = ((IntTag) entry.getValue()).getValue();
	                }
	            }
	
	            values.put(entry.getKey(), entry.getValue());
	        }
	
	        BlockVector vec = new BlockVector(x, y, z);
	        tileEntitiesMap.put(vec, values);
	    }*/
	
	    Size size = new Size(width, height, length);
	    schem = new Schematic(offset, origin, size);
	
	    for (int x = 0; x < width; ++x) {
	        for (int y = 0; y < height; ++y) {
	            for (int z = 0; z < length; ++z) {
	                int index = y * width * length + z * width + x;
	                Point pt = new Point(x, y, z);
	                //BlockData block = //getBlockForId(blocks[index], blockData[index]);
	                BlockData block = new BlockData(Material.getMaterial(blocks[index]), blockData[index]);
	                /*if (tileEntitiesMap.containsKey(pt)) {
	                    block.setNbtData(new CompoundTag("", tileEntitiesMap.get(pt)));
	                }*/
	                schem.set(pt, block);
	            }
	        }
	    }
	    
	    return schem;
	}
	
	public BlockData getBlockData(Point point, BlockData block) {
		if (!criteria.matches(point, block)) {
			return block;
		}
		return this.schem.get(point);
	}

	public Criteria getCriteria() {
		return this.criteria;
	}
	

	public Size getSize() {
		return this.schem.getSize();
	}
	
	public Point getOrigin() {
		return this.schem.getOrigin();
	}
	
	/**
     * Get child tag of a NBT structure.
     *
     * @param items The parent tag map
     * @param key The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws DataException if the tag does not exist or the tag is not of the expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key,
                                                 Class<T> expected) throws Exception {

        if (!items.containsKey(key)) {
            throw new Exception("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new Exception(
                    key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}
