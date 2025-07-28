package org.bread_experts_group.breadmod.compat.lookingat.jade
//object ExpansibleCellDisplayProvider : IBlockComponentProvider {
//	@DataGenerateLanguage("en_us", "Energy Data Provider", prefix = "config.jade.plugin_")
//	override fun getUid(): ResourceLocation = modLocation("energy_data_provider")
//	override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
//		val entity = accessor.blockEntity as? EnergyBearingBlockEntity ?: return
//		val sides = accessor.blockEntity as? FacingSensitiveProviderRetriever
//		tooltip.remove(JadeIds.UNIVERSAL_ENERGY_STORAGE)
//		if (sides == null) {
//			for (cellIndex in 0 ..< entity.energyHandler.getUnits()) {
//				tooltip.add(EnergyBarElement(entity.energyHandler.getUnit(cellIndex), null))
//			}
//		} else {
//			for (direction: Direction in Direction.entries) {
//				val cellIndex = sides.getIndexForBlockProvider(Capabilities.EnergyStorage.BLOCK, direction)
//				if (cellIndex > 0) tooltip.add(EnergyBarElement(entity.energyHandler.getUnit(cellIndex), direction))
//			}
//		}
//	}
//
//	override fun getDefaultPriority(): Int = TooltipPosition.TAIL
//}