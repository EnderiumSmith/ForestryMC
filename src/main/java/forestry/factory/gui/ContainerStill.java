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
package forestry.factory.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotEmptyLiquidContainerIn;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.tiles.TileUtil;
import forestry.factory.features.FactoryMenuTypes;
import forestry.factory.inventory.InventoryStill;
import forestry.factory.tiles.TileStill;

public class ContainerStill extends ContainerLiquidTanks<TileStill> {
	public static ContainerStill fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
		TileStill tile = TileUtil.getTile(inv.player.level(), data.readBlockPos(), TileStill.class);
		return new ContainerStill(windowId, inv, tile);
	}

	public ContainerStill(int windowId, Inventory player, TileStill tile) {
		super(windowId, FactoryMenuTypes.STILL.menuType(), player, tile, 8, 84);

		this.addSlot(new SlotOutput(tile, InventoryStill.SLOT_PRODUCT, 150, 54));
		this.addSlot(new SlotEmptyLiquidContainerIn(tile, InventoryStill.SLOT_RESOURCE, 150, 18));
		this.addSlot(new SlotLiquidIn(tile, InventoryStill.SLOT_CAN, 10, 36));
	}
}
