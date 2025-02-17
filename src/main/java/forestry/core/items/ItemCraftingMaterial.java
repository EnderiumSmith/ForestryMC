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
package forestry.core.items;

import net.minecraft.world.item.Item;

import forestry.core.items.definitions.EnumCraftingMaterial;

public class ItemCraftingMaterial extends ItemForestry {
	private final EnumCraftingMaterial type;

	public ItemCraftingMaterial(EnumCraftingMaterial type) {
		super(new Item.Properties());
		this.type = type;
	}

	public EnumCraftingMaterial getType() {
		return this.type;
	}
}
