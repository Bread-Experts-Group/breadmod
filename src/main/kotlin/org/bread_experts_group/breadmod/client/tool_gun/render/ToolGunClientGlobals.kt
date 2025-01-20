package org.bread_experts_group.breadmod.client.tool_gun.render

import net.minecraft.client.resources.model.BakedModel
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.api.IToolGunModeClient
import org.bread_experts_group.breadmod.client.render.modelLocation
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import java.math.BigDecimal
import java.security.SecureRandom

internal object ToolGunClientGlobals {
	// Client-Sided Modes
	var currentMode: IToolGunModeClient? = null
	val toolGunModesClient: MutableMap<ResourceLocation, IToolGunModeClient> = mutableMapOf()

	// Miscellaneous
	internal var helper: ToolGunRenderHelper = ToolGunRenderHelper()
	internal val caseOhInstrument: SecureRandom = SecureRandom()
	internal var caseOhSize: BigDecimal = BigDecimal.TWO

	// Recoil and Coil Spin vars
	internal var coilRotation: Float = 0f
	internal var coilDelta: Float = 0f
	internal var recoil: Float = 0f

	// Models
	@Suppress("unused")
	internal val altModel: BakedModel =
		this.helper.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/alt/tool_gun_alt"))
	internal val mainModel: BakedModel =
		this.helper.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/item"))
	internal val coilModel: BakedModel =
		this.helper.modelManager.getModel(modelLocation("item/$TOOL_GUN_DEF/coil"))
	// Operations
	/**
	 * Sets the delta and recoil to their triggered values.
	 */
	fun triggerDelta() {
		this.coilDelta = 1f
		this.recoil = 0.1f
	}
}