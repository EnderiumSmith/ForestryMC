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
package forestry.apiculture.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ParticleSnow extends TextureSheetParticle {
	public static final TextureAtlasSprite[] SPRITES = new TextureAtlasSprite[3];

	public ParticleSnow(ClientLevel level, double x, double y, double z) {
		super(level, x, y, z, 0, 0, 0);

		this.setSprite(SPRITES[this.random.nextInt(SPRITES.length)]);
		this.quadSize *= 0.5F;
		this.lifetime = (int) (40 / (this.random.nextDouble() * 0.8D + 0.2D));

		this.xd *= 0.01D;
		this.yd *= -0.4D;
		this.zd *= 0.01D;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
}
