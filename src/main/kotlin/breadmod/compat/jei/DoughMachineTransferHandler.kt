package breadmod.compat.jei

import breadmod.block.entity.menu.DoughMachineMenu
import breadmod.recipe.FluidEnergyRecipe
import it.unimi.dsi.fastutil.ints.IntArraySet
import mezz.jei.api.gui.ingredient.IRecipeSlotView
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.transfer.IRecipeTransferError
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import java.util.*

class DoughMachineTransferHandler(private val handlerHelper: IRecipeTransferHandlerHelper): IRecipeTransferHandler<DoughMachineMenu, FluidEnergyRecipe> {
    private val playerInvIndexes = IntArraySet.of(0, 1) // index only accounts for the item input slot and the fluid input tank
    private val basicRecipeTransferInfo = handlerHelper.createBasicRecipeTransferInfo(DoughMachineMenu::class.java, null, ModJEIRecipeTypes.fluidEnergyRecipeType, 0, 2, 3, 36)
    private val handler: IRecipeTransferHandler<DoughMachineMenu, FluidEnergyRecipe> = handlerHelper.createUnregisteredRecipeTransferHandler(basicRecipeTransferInfo)

    override fun getContainerClass(): Class<out DoughMachineMenu> = handler.containerClass
    override fun getMenuType(): Optional<MenuType<DoughMachineMenu>> = handler.menuType
    override fun getRecipeType(): RecipeType<FluidEnergyRecipe> = ModJEIRecipeTypes.fluidEnergyRecipeType

    override fun transferRecipe(
        container: DoughMachineMenu,
        recipe: FluidEnergyRecipe,
        recipeSlotsView: IRecipeSlotsView,
        player: Player,
        maxTransfer: Boolean,
        doTransfer: Boolean
    ): IRecipeTransferError? {
        if(!handlerHelper.recipeTransferHasServerSupport()) {
            val tooltipMessage = Component.translatable("jei.tooltip.error.recipe.transfer.no.server")
            return this.handlerHelper.createUserErrorWithTooltip(tooltipMessage)
        } else {
            val slotViews = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT)
            val filteredSlotViews = filterSlots(slotViews)
            val filteredRecipeSlots = handlerHelper.createRecipeSlotsView(filteredSlotViews)
            return handler.transferRecipe(container, recipe, filteredRecipeSlots, player, maxTransfer, doTransfer)
        }
    }

    private fun filterSlots(slotViews: List<IRecipeSlotView>): List<IRecipeSlotView> {
        val playerInvIndexesStream = playerInvIndexes.intStream()
        Objects.requireNonNull(slotViews)
        return playerInvIndexesStream.mapToObj{index -> slotViews[index]}.toList()
    }
}
