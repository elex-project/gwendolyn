/*
 * Project Ghoul
 *
 * Copyright (c) 2021. Elex.
 * https://www.elex-project.com/
 */

package com.elex_project.ghoul;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;
import java.util.regex.Pattern;

@Slf4j
public class Application {

	public static void main(String... args) {
		try {
			final Path dir = getWorkingDir(args);
			System.out.println("Working in " + dir.toString());
			final String ver = getVersion(args);
			System.out.println("Recent Version found: " + ver);

			doUpdate(dir, ver);

		} catch (IOException e) {
			log.error("Oops!", e);
			System.exit(0);
		}

	}

	@VisibleForTesting
	static void doUpdate(final Path dir, final String version) throws IOException {
		Files.find(dir, Integer.MAX_VALUE,
						(path, attr) -> attr.isRegularFile() && path.endsWith("gradle-wrapper.properties"))
				.forEach(path -> {
					try {
						updateFile(path, version);
					} catch (IOException e) {
						log.error("Unable to write to {}.", path, e);
					}
				});
	}

	@VisibleForTesting
	static void updateFile(final Path file, final String version) throws IOException {
		System.out.println("Working with " + file.toString());
		final StringJoiner stringJoiner = new StringJoiner("\n");
		Files.readAllLines(file)
				.forEach(line -> {
					if (line.startsWith("distributionUrl")) {
						stringJoiner.add("distributionUrl=https\\://services.gradle.org/distributions/gradle-" + version + "-all.zip");
					} else {
						stringJoiner.add(line);
					}
				});
		Files.writeString(file, stringJoiner.toString());
	}

	@VisibleForTesting
	static @NotNull Path getWorkingDir(final String... args) {
		try {
			if (null != args && args.length > 0) {
				final Path path = Paths.get(args[0]);
				if (Files.exists(path) &&
						Files.isDirectory(path)) {
					return path;
				}
			}
		} catch (InvalidPathException ignore) {

		}
		return Paths.get("");
	}

	@VisibleForTesting
	static String getVersion(final String... args) throws IOException {
		if (null != args && args.length > 1) {
			return args[1];
		} else {
			Document document = Jsoup.connect("https://gradle.org/releases/").get();
			Elements elements =
					document.getElementsMatchingText(Pattern.compile("^v\\d+.\\d+(.\\d+)?$"));
			log.debug("Found Regex matching: {}", elements.size());

			if (elements.size() > 0) {
				return elements.get(0).text().substring(1);
			} else {
				throw new RuntimeException();
			}
		}
	}
}
