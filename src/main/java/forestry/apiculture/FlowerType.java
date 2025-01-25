package forestry.apiculture;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import forestry.api.ForestryTags;
import forestry.api.apiculture.IFlowerType;

public class FlowerType implements IFlowerType {
	private final TagKey<Block> acceptableFlowers;
	private final boolean dominant;
	private final Random rand = new Random();

	public FlowerType(TagKey<Block> acceptableFlowers, boolean dominant) {
		this.acceptableFlowers = acceptableFlowers;
		this.dominant = dominant;
	}

	@Override
	public boolean isAcceptableFlower(Level level, BlockPos pos) {
		// for debugging purposes
		//level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(Blocks.REDSTONE_BLOCK.defaultBlockState()));
		return level.getBlockState(pos).is(this.acceptableFlowers);
	}

	@Override
	public boolean plantRandomFlower(Level level, BlockPos pos, List<BlockState> nearbyFlowers) {
		if (level.hasChunkAt(pos) && level.isEmptyBlock(pos)) {
			loop1:
			for (int i = 0; i < 8; i++) {
				BlockState randState = nearbyFlowers.get(rand.nextInt(nearbyFlowers.size()));
				if (randState.is(ForestryTags.Blocks.PLANTABLE_FLOWERS) && randState.canSurvive(level, pos)) {
					int count = 5;
					for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 1, 4))) {
						if (level.getBlockState(blockpos).is(randState.getBlock())) {
							--count;
							if (count <= 0) {
								continue loop1;
							}
						}
					}
					if (randState.hasProperty(DoublePlantBlock.HALF)) {
						BlockPos topPos = pos.above();

						if (level.isEmptyBlock(topPos)) {
							return level.setBlockAndUpdate(pos, randState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
									&& level.setBlockAndUpdate(topPos, randState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
						}
					} else {
						return level.setBlockAndUpdate(pos, randState);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isDominant() {
		return this.dominant;
	}
}
