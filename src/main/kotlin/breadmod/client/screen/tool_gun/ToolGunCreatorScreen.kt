package breadmod.client.screen.tool_gun

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.mode.creator.*
import breadmod.menu.item.ToolGunCreatorMenu
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.mojang.blaze3d.systems.RenderSystem
import moze_intel.projecte.gameObjs.registries.PEItems
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
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.registries.ForgeRegistries
import java.awt.Color
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Path

@Suppress("Unused", "MemberVisibilityCanBePrivate")
class ToolGunCreatorScreen(
    pMenu: ToolGunCreatorMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<ToolGunCreatorMenu>(pMenu, pPlayerInventory, pTitle) {
    companion object {
        val TEXTURE = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode.png")
        val TEXTURE_ASSETS = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode_assets.png")
        val instance: Minecraft = Minecraft.getInstance()
    }

    private val entityX = 35
    private val entityY = 94
    private var entityScale = 32

    var customEntityName: String? = null
    var entityString: String = "zombie"
    var entityType: EntityType<*>? = getEntityFromString(entityString)

    var entityHealth: Double? = 20.0
    var entitySpeed: Double? = 5.0

    // First Int: Duration, Second Int: Amplifier
    var entityEffect: MutableList<Triple<MobEffect, Int, Int>>? = mutableListOf(
        Triple(MobEffects.HARM, 1000, 10),
        Triple(MobEffects.JUMP, 1000, 10)
    )

    var helmetSlot: ItemStack? = Items.DIAMOND_HELMET.defaultInstance
    var chestplateSlot: ItemStack? = Items.DIAMOND_CHESTPLATE.defaultInstance
    var leggingsSlot: ItemStack? = Items.DIAMOND_LEGGINGS.defaultInstance
    var bootsSlot: ItemStack? = Items.DIAMOND_BOOTS.defaultInstance

    var mainHandSlot: ItemStack? = PEItems.RED_MATTER_AXE.get().defaultInstance
    var offHandSlot: ItemStack? = null

    val json = JsonObject().also {
        it.addProperty("entity", ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString())
        if(customEntityName != null) { it.addProperty("custom_entity_name", customEntityName) }
        if(entityHealth != null) { it.addProperty("entity_health", entityHealth) }
        if(entitySpeed != null) { it.addProperty("entity_speed", entitySpeed) }
        if(entityEffect != null) {
            it.add("effects", JsonObject().also { effectObject ->
                entityEffect?.forEach { (effect, duration, amplifier) ->
                    effectObject.add(effectToString(effect), JsonObject().also { currentEffect ->
                        currentEffect.addProperty("duration", duration)
                        currentEffect.addProperty("amplifier", amplifier)
                    })
                }
            })
        }
        if(helmetSlot != null) {
            it.addProperty("helmet", helmetSlot?.item?.let { helmet -> itemToString(helmet) })
        }
        if(chestplateSlot != null) {
            it.addProperty("chestplate", chestplateSlot?.item?.let { chestplate -> itemToString(chestplate) })
        }
        if(leggingsSlot != null) {
            it.addProperty("leggings", leggingsSlot?.item?.let { leggings -> itemToString(leggings) })
        }
        if(bootsSlot != null) {
            it.addProperty("boots", bootsSlot?.item?.let { boots -> itemToString(boots) })
        }
        if(mainHandSlot != null) {
            it.addProperty("main_hand", mainHandSlot?.item?.let { mainHand -> itemToString(mainHand) })
        }
        if(offHandSlot != null) {
            it.addProperty("off_hand", offHandSlot?.item?.let { offHand -> itemToString(offHand) })
        }
    }

    val jsonByteArray = json.toString().toByteArray()
    val gson = Gson()
    val filePath: Path = FMLPaths.GAMEDIR.get().resolve("breadmod/tool_gun/mob.json").toAbsolutePath()

    val reader = JsonReader(FileReader(filePath.toFile()))
    val data: JsonObject = gson.fromJson(reader, JsonObject::class.java)

    init {
        imageWidth = 256
        imageHeight = 220
//        println(json)

        if(!Files.exists(filePath)) Files.write(filePath, jsonByteArray)

//        println(GsonHelper.parse(String(jsonByteArray, StandardCharsets.UTF_8)).get("effects"))

        data.getAsJsonObject("effects").asMap().forEach { (key, value) ->
            println("key: $key")
            value.asJsonObject.asMap().forEach { (effectKey, valueInt) ->
                println("key: $effectKey, valueInt: $valueInt")
            }
        }

        // intentionally returns null to test non-existing entries in the object
        if(data.getAsJsonObject("stinky") != null) {
            println(data.getAsJsonObject("stinky"))
        } else println("this is null!")
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        renderBackground(pGuiGraphics)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    private var alpha = 1.0f
    // todo set this up for a fading static texture
    private fun alphaTick() = if (alpha > 0f) alpha -= 0.01f else alpha = 1f

    // render >> renderBg
    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        val level = instance.level ?: return
        val poseStack = pGuiGraphics.pose()
        val finalEntity = entityType?.create(level) as LivingEntity

        // Setup gui rendering
        RenderSystem.setShader { GameRenderer.getRendertypeTranslucentShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)
        RenderSystem.setShaderTexture(0, TEXTURE)

        // Update the displayed entity
        entityType = getEntityFromString(entityString)

        // Health
        entityHealth?.let { health ->
            finalEntity.getAttribute(Attributes.MAX_HEALTH)?.baseValue = health
            finalEntity.health = health.toFloat()
        }

        // Speed
        entitySpeed?.let {
            finalEntity.getAttribute(Attributes.MOVEMENT_SPEED)?.baseValue = it
            finalEntity.speed = it.toFloat()
        }

        // Armor Slots
        helmetSlot?.let { finalEntity.getSlot(HELMET_SLOT).set(it) }
        chestplateSlot?.let { finalEntity.getSlot(CHESTPLATE_SLOT).set(it) }
        leggingsSlot?.let { finalEntity.getSlot(LEGGINGS_SLOT).set(it) }
        bootsSlot?.let { finalEntity.getSlot(BOOTS_SLOT).set(it) }

        // Item Slots
        mainHandSlot?.let { finalEntity.getSlot(MAINHAND_SLOT).set(it) }
        offHandSlot?.let { finalEntity.getSlot(OFFHAND_SLOT).set(it) }

        // Potion Effects
        entityEffect?.let { it.forEach { (effect, duration, amplifier) ->
            finalEntity.addEffect(MobEffectInstance(effect, duration, amplifier))
        }}

        customEntityName?.let { finalEntity.customName = Component.literal(it) }

        // Actually rendering the gui elements
        pGuiGraphics.blit(TEXTURE_ASSETS, leftPos + 14, topPos + 24, 0, 0, 42, 75)
        InventoryScreen.renderEntityInInventoryFollowsMouse(
            pGuiGraphics, leftPos + entityX, topPos + entityY, entityScale,
            (leftPos + entityX) - pMouseX.toFloat(),
            (topPos + entityY - 50) - pMouseY.toFloat(),
            finalEntity
        )
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

    inner class ScaleButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        private val pText: Component
    ): AbstractButton(pX, pY, pWidth, pHeight, pText) {
        override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

        override fun onPress() {
            when(pText.string) {
                "+" -> entityScale += 1
                "++" -> entityScale += 10
                "+++" -> entityScale += 100
                "-" -> entityScale -= 1
                "--" -> entityScale -= 10
                "---" -> entityScale -= 100
            }
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
        { entityString = "skeleton" }
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