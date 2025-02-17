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
package forestry.factory.inventory;

import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import forestry.api.fuels.FuelManager;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.utils.RecipeUtils;
import forestry.core.utils.SlotUtil;
import forestry.factory.tiles.TileMoistener;

public class InventoryMoistener extends InventoryAdapterTile<TileMoistener> {
	public static final short SLOT_STASH_1 = 0;
	public static final short SLOT_STASH_COUNT = 6;
	public static final short SLOT_RESERVOIR_1 = 6;
	public static final short SLOT_RESERVOIR_COUNT = 3;
	public static final short SLOT_WORKING = 9;
	public static final short SLOT_PRODUCT = 10;
	public static final short SLOT_RESOURCE = 11;

	public InventoryMoistener(TileMoistener moistener) {
		super(moistener, 12, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack stack) {
		if (slotIndex == SLOT_RESOURCE) {
			return RecipeUtils.getMoistenerRecipe(tile.getLevel().getRecipeManager(), stack) != null;
		}

		if (SlotUtil.isSlotInRange(slotIndex, SLOT_STASH_1, SLOT_STASH_COUNT)) {
			return FuelManager.moistenerResource.containsKey(stack);
		}

		if (slotIndex == SLOT_PRODUCT) {
			Optional<FluidStack> fluidCap = FluidUtil.getFluidContained(stack);
			return fluidCap.map(f -> tile.getTankManager().canFillFluidType(f)).orElse(false);    //TODO very common pattern. Create Helper?
		}

		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slotIndex, ItemStack itemstack, Direction side) {
		if (slotIndex == SLOT_PRODUCT) {
			return true;
		}

		if (SlotUtil.isSlotInRange(slotIndex, SLOT_STASH_1, SLOT_STASH_COUNT + SLOT_RESERVOIR_COUNT)) {
			return !FuelManager.moistenerResource.containsKey(itemstack);
		}

		return false;
	}
}
