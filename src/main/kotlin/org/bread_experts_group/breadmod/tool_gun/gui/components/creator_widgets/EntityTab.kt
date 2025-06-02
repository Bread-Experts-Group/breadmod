package org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.tool_gun.gui.screen.CreatorScreen
import org.bread_experts_group.breadmod.util.putEntity

class EntityTab(screen: CreatorScreen) : ContainerWidget<CreatorScreen, EntityTab>(
	screen.leftPos,
	screen.topPos + 12,
	256,
	244,
	"entity_tab",
	screen
) {
	init {
		this.init()
	}

	override fun initContainer() {
		this.addChild(
			"test_button",
			GenericButton(this.x, this.y + 150, 40, 12, "TEST") {
				val entity = this.screen.currentEntity as LivingEntity
				entity.health = 100f
				entity.attributes.getInstance(Attributes.MAX_HEALTH)?.let { it.baseValue = 100.0 }
				this.screen.data.setValueDirect { it.putEntity("entity", entity) }
			}
		)
	}
}