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
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.IContainerCrafting;
import forestry.core.gui.slots.SlotCraftMatrix;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotLocked;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.core.tiles.TileUtil;
import forestry.factory.features.FactoryMenuTypes;
import forestry.factory.inventory.InventoryCarpenter;
import forestry.factory.tiles.TileCarpenter;

public class ContainerCarpenter extends ContainerLiquidTanks<TileCarpenter> implements IContainerCrafting {
	private ItemStack oldCraftPreview = ItemStack.EMPTY;

	public static ContainerCarpenter fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
		TileCarpenter tile = TileUtil.getTile(inv.player.level(), data.readBlockPos(), TileCarpenter.class);
		return new ContainerCarpenter(windowId, inv, tile);
	}

	public ContainerCarpenter(int windowId, Inventory inventoryplayer, TileCarpenter tile) {
		super(windowId, FactoryMenuTypes.CARPENTER.menuType(), inventoryplayer, tile, 8, 136);

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				addSlot(new Slot(tile, InventoryCarpenter.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 90 + i * 18));
			}
		}

		// Liquid Input
		addSlot(new SlotLiquidIn(tile, InventoryCarpenter.SLOT_CAN_INPUT, 120, 20));
		// Boxes
		addSlot(new SlotFiltered(tile, InventoryCarpenter.SLOT_BOX, 83, 20));
		// Product
		addSlot(new SlotOutput(tile, InventoryCarpenter.SLOT_PRODUCT, 120, 56));

		// Craft Preview display
		addSlot(new SlotLocked(tile.getCraftPreviewInventory(), 0, 80, 51));

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlot(new SlotCraftMatrix(this, tile.getCraftingInventory(), k1 + l * 3, 10 + k1 * 18, 20 + l * 18));
			}
		}
	}

	@Override
	public void onCraftMatrixChanged(Container iinventory, int slot) {
		this.tile.checkRecipe(this.tile.getLevel().registryAccess());
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		Container craftPreviewInventory = tile.getCraftPreviewInventory();

		ItemStack newCraftPreview = craftPreviewInventory.getItem(0);
		if (!ItemStack.matches(oldCraftPreview, newCraftPreview)) {
			oldCraftPreview = newCraftPreview;

			PacketItemStackDisplay packet = new PacketItemStackDisplay(tile, newCraftPreview);
			sendPacketToListeners(packet);
		}
	}

	public TileCarpenter getCarpenter() {
		return tile;
	}
}
