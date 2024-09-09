package bread.mod.breadmod.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import java.text.DecimalFormat

// todo port over config and features using data components
class TieredBreadAmuletItem(
    private val pType: Enum<BreadAmuletType>,
    pDurability: Int
) : Item(Properties()
    .durability(if(pDurability > 0) pDurability else Int.MAX_VALUE)
) {

    companion object {
//        private val feedTime = COMMON.BREAD_AMULET_FEED_TIME_TICKS
//        private val feedAmt = COMMON.BREAD_AMULET_FEED_AMOUNT
//        private val feedStacks = COMMON.BREAD_AMULET_STACKS

        private val decimalFormat = DecimalFormat("0.#")
        data class PlayerData(var timeLeft: Int, var lastExec: Int)
        private val timers = mutableMapOf<String,PlayerData>()
    }

    // todo item durability not lowering on item trigger, possibly due to multiple amulets in the same inventory
//    private fun playerFood(pPlayer: ServerPlayer, slot: EquipmentSlot) {
//        val timer = timers.getOrPut(pPlayer.stringUUID) { PlayerData(feedTime.get(), 0) }
//        if(feedStacks.get() || timer.lastExec != pPlayer.tickCount) {
//            val hungerLevel = pPlayer.foodData.foodLevel
//            if(hungerLevel <= 20 && timer.timeLeft <= 0) {
//                if(pType == BreadAmuletType.NORMAL || pType == BreadAmuletType.REINFORCED) {
//                    ItemStack(this).hurtAndBreak(1, pPlayer, slot)
//                }
//                pPlayer.foodData.foodLevel += feedAmt.get()
//                timer.timeLeft = feedTime.get()
//            } else if(hungerLevel <= 19) timer.timeLeft--
//            timer.lastExec = pPlayer.tickCount
//        }
//    }

//    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) =
//        if(entity is ServerPlayer) playerFood(entity, entity.getEquipmentSlotForItem(ItemStack(this))) else {}

//    override fun appendHoverText(
//        stack: ItemStack,
//        context: TooltipContext,
//        tooltipComponents: MutableList<Component>,
//        tooltipFlag: TooltipFlag
//    ) {
//        val bars = feedAmt.get().toDouble() / 2
//        val secDelay = feedTime.get().toDouble() / 20
//        tooltipComponents.add(modTranslatable(
//            "item",
//            "bread_amulet", "description",
//            args = listOf(
//                if(bars == 1.0) "a bar" else "${decimalFormat.format(bars)} bars",
//                if(secDelay == 1.0) "second" else "${decimalFormat.format(secDelay)} seconds"
//            )
//        ).append(
//            if(feedStacks.get()) Component.literal(" ").append(modTranslatable("item", "bread_amulet", "stacks"))
//            else Component.empty()
//        ).withStyle(ChatFormatting.GOLD))
//    }

    // todo probably won't port this
//    override fun initCapabilities(stack: ItemStack, nbt: CompoundTag?): ICapabilityProvider? =
//        if(ModList.get().isLoaded("curios")) {
//            CuriosApi.createCurioProvider(object : ICurio {
//                override fun getStack(): ItemStack {
//                    return stack
//                }
//
//                override fun curioTick(slotContext: SlotContext) = slotContext.entity.let {
//                    if(it !is ServerPlayer) return
//                    playerFood(it)
//                }
//            })
//        } else null

    enum class BreadAmuletType { NORMAL, REINFORCED, INDESTRUCTIBLE }
}