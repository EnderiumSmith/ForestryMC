/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.logic.crops;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.core.utils.BlockUtil;

public class CropBasicAgriCraft extends Crop {
	private final BlockState blockState;

	public CropBasicAgriCraft(Level world, BlockState blockState, BlockPos position) {
		super(world, position);
		this.blockState = blockState;
	}

	@Override
	protected boolean isCrop(Level world, BlockPos pos) {
		return world.getBlockState(pos) == blockState;
	}

	@Override
	protected List<ItemStack> harvestBlock(Level world, BlockPos pos) {
		Block block = blockState.getBlock();
		NonNullList<ItemStack> harvest = NonNullList.create();
		//		block.getDrops(harvest, world, pos, blockState, 0);
		//TODO getDrops
		if (harvest.size() > 1) {
			harvest.remove(1); //AgriCraft returns cropsticks in 0, seeds in 1 in getDrops, removing since harvesting doesn't return them.
		}
		harvest.remove(0);

		BlockState oldState = world.getBlockState(pos);
		BlockUtil.setBlockWithBreakSound(world, pos, block.defaultBlockState(), oldState);
		return harvest;
	}

	@Override
	public String toString() {
		return String.format("CropBasicAgriCraft [ position: [ %s ]; block: %s ]", position.toString(), blockState);
	}
}
