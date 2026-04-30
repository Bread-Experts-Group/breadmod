package org.bread_experts_group;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

final class LimitedClassLoader extends ClassLoader {
	private final Path[] locations;
	private final ClassLoader classLoader;

	LimitedClassLoader(Path[] locations, ClassLoader parent) {
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
			Path location = this.locations[locationIndex++];
			if (Files.isDirectory(location)) {
				Path classPath = location.resolve(asPath);
				if (Files.exists(classPath)) {
					try {
						byte[] data = Files.readAllBytes(classPath);
						return this.defineClass(name, data, 0, data.length);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			} else {
				try (InputStream is = Files.newInputStream(location)) {
					JarInputStream jar = new JarInputStream(is);
					while (true) {
						JarEntry entry = jar.getNextJarEntry();
						if (entry == null) break;
						if (entry.getName().equals(asPath)) {
							byte[] data = jar.readNBytes((int) entry.getSize());
							return this.defineClass(name, data, 0, data.length);
						}
					}
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		}
		return this.classLoader.loadClass(name);
	}
}
