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
package forestry.apiculture.inventory;

import net.minecraft.world.item.ItemStack;

import forestry.api.IForestryApi;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.core.inventory.InventoryAdapterTile;

public class InventorySwarmer extends InventoryAdapterTile<TileAlvearySwarmer> {
	public InventorySwarmer(TileAlvearySwarmer alvearySwarmer) {
		super(alvearySwarmer, 4, "SwarmInv");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack stack) {
		return IForestryApi.INSTANCE.getHiveManager().getSwarmingMaterialChance(stack.getItem()) != 0f;
	}
}
