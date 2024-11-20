package forestry.cultivation.gui;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.cultivation.gui.widgets.GhostItemStackWidget;
import forestry.cultivation.inventory.InventoryPlanter;
import forestry.cultivation.tiles.TilePlanter;
import forestry.farming.gui.FarmLedger;

public class GuiPlanter extends GuiForestryTitled<ContainerPlanter> {
	private final TilePlanter tile;

	public GuiPlanter(ContainerPlanter container, Inventory playerInventory, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/planter.png", container, playerInventory, title);
		this.tile = container.getTile();
		this.imageWidth = 202;
		this.imageHeight = 192;

		List<ItemStack> resourceStacks = tile.createResourceStacks();
		List<ItemStack> germlingStacks = tile.createGermlingStacks();
		List<ItemStack> productionStacks = tile.createProductionStacks();

		widgetManager.add(new TankWidget(widgetManager, 178, 44, 0).setOverlayOrigin(imageWidth, 18));

		// Resources
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int index = j + i * 2;
				if (resourceStacks.size() == 4) {
					widgetManager.add(new GhostItemStackWidget(widgetManager, 11 + j * 18, 65 + i * 18, resourceStacks.get(index), this.getMenu().getSlot(36 + InventoryPlanter.CONFIG.resourcesStart + index)));
				}
			}
		}

		// Germlings
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int index = j + i * 2;
				if (germlingStacks.size() == 4) {
					widgetManager.add(new GhostItemStackWidget(widgetManager, 71 + j * 18, 65 + i * 18, germlingStacks.get(index), this.getMenu().getSlot(36 + InventoryPlanter.CONFIG.germlingsStart + index)));
				}
			}
		}

		// Production
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int index = j + i * 2;
				if (productionStacks.size() == 4) {
					widgetManager.add(new GhostItemStackWidget(widgetManager, 131 + j * 18, 65 + i * 18, productionStacks.get(index), getMenu().getSlot(36 + InventoryPlanter.CONFIG.productionStart + j + i * 2)));
				}
			}
		}

		widgetManager.add(new GhostItemStackWidget(widgetManager, 83, 22, CoreItems.FERTILIZER_COMPOUND.stack(), getMenu().getSlot(36 + InventoryPlanter.CONFIG.fertilizerStart)));
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addClimateLedger(tile);
		ledgerManager.add(new FarmLedger(ledgerManager, tile.getFarmLedgerDelegate()));
		addOwnerLedger(tile);
		addPowerLedger(tile.getEnergyManager());
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(graphics, partialTicks, mouseY, mouseX);

		// Fuel remaining
		int fertilizerRemain = tile.getStoredFertilizerScaled(16);
		if (fertilizerRemain > 0) {
			graphics.blit(this.textureFile, leftPos + 101, topPos + 21 + 17 - fertilizerRemain, imageWidth, 17 - fertilizerRemain, 4, fertilizerRemain);
		}
	}
}
