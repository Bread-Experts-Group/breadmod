package org.bread_experts_group.breadmod.api

import net.neoforged.api.distmarker.Dist
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
annotation class ToolGunMode(val side: Dist)