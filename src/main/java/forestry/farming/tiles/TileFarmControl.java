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
package forestry.farming.tiles;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.api.multiblock.IFarmComponent;
import forestry.farming.blocks.FarmBlock;
import forestry.farming.features.FarmingTiles;

public class TileFarmControl extends TileFarm implements IFarmComponent.Listener {
	private final IFarmListener farmListener;

	public TileFarmControl(BlockPos pos, BlockState state) {
		super(FarmingTiles.CONTROL.tileType(), pos, state);
		this.farmListener = new ControlFarmListener(this);
	}

	@Override
	public IFarmListener getFarmListener() {
		return farmListener;
	}

	private static class ControlFarmListener implements IFarmListener {
		private final TileFarmControl tile;

		public ControlFarmListener(TileFarmControl tile) {
			this.tile = tile;
		}

		@Override
		public boolean cancelTask(IFarmLogic logic, Direction direction) {
			for (Direction facing : new Direction[]{Direction.UP, Direction.DOWN, direction}) {
				BlockPos pos = tile.getBlockPos();
				Level world = tile.getWorldObj();
				BlockState blockState = world.getBlockState(pos.relative(facing));
				if (!(blockState.getBlock() instanceof FarmBlock) && world.getSignal(pos, facing) > 0) {
					return true;
				}
			}
			return false;
		}
	}

}
