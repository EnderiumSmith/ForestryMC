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
package forestry.arboriculture.worldgen;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import forestry.api.arboriculture.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

public class FeatureWalnut extends FeatureTree {

	public FeatureWalnut(ITreeGenData tree) {
		super(tree, 7, 3);
	}

	@Override
	public Set<BlockPos> generateTrunk(LevelAccessor level, RandomSource rand, TreeBlockTypeLog wood, BlockPos startPos) {
		FeatureHelper.generateTreeTrunk(level, rand, wood, startPos, height, girth, 0, 0, null, 0);

		Set<BlockPos> branchEnds = new HashSet<>();

		int branchHeight = height - 3;
		float branchSize = 2;
		while (branchHeight >= 3) {
			branchEnds.addAll(FeatureHelper.generateBranches(level, rand, wood, startPos.offset(0, branchHeight, 0), girth, 0.2f, 0.2f, (int) branchSize, 1, 0.5f));
			branchHeight--;
			branchSize += 0.25f;
		}

		return branchEnds;
	}

	@Override
	protected void generateLeaves(LevelAccessor level, RandomSource rand, TreeBlockTypeLeaf leaf, TreeContour contour, BlockPos startPos) {
		int leafSpawn = height + 1;

		FeatureHelper.generateCylinderFromTreeStartPos(level, leaf, startPos.offset(0, leafSpawn--, 0), girth, girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
		FeatureHelper.generateCylinderFromTreeStartPos(level, leaf, startPos.offset(0, leafSpawn--, 0), girth, 0.5f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);

		FeatureHelper.generateCylinderFromTreeStartPos(level, leaf, startPos.offset(0, leafSpawn--, 0), girth, 1.5f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
		FeatureHelper.generateCylinderFromTreeStartPos(level, leaf, startPos.offset(0, leafSpawn--, 0), girth, 2f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);

		float branchSize = 2;
		while (leafSpawn >= 3) {
			int leafRadius = Math.min(4, (int) branchSize);
			for (BlockPos branchEnd : contour.getBranchEnds()) {
				FeatureHelper.generateCircle(level, rand, branchEnd, leafRadius, 2, 2, leaf, 1.0f, FeatureHelper.EnumReplaceMode.SOFT, contour);
			}
			leafSpawn--;
			branchSize += 0.25f;
		}
	}
}
