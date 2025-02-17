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
package forestry.farming.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.utils.ResourceUtil;
import forestry.core.utils.StringUtil;

public class FarmLedger extends Ledger {
	private final IFarmLedgerDelegate delegate;

	public FarmLedger(LedgerManager ledgerManager, IFarmLedgerDelegate delegate) {
		super(ledgerManager, "farm");
		this.delegate = delegate;

		int titleHeight = StringUtil.getLineHeight(maxTextWidth, getTooltip());
		this.maxHeight = titleHeight + 110;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void draw(GuiGraphics graphics, int y, int x) {

		// Draw background
		drawBackground(graphics, y, x);
		y += 4;

		int xIcon = x + 3;
		int xBody = x + 10;
		int xHeader = x + 22;

		// Draw icon
		TextureAtlasSprite textureAtlasSprite = ResourceUtil.getBlockSprite("item/water_bucket");
		drawSprite(graphics, textureAtlasSprite, xIcon, y, TextureAtlas.LOCATION_BLOCKS);
		y += 4;

		if (!isFullyOpened()) {
			return;
		}

		y += drawHeader(graphics, Component.translatable("for.gui.hydration"), xHeader, y);
		y += 4;

		y += drawSubheader(graphics, Component.translatable("for.gui.hydr.heat").append(":"), xBody, y);
		y += 3;
		y += drawText(graphics, StringUtil.floatAsPercent(delegate.getHydrationTempModifier()), xBody, y);
		y += 3;

		y += drawSubheader(graphics, Component.translatable("for.gui.hydr.humid").append(":"), xBody, y);
		y += 3;
		y += drawText(graphics, StringUtil.floatAsPercent(delegate.getHydrationHumidModifier()), xBody, y);
		y += 3;

		y += drawSubheader(graphics, Component.translatable("for.gui.hydr.rainfall").append(":"), xBody, y);
		y += 3;
		y += drawText(graphics, StringUtil.floatAsPercent(delegate.getHydrationRainfallModifier()) + " (" + delegate.getDrought() + " d)", xBody, y);
		y += 3;

		y += drawSubheader(graphics, Component.translatable("for.gui.hydr.overall").append(":"), xBody, y);
		y += 3;
		drawText(graphics, StringUtil.floatAsPercent(delegate.getHydrationModifier()), xBody, y);
	}

	@Override
	public Component getTooltip() {
		float hydrationModifier = delegate.getHydrationModifier();
		return Component.literal(StringUtil.floatAsPercent(hydrationModifier) + ' ')
				.append(Component.translatable("for.gui.hydration"));
	}
}