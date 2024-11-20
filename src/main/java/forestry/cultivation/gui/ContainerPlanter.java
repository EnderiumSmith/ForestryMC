package forestry.cultivation.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketGuiStream;
import forestry.core.tiles.TileUtil;
import forestry.cultivation.features.CultivationMenuTypes;
import forestry.cultivation.inventory.InventoryPlanter;
import forestry.cultivation.tiles.TilePlanter;

public class ContainerPlanter extends ContainerLiquidTanks<TilePlanter> {
	public static ContainerPlanter fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
		TilePlanter planter = TileUtil.getTile(playerInv.player.level(), extraData.readBlockPos(), TilePlanter.class);
		return new ContainerPlanter(windowId, playerInv, planter);
	}

	public ContainerPlanter(int windowId, Inventory playerInventory, TilePlanter tileForestry) {
		super(windowId, CultivationMenuTypes.PLANTER.menuType(), playerInventory, tileForestry, 21, 110);

		// Resources
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new SlotFiltered(tile.getInternalInventory(), InventoryPlanter.CONFIG.resourcesStart + j + i * 2, 11 + j * 18, 65 + i * 18));
			}
		}

		// Germlings
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new SlotFiltered(tile.getInternalInventory(), InventoryPlanter.CONFIG.germlingsStart + j + i * 2, 71 + j * 18, 65 + i * 18));
			}
		}

		// Production
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlot(new SlotOutput(tile.getInternalInventory(), InventoryPlanter.CONFIG.productionStart + j + i * 2, 131 + j * 18, 65 + i * 18));
			}
		}

		// Fertilizer
		addSlot(new SlotFiltered(tile.getInternalInventory(), InventoryPlanter.CONFIG.fertilizerStart, 83, 22));
		// Can Slot
		addSlot(new SlotLiquidIn(tile.getInternalInventory(), InventoryPlanter.CONFIG.canStart, 178, 18));
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		PacketGuiStream packet = new PacketGuiStream(tile);
		sendPacketToListeners(packet);
	}
}
