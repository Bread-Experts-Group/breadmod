package org.bread_experts_group;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.IModLanguageLoader;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

public final class UpwardsLanguageLoader implements IModLanguageLoader {
	private final Logger logger = LogManager.getLogger("BM Upwards Loader");

	@Override
	public String name() {
		return "bm_upwards";
	}

	@Override
	public String version() {
		return "a";
	}

	@Override
	public ModContainer loadMod(IModInfo info, ModFileScanData modFileScanResults, ModuleLayer layer) {
		IModFile modFile = info.getOwningFile().getFile();
		ModFileScanData.AnnotationData annotation = modFileScanResults.getAnnotatedBy(
				UpwardsMod.class, ElementType.TYPE
		).filter(
				upwardsMod -> upwardsMod.annotationData().get("modID").equals(info.getModId())
		).findFirst().orElseThrow(
				() -> new ModLoadingException(
						new ModLoadingIssue(
								ModLoadingIssue.Severity.ERROR,
								"something.here.todo.miko1",
								Collections.emptyList(),
								null, null, modFile, info
						)
				)
		);
		Map<String, Object> annotationData = annotation.annotationData();
		ModContainer container;
		try {
			ArrayList<Path> localClassPath = new ArrayList<>();
			Path modPath = modFile.getFilePath();
			if (modPath.endsWith("java/main")) {
				localClassPath.add(modPath.resolve("../../kotlin/main"));
			}
			localClassPath.add(modPath);
			Path path = modFile.findResource((String) annotationData.get("dependencyLocation"));
			try (Stream<Path> fileList = Files.list(path)) {
				fileList.forEach(localClassPath::add);
			}
			Path[] urls = localClassPath.toArray(new Path[0]);
			ClassLoader classLoader = new LimitedClassLoader(urls, this.getClass().getClassLoader());

			Class<?> modClass = classLoader.loadClass(annotation.memberName());
			Constructor<?>[] constructors = modClass.getConstructors();
			if (constructors.length != 1) throw new ModLoadingException(
					new ModLoadingIssue(
							ModLoadingIssue.Severity.ERROR,
							"something.here.todo.miko2",
							Collections.emptyList(),
							null, null, modFile, info
					)
			);
			Constructor<?> constructor = constructors[0];
			HashSet<Class<?>> availableClasses = new HashSet<>(3);
			availableClasses.add(IEventBus.class);
			availableClasses.add(ModContainer.class);
			availableClasses.add(Dist.class);
			Object[] parameters = new Object[constructor.getParameterCount()];
			Parameter[] constructorParameters = constructor.getParameters();

			IEventBus eventBus = BusBuilder.builder()
					.markerType(IModBusEvent.class)
					.allowPerPhasePost()
					.build();
			container = new UpwardsModContainer(info, eventBus, parameters, constructor);

			int i = 0;
			while (i < parameters.length) {
				Parameter parameter = constructorParameters[i];
				if (!availableClasses.remove(parameter.getType())) throw new ModLoadingException(
						new ModLoadingIssue(
								ModLoadingIssue.Severity.ERROR,
								"something.here.todo.miko3",
								Collections.emptyList(),
								null, null, modFile, info
						)
				);
				else {
					Class<?> type = parameter.getType();
					if (type == ModContainer.class) {
						parameters[i] = container;
					} else if (type == IEventBus.class) {
						parameters[i] = eventBus;
					} else if (type == Dist.class) {
						parameters[i] = FMLEnvironment.dist;
					}
				}
				i++;
			}
			return container;
		} catch (Exception e) {
			throw new ModLoadingException(
					new ModLoadingIssue(
							ModLoadingIssue.Severity.ERROR,
							"something.here.todo.miko4",
							Collections.emptyList(),
							e, null, modFile, info
					)
			);
		}
	}
}