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
package forestry.core.genetics.mutations;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.IMutationCondition;
import forestry.api.genetics.ISpecies;
import forestry.api.genetics.ISpeciesType;
import forestry.api.genetics.alleles.IAllele;
import forestry.api.genetics.alleles.IChromosome;
import forestry.core.features.CoreItems;
import forestry.core.genetics.ItemResearchNote;

public class Mutation<S extends ISpecies<?>> implements IMutation<S> {
	private final ISpeciesType<S, ?> type;
	private final int chance;
	private final List<IMutationCondition> conditions;
	private final List<Component> specialConditions;
	private final S firstParent;
	private final S secondParent;
	private final S result;
	private final Map<IChromosome<?>, IAllele> resultAlleles;
	private final boolean secret;

	public Mutation(ISpeciesType<S, ?> type, S firstParent, S secondParent, S result, Map<IChromosome<?>, IAllele> resultAlleles, int chance, List<IMutationCondition> conditions) {
		this.type = type;
		this.chance = chance;
		this.conditions = conditions;
		ImmutableList.Builder<Component> specialConditions = ImmutableList.builderWithExpectedSize(conditions.size());
		for (IMutationCondition condition : conditions) {
			specialConditions.add(condition.getDescription());
		}
		this.specialConditions = specialConditions.build();
		this.firstParent = firstParent;
		this.secondParent = secondParent;
		this.result = result;
		this.resultAlleles = resultAlleles;
		this.secret = result.isSecret() || firstParent.isSecret() || secondParent.isSecret();
	}

	public static float getChance(IMutation<?> mutation, Level level, BlockPos pos, ISpecies<?> firstParent, ISpecies<?> secondParent, IGenome firstGenome, IGenome secondGenome, IClimateProvider climate) {
		float mutationChance = mutation.getBaseChance();
		for (IMutationCondition condition : mutation.getConditions()) {
			mutationChance *= condition.getChance(level, pos, firstParent, secondParent, firstGenome, secondGenome, climate);
			if (mutationChance == 0f) {
				return 0f;
			}
		}
		return Math.max(0f, mutationChance);
	}

	@Override
	public ISpeciesType<S, ?> getType() {
		return this.type;
	}

	@Override
	public List<IMutationCondition> getConditions() {
		return this.conditions;
	}

	@Override
	public Collection<Component> getSpecialConditions() {
		return this.specialConditions;
	}

	@Override
	public S getFirstParent() {
		return this.firstParent;
	}

	@Override
	public S getSecondParent() {
		return this.secondParent;
	}

	@Override
	public S getResult() {
		return this.result;
	}

	@Override
	public Map<IChromosome<?>, IAllele> getResultAlleles() {
		return this.resultAlleles;
	}

	@Override
	public int getBaseChance() {
		return this.chance;
	}

	@Override
	public boolean isPartner(ISpecies<?> other) {
		return this.firstParent.id().equals(other.id()) || this.secondParent.id().equals(other.id());
	}

	@Override
	public ISpecies<?> getPartner(ISpecies<?> species) {
		if (firstParent.id().equals(species.id())) {
			return secondParent;
		} else if (secondParent.id().equals(species.id())) {
			return firstParent;
		} else {
			throw new IllegalArgumentException("Tried to get partner for allele that is not part of this mutation.");
		}
	}

	@Override
	public boolean isSecret() {
		return this.secret;
	}

	@Override
	public ItemStack getMutationNote(GameProfile researcher) {
		return ItemResearchNote.createMutationNoteStack(researcher, this);
	}
}
