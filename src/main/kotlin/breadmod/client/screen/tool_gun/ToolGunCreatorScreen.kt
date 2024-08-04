package breadmod.client.screen.tool_gun

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.registry.item.ModItems
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraftforge.registries.ForgeRegistries
import java.awt.Color
import kotlin.jvm.optionals.getOrNull

class ToolGunCreatorScreen(
    pMenu: ToolGunCreatorMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<ToolGunCreatorMenu>(pMenu, pPlayerInventory, pTitle) {

    companion object {
        val TEXTURE = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode.png")
        val TEXTURE_ASSETS = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode_assets.png")
        val instance: Minecraft = Minecraft.getInstance()
        var entityScale = 32
        var setEntity: String = "creeper"
    }

    init {
        imageWidth = 256
        imageHeight = 220
    }

    private val entityX = 35
    private val entityY = 94
    private var entityType: EntityType<*>? = getEntity(setEntity)

    private val helmetSlot = 103
    private val chestplateSlot = 102
    private val leggingsSlot = 101
    private val bootsSlot = 100
    private val mainHandSlot = 98
    private val offHandSlot = 99

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        entityType = getEntity(setEntity)
        renderBackground(pGuiGraphics)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
//        println(setEntity)
    }

    // renderBg is called after render
    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)

        val level = instance.level ?: return
        val finalEntity = entityType?.create(level) ?: return
        val poseStack = pGuiGraphics.pose()

//        println("width: ${entity.type.width}, height: ${entity.type.height}")
//        theEntity.customName = Component.literal("jim")

        finalEntity.getSlot(helmetSlot).set(ModItems.BREAD_HELMET.get().defaultInstance)
//            setEntity.getSlot(102).set(ModItems.BREAD_CHESTPLATE.get().defaultInstance)
//            setEntity.getSlot(101).set(ModItems.BREAD_LEGGINGS.get().defaultInstance)
//            setEntity.getSlot(100).set(ModItems.BREAD_BOOTS.get().defaultInstance)
//            setEntity.getSlot(98).set(ModItems.TOOL_GUN.get().defaultInstance)
//            setEntity.getSlot(99).set(Items.TNT.defaultInstance)

        pGuiGraphics.blit(TEXTURE_ASSETS, leftPos + 14, topPos + 24, 0, 0, 42, 75)
        InventoryScreen.renderEntityInInventoryFollowsMouse(
            pGuiGraphics, leftPos + entityX, topPos + entityY, entityScale,
            (leftPos + entityX) - pMouseX.toFloat(),
            (topPos + entityY - 50) - pMouseY.toFloat(),
            finalEntity as LivingEntity
        )

        // really cursed
        poseStack.translate(0.0, 0.0, 130.0)
        pGuiGraphics.drawString(
            font,
            Component.translatable(finalEntity.name.copy().string)
                .withStyle(ChatFormatting.GOLD),
            leftPos + 13, topPos + 14,
            Color.WHITE.rgb
        )
        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)
    }

    override fun renderLabels(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        pGuiGraphics.drawString(font, title, 2, 2, Color.WHITE.rgb, false)
        pGuiGraphics.drawString(font, playerInventoryTitle, 2, 132, Color.WHITE.rgb, false)
        pGuiGraphics.drawString(font, modTranslatable("tool_gun", "creator", "save_load"), 167, 132, Color.WHITE.rgb, false)
    }

    override fun init() {
        super.init()
        addRenderableWidget(
            ScaleButton(leftPos + 100, topPos + 30, 10, 10, Component.literal("+"))
        )
        addRenderableWidget(
            ScaleButton(leftPos + 100, topPos + 40, 10, 10, Component.literal("-"))
        )
        addRenderableWidget(
            FunnyButton(leftPos + 200, topPos + 80, 20, 20,
                0, 0, 20, 20,
                modLocation("textures", "block", "fish.gif"))
        )
        addRenderableWidget(MobSelector(leftPos + 100, topPos + 100, 100, 15, Component.literal("mob")))
    }

    private fun getEntity(string: String): EntityType<*>? =
        ForgeRegistries.ENTITY_TYPES.getValue(EntityType.byString(string).getOrNull()?.let { EntityType.getKey(it) })

    inner class ScaleButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        private val pText: Component
    ): AbstractButton(pX, pY, pWidth, pHeight, pText) {
        override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

        override fun onPress() {
            if (pText.string == "+") {
                entityScale += 1
            } else entityScale -= 1
        }

        override fun isFocused(): Boolean = false
    }

    inner class FunnyButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pXTexStart: Int,
        pYTexStart: Int,
        pTextureWidth: Int,
        pTextureHeight: Int,
        pResourceLocation: ResourceLocation
    ): ImageButton(
        pX, pY,
        pWidth, pHeight,
        pXTexStart, pYTexStart,
        0,
        pResourceLocation, pTextureWidth, pTextureHeight,
        { setEntity = "spider" }
    )

    inner class MobSelector(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pMessage: Component
    ): EditBox(font, pX, pY, pWidth, pHeight, pMessage) {
        override fun setValue(pText: String) {
            super.setValue(pText)
        }
    }
}