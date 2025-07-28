package org.bread_experts_group.breadmod.registry.menu.actual
//class WheatCrusherMenu(
//	id: Int,
//	inventory: Inventory,
//	parent: WheatCrusherBlockEntity
//) : BMContainerMenu.RecipeEntity<WheatCrusherRecipe, WheatCrusherBlockEntity>(
//	ModMenuTypes.WHEAT_CRUSHER.get(),
//	id,
//	inventory,
//	parent
//) {
//	constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
//		id, inventory,
//		BMContainerMenu.blockEntityFromByteBuf(inventory, byteBuf, ModBlockEntityTypes.WHEAT_CRUSHER)
//	)
//
//	override val progressWidth: Int = 48
//	override val containerSlotCount: Int = 2
//
//	init {
//		this.addInventorySlots(inventory, 8, 174, 116)
//		this.addHandlerSlot(0, 80, 15)
//		this.addResultHandlerSlot(1, 80, 87)
//	}
//}