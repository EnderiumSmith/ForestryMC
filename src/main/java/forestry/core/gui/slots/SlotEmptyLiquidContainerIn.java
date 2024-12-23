package forestry.core.gui.slots;

import net.minecraft.world.Container;

import forestry.api.client.ForestrySprites;
import forestry.core.tiles.IFilterSlotDelegate;

public class SlotEmptyLiquidContainerIn extends SlotFiltered {
	public <T extends Container & IFilterSlotDelegate> SlotEmptyLiquidContainerIn(T inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		setBackgroundSprite(ForestrySprites.SLOT_CONTAINER);
	}
}
