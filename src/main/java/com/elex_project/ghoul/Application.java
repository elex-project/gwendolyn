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
		System.out.println("Gradle wrapper version updater\n");

		try {
			final Path dir = getWorkingDir(args);
			System.out.println("Current working directory: " + dir.toString());
			final String ver = getVersion(args);
			System.out.println("Latest version found: " + ver);

			doUpdate(dir, ver);

		} catch (IOException e) {
			log.error("Oops! something's wrong.", e);
			System.err.println("Oops! something's wrong.");
		}

		System.out.println("\nprogrammed by Elex, with love. Thanks.");
		System.exit(0);
	}

	/**
	 * Recursively find all gradle-wrapper.properties file, then replace version info.
	 *
	 * @param dir
	 * @param version
	 * @throws IOException
	 */
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

	/**
	 * Replace version info in the gradle-wrapper.properties file.
	 *
	 * @param file
	 * @param version
	 * @throws IOException
	 */
	@VisibleForTesting
	static void updateFile(final @NotNull Path file, final String version) throws IOException {
		System.out.print("Working with " + file.toString());

		final StringJoiner stringJoiner = new StringJoiner("\n");
		Files.readAllLines(file)
				.forEach(line -> {
					if (line.startsWith("distributionUrl")) {
						stringJoiner.add("distributionUrl=https\\://services.gradle.org/distributions/gradle-"
								+ version + "-all.zip");
					} else {
						stringJoiner.add(line);
					}
				});

		Files.writeString(file, stringJoiner.toString());
		System.out.println("... Ok.");
	}

	/**
	 * Get a working directory from args or curruen working dir.
	 *
	 * @param args
	 * @return working dir
	 */
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

	/**
	 * Get latest gradle version from args or from gradle website
	 *
	 * @param args
	 * @return latest gradle version string
	 * @throws IOException
	 */
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
				System.err.println("Unable to get a latest Gradle version from the web site!");
				throw new RuntimeException();
			}
		}
	}
}
