package org.bread_experts_group.breadmod.client.screen.tool_gun.widgets

import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class GenericButton(x: Int, y: Int, width: Int, height: Int, message: String, onPress: OnPress) :
	Button(x, y, width, height, Component.literal(message), onPress, { Component.empty() })