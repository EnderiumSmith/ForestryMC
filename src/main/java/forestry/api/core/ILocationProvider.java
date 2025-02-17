/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Interface for things, that have a location.
 * Must not be named "getWorld" and "getPos" to avoid
 * SpecialSource issue https://github.com/md-5/SpecialSource/issues/12
 * TODO rename to getBlockPos, getLevel in 1.21
 */
public interface ILocationProvider {
	BlockPos getCoordinates();

	@Nullable
	Level getWorldObj();
}
