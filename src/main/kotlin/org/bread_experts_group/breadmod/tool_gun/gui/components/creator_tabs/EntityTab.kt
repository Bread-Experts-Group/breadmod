package org.bread_experts_group.breadmod.tool_gun.gui.components.creator_tabs

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Cow
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.tool_gun.gui.screen.CreatorScreen
import org.bread_experts_group.breadmod.util.putEntity

class EntityTab(screen: CreatorScreen, private val level: Level) : ContainerWidget<CreatorScreen>(
	screen.leftPos,
	screen.topPos + 12,
	256,
	244,
	"entity_tab",
	screen
) {
	private var currentEntity: Entity = Cow(EntityType.COW, this.level)

	init {
		this.init()
	}

	override fun init() {
		this.addChild(
			"test_button",
			GenericButton(this.x, this.y + 150, 40, 12, "TEST") {
				this.screen.data.setValueDirect { it.putEntity("entity", this.currentEntity) }
			}
		)
	}
}