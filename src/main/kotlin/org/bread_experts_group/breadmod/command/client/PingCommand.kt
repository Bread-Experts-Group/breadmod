package org.bread_experts_group.breadmod.command.client

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

internal object PingCommand {
	fun register(): ArgumentBuilder<CommandSourceStack, *> = Commands.literal("ping")
		.requires { sourceStack -> sourceStack.hasPermission(1) }
		.then(
			Commands.argument("Server Address", StringArgumentType.string())
				.executes { ctx ->
					Arena.ofConfined().use { arena ->
						// Allocate off-heap memory and
						// copy the argument, a Java string, into off-heap memory
//						val nativeString = arena.allocateUtf8String("test string")
						// Link and call the C function strlen
						// Obtain an instance of the native linker
						val linker = Linker.nativeLinker()
						// Locate the address of the C function signature
						val stdLib: SymbolLookup = linker.defaultLookup()
						val strlenAddr: MemorySegment = stdLib.find("strlen").get()
						// Create a description of the C function
						val strlenSig: FunctionDescriptor =
							FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
						// Create a downcall handle for the C function
						val strlen: MethodHandle = linker.downcallHandle(strlenAddr, strlenSig)
						// Call the C function directly from Java
//						ctx.source.sendSystemMessage(Component.literal((strlen.invokeExact(nativeString) as Long).toString()))
					}
					ctx.source.sendSystemMessage(Component.literal("NTV"))
					Command.SINGLE_SUCCESS
				}
		)
}