package me.dags.converter.extent.volume.latest;

import me.dags.converter.block.BlockState;
import me.dags.converter.extent.volume.Volume;
import me.dags.converter.registry.Registry;
import me.dags.converter.util.storage.BitArray;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;
import org.jnbt.TagType;

import java.text.ParseException;
import java.util.List;

public abstract class AbstractVolumeReader implements Volume.Reader {

    private final CompoundTag root;
    private final BitArray blocks;
    private final Registry.Reader<BlockState> palette;

    public AbstractVolumeReader(Registry<BlockState> registry, CompoundTag root) throws ParseException {
        this.root = root;
        ListTag<CompoundTag> pal = root.getListTag("Palette", TagType.COMPOUND);
        long[] states = root.getLongs("BlockStates");
        if ((pal == null) || (states == null)) {
        	this.blocks = null;
        	this.palette = null;
        }
        else {
        	List<Tag<CompoundTag>> palette = pal.getBacking();
        	int bits = BitArray.minBits(palette.size() - 1);
        	this.palette = registry.getParser().parsePalette(palette);
        	this.blocks = new BitArray(bits, states);
        }
    }

    @Override
    public int size() {
    	if (blocks == null) return 0;
        return blocks.size();
    }

    @Override
    public BlockState getState(int x, int y, int z) {
    	if (blocks == null) return BlockState.AIR;
        int index = indexOf(x, y, z);
        int stateId = blocks.getBits(index);
        return palette.getValue(stateId);
    }

    @Override
    public Tag<?> getData(String key) {
        return root.get(key);
    }
}
