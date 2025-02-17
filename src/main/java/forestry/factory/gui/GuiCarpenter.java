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
package forestry.factory.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.factory.tiles.TileCarpenter;

public class GuiCarpenter extends GuiForestryTitled<ContainerCarpenter> {
	private final TileCarpenter tile;

	public GuiCarpenter(ContainerCarpenter container, Inventory inventory, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/carpenter.png", container, inventory, title);

		this.tile = container.getTile();
		this.imageHeight = 218;
		this.widgetManager.add(new TankWidget(this.widgetManager, 150, 17, 0));
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(graphics, partialTicks, mouseY, mouseX);
		int progressScaled = tile.getProgressScaled(16);
		graphics.blit(this.textureFile, leftPos + 98, topPos + 51 + 16 - progressScaled, 176, 60 + 16 - progressScaled, 4, progressScaled);
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addPowerLedger(tile.getEnergyManager());
		addHintLedger("carpenter");
	}
}
