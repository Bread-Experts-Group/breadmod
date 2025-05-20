package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class GenericButton(x: Int, y: Int, width: Int, height: Int, message: Component, onPress: OnPress) :
	Button(x, y, width, height, message, onPress, { Component.empty() }) {
	constructor(x: Int, y: Int, width: Int, height: Int, message: String, onPress: OnPress) : this(
		x,
		y,
		width,
		height,
		Component.literal(message),
		onPress
	)
}