/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

/**
 * Stores beekeeping logic for bee housings.
 * Get one with BeeManager.beeRoot.createBeekeepingLogic(IBeeHousing housing)
 * Save and load it to NBT using the INbtWritable methods.
 */
public interface IBeekeepingLogic extends INbtWritable, INbtReadable {
	/**
	 * The default number of ticks per queen work cycle.
	 */
	int DEFAULT_WORK_THROTTLE = 550;

	/* SERVER */

	/**
	 * Checks that the bees can work, setting error conditions on the housing where needed
	 *
	 * @return true if no errors are present and doWork should be called
	 */
	boolean canWork();

	/**
	 * Performs actual work, breeding, production, etc.
	 */
	void doWork();

	/**
	 * Force the logic to refresh any cached values and error states.
	 * Call this when a player opens the gui so that all errors are up to date.
	 */
	void clearCachedValues();

	/**
	 * Changes the number of ticks per work cycle for this beekeeping logic.
	 * The default work throttle is 550 ticks per work cycle, which is 27.5 seconds per work cycle.
	 *
	 * @param workThrottle The number of ticks per work cycle.
	 * @since 2.2.4
	 */
	default void setWorkThrottle(int workThrottle) {
	}

	/* CLIENT */

	/**
	 * Sync to client by using {@link #write(net.minecraft.nbt.CompoundTag)} in your {@link net.minecraft.world.level.block.entity.BlockEntity#getUpdateTag()}
	 */
	void syncToClient();

	void syncToClient(ServerPlayer player);

	/**
	 * Get the progress bar for breeding and production.
	 * To avoid network spam, this is only available server-side,
	 * and must be synced manually to the client when a GUI is open.
	 */
	int getBeeProgressPercent();

	/**
	 * Whether bee fx should be active.
	 * Internally, this is automatically synced to the client.
	 */
	@OnlyIn(Dist.CLIENT)
	boolean canDoBeeFX();

	/**
	 * Display bee fx. Calls IBee.doFX(IEffectData[] storedData, IBeeHousing housing) on the queen.
	 * Internally, the queen is automatically synced to the client for the fx.
	 */
	@OnlyIn(Dist.CLIENT)
	void doBeeFX();

	/**
	 * Used by bee fx to direct bees to nearby flowers.
	 * These positions are synced to the client from the server.
	 */
	List<BlockPos> getFlowerPositions();

	default void readData(FriendlyByteBuf data) {
	}

	default void writeData(FriendlyByteBuf data) {
	}
}
