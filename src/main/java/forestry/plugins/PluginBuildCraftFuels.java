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
package forestry.plugins;

import java.awt.Color;

import net.minecraft.block.Block;

import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.Optional;

import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import forestry.core.items.ItemLiquidContainer.EnumContainerType;

@Plugin(pluginID = "BC6|Fuels", name = "BuildCraft 6 Fuels", author = "mezz", url = Defaults.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
public class PluginBuildCraftFuels extends ForestryPlugin {

	@Override
	public boolean isAvailable() {
		return Proxies.common.isAPILoaded("buildcraft.api.fuels", "[2.0, 2.1)");
	}

	@Override
	public String getFailMessage() {
		return "Compatible BuildCraftAPI|fuels version not found";
	}

	@Optional.Method(modid = "BuildCraftAPI|fuels")
	@Override
	public void doInit() {
		BuildcraftFuelRegistry.coolant.addCoolant(Fluids.ICE.get(), 10.0f);

		Fluid ethanol = Fluids.BIOETHANOL.get();
		if (ethanol != null) {
			int ethanolPower = 40;
			int ethanolBurnTime = Math.round(Defaults.ENGINE_CYCLE_DURATION_ETHANOL * GameMode.getGameMode().getFloatSetting("fuel.ethanol.combustion"));
			BuildcraftFuelRegistry.fuel.addFuel(ethanol, ethanolPower, ethanolBurnTime);
		}

		Fluid oil = Fluids.OIL.get();
		if (oil != null) {
			Block oilBlock = oil.getBlock();
			if (oilBlock != null) {
				Color oilColor = new Color(0x404040);
				ForestryItem.waxCapsuleOil.registerContainer(EnumContainerType.CAPSULE, oilBlock, oilColor);
				ForestryItem.canOil.registerContainer(EnumContainerType.CAN, oilBlock, oilColor);
				ForestryItem.refractoryOil.registerContainer(EnumContainerType.REFRACTORY, oilBlock, oilColor);

				LiquidHelper.injectLiquidContainer(Fluids.OIL, ForestryItem.waxCapsuleOil.getItemStack());
				LiquidHelper.injectLiquidContainer(Fluids.OIL, ForestryItem.refractoryOil.getItemStack());
				LiquidHelper.injectLiquidContainer(Fluids.OIL, ForestryItem.canOil.getItemStack());
			}
		}

		Fluid fuel = Fluids.FUEL.get();
		if (fuel != null) {
			Block fuelBlock = fuel.getBlock();
			if (fuelBlock != null) {
				Color fuelColor = new Color(0xffff00);
				ForestryItem.waxCapsuleFuel.registerContainer(EnumContainerType.CAPSULE, fuelBlock, fuelColor);
				ForestryItem.canFuel.registerContainer(EnumContainerType.CAN, fuelBlock, fuelColor);
				ForestryItem.refractoryFuel.registerContainer(EnumContainerType.REFRACTORY, fuelBlock, fuelColor);

				LiquidHelper.injectLiquidContainer(Fluids.FUEL, ForestryItem.waxCapsuleFuel.getItemStack());
				LiquidHelper.injectLiquidContainer(Fluids.FUEL, ForestryItem.refractoryFuel.getItemStack());
				LiquidHelper.injectLiquidContainer(Fluids.FUEL, ForestryItem.canFuel.getItemStack());
			}
		}
	}

}
