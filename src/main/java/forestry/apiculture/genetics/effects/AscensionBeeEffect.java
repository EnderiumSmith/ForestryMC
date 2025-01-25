package forestry.apiculture.genetics.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IGenome;
import forestry.apiculture.genetics.Bee;
import forestry.core.utils.VecUtil;

public class AscensionBeeEffect extends PotionBeeEffect{
	public AscensionBeeEffect() {
		super(true, MobEffects.LEVITATION, 200);
	}

	@Override
	public IEffectData doFX(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		super.doFX(genome, storedData, housing);
		RandomSource rand=housing.getWorldObj().random;
		Vec3i area = Bee.getParticleArea(genome, housing);
		BlockPos coordinates = housing.getCoordinates().offset(VecUtil.center(area));
		housing.getWorldObj().addParticle(ParticleTypes.END_ROD,coordinates.getX()+rand.nextFloat()*area.getX(),coordinates.getY()+rand.nextFloat()*area.getY(), coordinates.getZ()+rand.nextFloat()*area.getZ(), 0D, 0.5D*rand.nextFloat(), 0D);
		return storedData;
	}
}
