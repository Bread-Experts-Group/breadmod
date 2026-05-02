package org.bread_experts_group.upwards;

import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.IModLanguageLoader;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.locating.IModFile;

import java.lang.annotation.ElementType;
import java.util.Collections;

public final class UpwardsLanguageLoader implements IModLanguageLoader {
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
		IEventBus eventBus = BusBuilder.builder()
				.markerType(IModBusEvent.class)
				.allowPerPhasePost()
				.build();
		return new UpwardsModContainer(info, eventBus, annotation);
	}
}