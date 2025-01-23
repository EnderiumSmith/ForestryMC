package forestry.apiculture.genetics.effects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import forestry.api.IForestryApi;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBeeEffect;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.alleles.BeeChromosomes;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import forestry.apiculture.genetics.Bee;
import forestry.core.tiles.TileUtil;

// An effect applied to other bee hives that shouldn't stack (ex. the Chronophage and Rejuvenation effects)
public abstract class NonStackingBeeEffect implements IBeeEffect {
	private final HashMap<ResourceKey<Level>, HashSet<BlockPos>> trackedOwners;
	private final boolean dominant;

	public NonStackingBeeEffect(boolean dominant) {
		this.dominant = dominant;
		this.trackedOwners = new HashMap<>();

		MinecraftForge.EVENT_BUS.addListener(this::performGlobalEffect);
	}

	@Override
	public IEffectData doEffect(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		// Don't spam adding to the set
		if ((housing.getWorldObj().getGameTime() & 64L) == 0) {
			this.trackedOwners.computeIfAbsent(housing.getWorldObj().dimension(), key -> new HashSet<>()).add(housing.getCoordinates());
		}
		return IBeeEffect.super.doEffect(genome, storedData, housing);
	}

	@Override
	public boolean isDominant() {
		return this.dominant;
	}

	private void performGlobalEffect(TickEvent.LevelTickEvent event) {
		if (event.phase != TickEvent.Phase.START) {
			return;
		}

		Level level = event.level;

		if (level.isClientSide || level.getGameTime() % IBeekeepingLogic.DEFAULT_WORK_THROTTLE != 0) {
			return;
		}

		HashSet<BlockPos> owners = this.trackedOwners.get(level.dimension());
		HashSet<BlockPos> affectedHives = new HashSet<>();

		for (Iterator<BlockPos> iterator = owners.iterator(); iterator.hasNext(); ) {
			BlockPos pos = iterator.next();
			IBeeHousing housing = TileUtil.getTile(level, pos, IBeeHousing.class);

			// Don't want to call canWork twice in one tick
			if (housing != null && !housing.getErrorLogic().hasErrors()) {
				IIndividualHandlerItem handler = IIndividualHandlerItem.get(housing.getBeeInventory().getQueen());

				if (handler != null && handler.getStage() == BeeLifeStage.QUEEN) {
					IIndividual queen = handler.getIndividual();
					IGenome genome = queen.getGenome();

					if (genome.getActiveValue(BeeChromosomes.EFFECT) == this || genome.getInactiveValue(BeeChromosomes.EFFECT) == this) {
						IBeeModifier modifier = IForestryApi.INSTANCE.getHiveManager().createBeeHousingModifier(housing);
						Vec3i territory = Bee.getAdjustedTerritory(genome, modifier);

						applyEffectToNearbyTiles(affectedHives, level, pos, territory);

						// Skips the iterator.remove() at the end of the loop
						continue;
					}
				}
			}

			iterator.remove();
		}
	}

	private void applyEffectToNearbyTiles(HashSet<BlockPos> affectedHives, Level level, BlockPos pos, Vec3i territory) {
		int x = pos.getX();
		int z = pos.getZ();
		int xWidth = territory.getX();
		int zWidth = territory.getZ();
		int xChunksPositive = (x % 16 + xWidth) / 16;
		int zChunksPositive = (z % 16 + zWidth) / 16;
		int xChunksNegative = (x % 16 - xWidth) / 16;
		int zChunksNegative = (z % 16 - zWidth) / 16;
		int chunkX = SectionPos.blockToSectionCoord(pos.getX());
		int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());

		for (int i = chunkX - xChunksPositive; i <= chunkX + xChunksNegative; i++) {
			for (int j = chunkZ - zChunksPositive; j <= chunkZ + zChunksNegative; j++) {
				level.getChunk(i, j).blockEntities.forEach((targetPos, blockEntity) -> {
					if (blockEntity instanceof IBeeHousing housing) {
						int distX = Math.abs(pos.getX() - targetPos.getX());
						int distY = Math.abs(pos.getY() - targetPos.getY());
						int distZ = Math.abs(pos.getZ() - targetPos.getZ());

						if (distX > territory.getX() || distY > territory.getY() || distZ > territory.getZ()) {
							return;
						}

						if (affectedHives.add(targetPos)) {
							doEffectForHive(level, housing);
						}
					}
				});
			}
		}
	}

	protected abstract void doEffectForHive(Level level, IBeeHousing housing);
}