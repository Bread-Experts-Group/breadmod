package org.bread_experts_group.upwards;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Stream;

final class UpwardsModContainer extends ModContainer {
	private final IEventBus modBus;
	private final ModFileScanData.AnnotationData annotation;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	private ClassLoader classLoader;

	UpwardsModContainer(
			IModInfo info,
			IEventBus modBus,
			ModFileScanData.AnnotationData annotation
	) {
		super(info);
		this.modBus = modBus;
		this.annotation = annotation;
	}

	@Override
	public @Nullable IEventBus getEventBus() {
		return this.modBus;
	}

	private void addPath(ArrayList<Object> classPath, Path path) {
		if (Files.isDirectory(path)) {
			classPath.add(path);
			return;
		}
		try {
			Path temp = Files.createTempFile(null, "jar");
			Files.copy(path, temp, StandardCopyOption.REPLACE_EXISTING);
			classPath.add(new JarFile(temp.toFile()));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	protected void constructMod() {
		IModFile modFile = this.modInfo.getOwningFile().getFile();
		Map<String, Object> annotationData = this.annotation.annotationData();
		try {
			ArrayList<Object> localClassPath = new ArrayList<>();
			Path modPath = modFile.getFilePath();
			if (modPath.endsWith("java/main")) localClassPath.add(modPath.resolve("../../kotlin/main"));
			addPath(localClassPath, modPath);
			Path path = modFile.findResource((String) annotationData.get("dependencyLocation"));
			try (Stream<Path> fileList = Files.list(path)) {
				fileList.forEach(entry -> addPath(localClassPath, entry));
			}
			Object[] paths = localClassPath.toArray(new Object[0]);

			String piggyback = (String) annotationData.get("piggybackModID");
			ClassLoader parent;
			if (piggyback != null) {
				parent = ((UpwardsModContainer) ModList.get().getModContainerById(piggyback).orElseThrow()).classLoader;
			} else {
				parent = this.getClass().getClassLoader();
			}
			ClassLoader classLoader = new LimitedClassLoader(paths, parent);

			Class<?> modClass = classLoader.loadClass(this.annotation.memberName());
			Constructor<?>[] constructors = modClass.getConstructors();
			if (constructors.length != 1) throw new ModLoadingException(
					new ModLoadingIssue(
							ModLoadingIssue.Severity.ERROR,
							"something.here.todo.miko2",
							Collections.emptyList(),
							null, null, modFile, this.modInfo
					)
			);
			Constructor<?> constructor = constructors[0];
			HashSet<Class<?>> availableClasses = new HashSet<>(3);
			availableClasses.add(IEventBus.class);
			availableClasses.add(ModContainer.class);
			availableClasses.add(Dist.class);
			Object[] parameters = new Object[constructor.getParameterCount()];
			Parameter[] constructorParameters = constructor.getParameters();

			int i = 0;
			while (i < parameters.length) {
				Parameter parameter = constructorParameters[i];
				if (!availableClasses.remove(parameter.getType())) throw new ModLoadingException(
						new ModLoadingIssue(
								ModLoadingIssue.Severity.ERROR,
								"something.here.todo.miko3",
								Collections.emptyList(),
								null, null, modFile, this.modInfo
						)
				);
				else {
					Class<?> type = parameter.getType();
					if (type == ModContainer.class) {
						parameters[i] = this;
					} else if (type == IEventBus.class) {
						parameters[i] = this.modBus;
					} else if (type == Dist.class) {
						parameters[i] = FMLEnvironment.dist;
					}
				}
				i++;
			}

			Object mod = constructor.newInstance(parameters);
			this.classLoader = mod.getClass().getClassLoader();
		} catch (Exception e) {
			throw new ModLoadingException(
					new ModLoadingIssue(
							ModLoadingIssue.Severity.ERROR,
							"something.here.todo.miko4",
							Collections.emptyList(),
							e, null, modFile, this.modInfo
					)
			);
		}
	}
}
