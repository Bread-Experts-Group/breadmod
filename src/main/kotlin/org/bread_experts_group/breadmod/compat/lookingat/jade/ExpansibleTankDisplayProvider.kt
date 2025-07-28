package org.bread_experts_group.breadmod.compat.lookingat.jade
//object ExpansibleTankDisplayProvider : IBlockComponentProvider {
//	@DataGenerateLanguage("en_us", "Fluid Data Provider", prefix = "config.jade.plugin_")
//	override fun getUid(): ResourceLocation = modLocation("fluid_data_provider")
//	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
//		val entity = accessor.blockEntity as? FluidBearingBlockEntity ?: return
//		val sides = accessor.blockEntity as? FacingSensitiveProviderRetriever
//		tooltip.remove(JadeIds.UNIVERSAL_FLUID_STORAGE)
//		if (sides == null) {
//			for (tankIndex in 0 ..< entity.fluidHandler.getUnits()) {
//				tooltip.add(FluidBarElement(entity.fluidHandler.getUnit(tankIndex), null))
//			}
//		} else {
//			for (direction: Direction in Direction.entries) {
//				val tankIndex = sides.getIndexForBlockProvider(Capabilities.FluidHandler.BLOCK, direction)
//				if (tankIndex > 0) tooltip.add(FluidBarElement(entity.fluidHandler.getUnit(tankIndex), direction))
//			}
//		}
//	}
//
//	override fun getDefaultPriority(): Int = TooltipPosition.TAIL
//}