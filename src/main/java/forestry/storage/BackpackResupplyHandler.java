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
package forestry.storage;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import forestry.api.storage.BackpackResupplyEvent;
import forestry.core.inventory.ItemInventory;
import forestry.storage.inventory.ItemInventoryBackpack;
import forestry.storage.items.ItemBackpack;

public class BackpackResupplyHandler {
	private static NonNullList<ItemStack> getBackpacks(Inventory playerInventory) {
		NonNullList<ItemStack> backpacks = NonNullList.create();
		for (ItemStack itemStack : playerInventory.items) {
			if (itemStack.getItem() instanceof ItemBackpack) {
				backpacks.add(itemStack);
			}
		}
		return backpacks;
	}

	public static void resupply(Player player) {
		// Do not attempt resupplying if this backpack is already opened.
		if (player.containerMenu instanceof InventoryMenu) {
			for (ItemStack backpack : getBackpacks(player.getInventory())) {
				if (ItemBackpack.getMode(backpack) == BackpackMode.RESUPPLY) {
					// Load their inventory
					ItemBackpack backpackItem = (ItemBackpack) backpack.getItem();
					ItemInventory backpackInventory = new ItemInventoryBackpack(player, backpackItem.getBackpackSize(), backpack);

					Event event = new BackpackResupplyEvent(player, backpackItem.getDefinition(), backpackInventory);
					if (!MinecraftForge.EVENT_BUS.post(event)) {
						for (int i = 0; i < backpackInventory.getContainerSize(); i++) {
							ItemStack itemStack = backpackInventory.getItem(i);
							if (topOffPlayerInventory(player, itemStack)) {
								backpackInventory.setItem(i, itemStack);
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This tops off existing stacks in the player's inventory.
	 * Adds to player inventory if there is an incomplete stack in there.
	 */
	private static boolean topOffPlayerInventory(Player player, ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}
		Inventory playerInventory = player.getInventory();
		List<ItemStack> inventory = new LinkedList<>();
		inventory.addAll(playerInventory.items);
		inventory.addAll(playerInventory.offhand);

		for (ItemStack inventoryStack : inventory) {
			if (playerInventory.hasRemainingSpaceForItem(inventoryStack, itemstack)) {
				inventoryStack.grow(1);
				inventoryStack.setPopTime(5);
				itemstack.shrink(1);
				return true;
			}
		}

		return false;
	}

}
