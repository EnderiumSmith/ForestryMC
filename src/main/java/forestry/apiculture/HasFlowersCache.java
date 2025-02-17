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
package forestry.apiculture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IFlowerType;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.alleles.BeeChromosomes;
import forestry.api.util.TickHelper;

// Cache used to determine if a beehive has a suitable flower nearby.
// This passively checks one block a tick in a spiraling pattern centered on the hive,
// but the entire area is checked at once when a player opens the hive GUI.
public class HasFlowersCache implements INbtWritable, INbtReadable {
	private static final String NBT_KEY = "hasFlowerCache";
	private static final String NBT_KEY_FLOWERS = "flowers";
	private final int flowerCheckInterval;

	private final TickHelper tickHelper = new TickHelper(0);

	public HasFlowersCache() {
		this.flowerCheckInterval = 200;
	}

	public HasFlowersCache(int checkInterval) {
		this.flowerCheckInterval = checkInterval;
	}

	@Nullable
	private FlowerData flowerData;
	private final ArrayList<BlockPos> flowerCoords = new ArrayList<>();
	private final List<BlockState> flowers = new ArrayList<>();

	private boolean needsSync = false;

	private static class FlowerData {
		public final IFlowerType flowerType;
		public final Vec3i territory;
		public Iterator<BlockPos.MutableBlockPos> areaIterator;

		public FlowerData(IBee queen, IBeeHousing housing) {
			this.flowerType = queen.getGenome().getActiveValue(BeeChromosomes.FLOWER_TYPE);
			this.territory = queen.getGenome().getActiveValue(BeeChromosomes.TERRITORY);
			this.areaIterator = queen.getAreaIterator(housing);
		}

		public void resetIterator(IBee queen, IBeeHousing beeHousing) {
			this.areaIterator = queen.getAreaIterator(beeHousing);
		}
	}

	public void update(IBee queen, IBeeHousing beeHousing) {
		if (flowerData == null) {
			this.flowerData = new FlowerData(queen, beeHousing);
			this.flowerCoords.clear();
			this.flowers.clear();
		}
		Level level = beeHousing.getWorldObj();
		tickHelper.onTick();

		if (!flowerCoords.isEmpty() && tickHelper.updateOnInterval(flowerCheckInterval)) {
			Iterator<BlockPos> iterator = flowerCoords.iterator();
			while (iterator.hasNext()) {
				BlockPos flowerPos = iterator.next();
				if (level.hasChunkAt(flowerPos) && !flowerData.flowerType.isAcceptableFlower(level, flowerPos)) {
					iterator.remove();
					flowers.clear();
					needsSync = true;
				}
			}
		}

		final int flowerCount = flowerCoords.size();
		final int ticksPerCheck = 1 + (flowerCount * flowerCount);

		if (tickHelper.updateOnInterval(ticksPerCheck)) {
			if (flowerData.areaIterator.hasNext()) {
				BlockPos.MutableBlockPos blockPos = flowerData.areaIterator.next();
				if (flowerData.flowerType.isAcceptableFlower(level, blockPos)) {
					addFlowerPos(blockPos.immutable());
				}
			} else {
				flowerData.resetIterator(queen, beeHousing);
			}
		}
	}

	public boolean hasFlowers() {
		return !flowerCoords.isEmpty();
	}

	public boolean needsSync() {
		boolean returnVal = needsSync;
		needsSync = false;
		return returnVal;
	}

	public void onNewQueen(IBee queen, IBeeHousing housing) {
		if (this.flowerData != null) {
			IGenome genome = queen.getGenome();
			IFlowerType flowerType = genome.getActiveValue(BeeChromosomes.FLOWER_TYPE);
			if (this.flowerData.flowerType != flowerType || !this.flowerData.territory.equals(genome.getActiveValue(BeeChromosomes.TERRITORY))) {
				flowerData = new FlowerData(queen, housing);
				flowerCoords.clear();
				flowers.clear();
			}
		}
	}

	public List<BlockPos> getFlowerCoords() {
		return Collections.unmodifiableList(flowerCoords);
	}

	public List<BlockState> getFlowers(Level level) {
		if (flowers.isEmpty() && !flowerCoords.isEmpty()) {
			for (BlockPos flowerCoord : flowerCoords) {
				BlockState blockState = level.getBlockState(flowerCoord);
				flowers.add(blockState);
			}
		}
		return Collections.unmodifiableList(flowers);
	}

	public void addFlowerPos(BlockPos blockPos) {
		flowerCoords.add(blockPos);
		flowers.clear();
		needsSync = true;
	}

	public void forceLookForFlowers(IBee queen, IBeeHousing housing) {
		if (flowerData != null) {
			flowerCoords.clear();
			flowers.clear();
			flowerData.resetIterator(queen, housing);
			Level level = housing.getWorldObj();
			while (flowerData.areaIterator.hasNext()) {
				BlockPos.MutableBlockPos blockPos = flowerData.areaIterator.next();
				if (flowerData.flowerType.isAcceptableFlower(level, blockPos)) {
					addFlowerPos(blockPos.immutable());
				}
			}
		}
	}

	@Override
	public void read(CompoundTag compoundNBT) {
		if (!compoundNBT.contains(NBT_KEY)) {
			return;
		}

		CompoundTag hasFlowerCacheNBT = compoundNBT.getCompound(NBT_KEY);
		flowerCoords.clear();
		if (hasFlowerCacheNBT.contains(NBT_KEY_FLOWERS)) {
			int[] flowersList = hasFlowerCacheNBT.getIntArray(NBT_KEY_FLOWERS);
			if (flowersList.length % 3 == 0) {
				int flowerCount = flowersList.length / 3;

				flowerCoords.ensureCapacity(flowerCount);

				for (int i = 0; i < flowerCount; i++) {
					int index = i * 3;
					BlockPos flowerPos = new BlockPos(flowersList[index], flowersList[index + 1], flowersList[index + 2]);
					flowerCoords.add(flowerPos);
				}
				needsSync = true;
			}
		}
		flowers.clear();
	}

	@Override
	public CompoundTag write(CompoundTag CompoundNBT) {
		CompoundTag hasFlowerCacheNBT = new CompoundTag();

		if (!flowerCoords.isEmpty()) {
			int[] flowersList = new int[flowerCoords.size() * 3];
			int i = 0;
			for (BlockPos flowerPos : flowerCoords) {
				flowersList[i] = flowerPos.getX();
				flowersList[i + 1] = flowerPos.getY();
				flowersList[i + 2] = flowerPos.getZ();
				i += 3;
			}

			hasFlowerCacheNBT.putIntArray(NBT_KEY_FLOWERS, flowersList);
		}

		CompoundNBT.put(NBT_KEY, hasFlowerCacheNBT);
		return CompoundNBT;
	}

	public void writeData(FriendlyByteBuf data) {
		int size = flowerCoords.size();
		data.writeVarInt(size);
		if (size > 0) {
			for (BlockPos pos : flowerCoords) {
				data.writeVarInt(pos.getX());
				data.writeVarInt(pos.getY());
				data.writeVarInt(pos.getZ());
			}
		}
	}

	public void readData(FriendlyByteBuf data) {
		flowerCoords.clear();
		flowers.clear();

		int size = data.readVarInt();
		while (size > 0) {
			BlockPos pos = new BlockPos(data.readVarInt(), data.readVarInt(), data.readVarInt());
			flowerCoords.add(pos);
			size--;
		}
	}
}
