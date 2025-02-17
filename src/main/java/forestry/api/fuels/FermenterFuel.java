/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.fuels;

import net.minecraft.world.item.ItemStack;

/**
 * todo data driven
 *
 * @param item            Item that is a valid fuel for the fermenter (i.e. fertilizer).
 * @param fermentPerCycle How much is fermented per work cycle, i.e. how much biomass is produced per cycle.
 * @param burnDuration    Amount of work cycles a single item of this fuel lasts before expiring.
 */
public record FermenterFuel(ItemStack item, int fermentPerCycle, int burnDuration) {
}
