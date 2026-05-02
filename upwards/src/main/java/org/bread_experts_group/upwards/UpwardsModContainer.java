package org.bread_experts_group.upwards;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

final class UpwardsModContainer extends ModContainer {
	private final IEventBus modBus;
	private final Object[] constructorParameters;
	private final Constructor<?> modConstructor;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	ClassLoader classLoader;

	UpwardsModContainer(
			IModInfo info, IEventBus modBus, Object[] constructorParameters, Constructor<?> modConstructor
	) {
		super(info);
		this.modBus = modBus;
		this.constructorParameters = constructorParameters;
		this.modConstructor = modConstructor;
	}

	@Override
	public @Nullable IEventBus getEventBus() {
		return this.modBus;
	}

	@Override
	protected void constructMod() {
		try {
			Object mod = this.modConstructor.newInstance(this.constructorParameters);
			this.classLoader = mod.getClass().getClassLoader();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
