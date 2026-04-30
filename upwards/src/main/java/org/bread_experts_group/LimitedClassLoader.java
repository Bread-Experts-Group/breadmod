package org.bread_experts_group;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class LimitedClassLoader extends ClassLoader {
	private final Object[] locations;
	private final ClassLoader classLoader;

	LimitedClassLoader(Object[] locations, ClassLoader parent) {
		this.locations = locations.clone();
		this.classLoader = parent;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> loaded = this.findLoadedClass(name);
		if (loaded != null) return loaded;
		int locationIndex = 0;
		String asPath = name.replace('.', '/') + ".class";
		while (locationIndex < this.locations.length) {
			Object location = this.locations[locationIndex++];
			try {
				if (location instanceof JarFile) {
					ZipEntry entry = ((ZipFile) location).getEntry(asPath);
					if (entry == null) continue;
					byte[] data = ((ZipFile) location).getInputStream(entry).readAllBytes();
					return this.defineClass(name, data, 0, data.length);
				} else {
					Path classPath = ((Path) location).resolve(asPath);
					if (Files.exists(classPath)) {
						try {
							byte[] data = Files.readAllBytes(classPath);
							return this.defineClass(name, data, 0, data.length);
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					}
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		return this.classLoader.loadClass(name);
	}
}
