package forestry.farming.logic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import forestry.api.IForestryApi;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmPropertiesBuilder;
import forestry.api.farming.IFarmRegistry;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.api.farming.IWaterConsumption;
import forestry.api.farming.Soil;

public final class FarmProperties implements IFarmProperties {
	private final Set<Soil> soils;
	private final IFarmLogic manualLogic;
	private final IFarmLogic managedLogic;
	private final Supplier<ItemStack> icon;
	private final Collection<IFarmable> farmables;
	private final Collection<IFarmableInfo> farmableInfo;
	private final ToIntFunction<IFarmHousing> fertilizerConsumption;
	private final IWaterConsumption waterConsumption;
	private final String translationKey;

	public FarmProperties(Builder builder) {
		Preconditions.checkNotNull(builder.factory);
		Preconditions.checkNotNull(builder.icon);
		Preconditions.checkNotNull(builder.waterConsumption);
		Preconditions.checkNotNull(builder.fertilizerConsumption);
		Preconditions.checkNotNull(builder.translationKey);
		IFarmRegistry registry = IForestryApi.INSTANCE.getFarmRegistry();
		this.manualLogic = builder.factory.apply(this, true);
		this.managedLogic = builder.factory.apply(this, false);
		this.soils = ImmutableSet.copyOf(builder.soils);
		ImmutableSet.Builder<IFarmable> farmableBuilder = new ImmutableSet.Builder<>();
		ImmutableSet.Builder<IFarmableInfo> infoBuilder = new ImmutableSet.Builder<>();
		for (String farmableIdentifier : builder.farmablesIdentifiers) {
			farmableBuilder.addAll(registry.getFarmables(farmableIdentifier));
			infoBuilder.add(registry.getFarmableInfo(farmableIdentifier));
		}
		this.farmables = farmableBuilder.build();
		this.farmableInfo = infoBuilder.build();
		this.fertilizerConsumption = builder.fertilizerConsumption;
		this.waterConsumption = builder.waterConsumption;
		this.translationKey = builder.translationKey;
		this.icon = builder.icon;
	}

	@Override
	public Collection<IFarmable> getFarmables() {
		return farmables;
	}

	@Override
	public Collection<IFarmableInfo> getFarmableInfo() {
		return farmableInfo;
	}

	@Override
	public IFarmLogic getLogic(boolean manuel) {
		return manuel ? manualLogic : managedLogic;
	}

	@Override
	public boolean isAcceptedSoil(BlockState ground) {
		for (Soil soil : soils) {
			BlockState soilState = soil.soilState();
			if (soilState.getBlock() == ground.getBlock()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		for (Soil soil : soils) {
			if (soil.resource().sameItem(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getFertilizerConsumption(IFarmHousing housing) {
		return fertilizerConsumption.applyAsInt(housing);
	}

	@Override
	public int getWaterConsumption(IFarmHousing housing, float hydrationModifier) {
		return this.waterConsumption.get(housing, hydrationModifier);
	}

	@Override
	public Component getDisplayName(boolean manual) {
		String unformatted = manual ? "for.farm.grammar.manual" : "for.farm.grammar.managed";
		return Component.translatable(unformatted, Component.translatable(translationKey));
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}

	@Override
	public ItemStack getIcon() {
		return icon.get();
	}

	@Override
	public boolean isAcceptedSeedling(ItemStack itemstack) {
		for (IFarmable farmable : farmables) {
			if (farmable.isGermling(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack itemstack) {
		for (IFarmable farmable : farmables) {
			if (farmable.isWindfall(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<Soil> getSoils() {
		return soils;
	}

	public static class Builder implements IFarmPropertiesBuilder {
		private final String identifier;
		private final Set<Soil> soils = new HashSet<>();
		private final Set<String> farmablesIdentifiers = new HashSet<>();
		private final IFarmableInfo defaultInfo;
		@Nullable
		private BiFunction<IFarmProperties, Boolean, IFarmLogic> factory;
		@Nullable
		private Supplier<ItemStack> icon;
		@Nullable
		private ToIntFunction<IFarmHousing> fertilizerConsumption;
		@Nullable
		private IWaterConsumption waterConsumption;
		@Nullable
		private String translationKey;

		public Builder(String identifier) {
			this.identifier = identifier;
			this.defaultInfo = IForestryApi.INSTANCE.getFarmRegistry().getFarmableInfo(identifier);
		}

		@Override
		public IFarmPropertiesBuilder addFarmables(String... identifiers) {
			farmablesIdentifiers.addAll(Arrays.asList(identifiers));
			return this;
		}

		@Override
		public IFarmPropertiesBuilder setFactory(BiFunction<IFarmProperties, Boolean, IFarmLogic> factory) {
			this.factory = factory;
			return this;
		}

		@Override
		public IFarmPropertiesBuilder setIcon(Supplier<ItemStack> stackSupplier) {
			this.icon = stackSupplier;
			return this;
		}

		@Override
		public IFarmPropertiesBuilder setFertilizer(ToIntFunction<IFarmHousing> consumption) {
			this.fertilizerConsumption = consumption;
			return this;
		}

		@Override
		public IFarmPropertiesBuilder setWaterConsumption(IWaterConsumption waterConsumption) {
			this.waterConsumption = waterConsumption;
			return this;
		}

		@Override
		public IFarmPropertiesBuilder setTranslationKey(String translationKey) {
			this.translationKey = translationKey;
			return this;
		}

		/*@Override
	public IFarmPropertiesBuilder setResourcePredicate(Predicate<ItemStack> isResource) {
		return this;
	}

	@Override
	public IFarmPropertiesBuilder setSeedlingPredicate(Predicate<ItemStack> isSeedling) {
		return null;
	}

	@Override
	public IFarmPropertiesBuilder setWindfallPredicate(Predicate<ItemStack> isWindfall) {
		return null;
	}*/

		@Override
		public IFarmPropertiesBuilder addSoil(ItemStack resource, BlockState soilState) {
			soils.add(new Soil(resource, soilState));
			return this;
		}

		@Override
		public IFarmPropertiesBuilder addSeedlings(ItemStack... seedling) {
			defaultInfo.addSeedlings(seedling);
			return this;
		}

		@Override
		public IFarmPropertiesBuilder addSeedlings(Collection<ItemStack> seedling) {
			defaultInfo.addSeedlings(seedling);
			return this;
		}

		@Override
		public IFarmPropertiesBuilder addProducts(ItemStack... products) {
			defaultInfo.addProducts(products);
			return this;
		}

		@Override
		public IFarmPropertiesBuilder addProducts(Collection<ItemStack> products) {
			defaultInfo.addProducts(products);
			return this;
		}

		@Override
		public IFarmProperties create() {
			return IForestryApi.INSTANCE.getFarmRegistry().registerProperties("farm" + WordUtils.capitalize(identifier), new FarmProperties(this));
		}
	}
}
