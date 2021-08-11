/*
 * Project Ghoul
 *
 * Copyright (c) 2021. Elex.
 * https://www.elex-project.com/
 */
buildscript {
	repositories {
		maven {
			url = uri("https://repository.elex-project.com/repository/maven")
		}
	}

	dependencies {
		classpath ("com.jaredsburrows:gradle-license-plugin:0.8.90")
	}
}

plugins {
	java
	application
	idea
	id("com.jaredsburrows.license") version "0.8.90"
}

group = "com.elex-project"
version = "1.0.3"
description = "Gradle Wrapper Version Updater"

repositories {
	maven {
		url = uri("https://repository.elex-project.com/repository/maven")
	}
}

application{
	mainClass.set("com.elex_project.ghoul.Application")
	mainModule.set("com.elex_project.ghoul")
}

java {
	withSourcesJar()
	withJavadocJar()
	sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
	targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
}

configurations {
	compileOnly {
		extendsFrom(annotationProcessor.get())
	}
	testCompileOnly {
		extendsFrom(testAnnotationProcessor.get())
	}
}

tasks.jar {
	manifest {
		attributes(mapOf(
				"Implementation-Title" to project.name,
				"Implementation-Version" to project.version,
				"Implementation-Vendor" to "ELEX co.,pte.",
				"Main-Class" to application.mainClass
		))
	}
}

tasks.register<Jar>("fatJar"){
	group = "distribution"
	description = "Build a Fat-Jar archive."
	manifest {
		attributes(mapOf(
			"Implementation-Title" to project.name,
			"Implementation-Version" to project.version,
			"Implementation-Vendor" to "ELEX co.,pte.",
			"Main-Class" to application.mainClass,
			"Automatic-Module-Name" to "com.elex_project.${project.name}"
		))
	}
	archiveBaseName.set(project.name+"-fat")
	from(configurations.runtimeClasspath.get().map {
		if (it.isDirectory) it else zipTree(it)
	})
	destinationDirectory.set(projectDir.resolve("distributions"))
	with(tasks.jar.get() as CopySpec)
	exclude("**/module-info.class", "**/LICENSE", "**/NOTICE")

}

tasks.compileJava {
	options.encoding = "UTF-8"
}

tasks.compileTestJava {
	options.encoding = "UTF-8"
}

tasks.licenseReport {
	generateCsvReport = false
	generateHtmlReport = true
	generateJsonReport = true
}

tasks.test {
	useJUnitPlatform()
}

tasks.installDist{
	into("${System.getProperty("user.home")}/scripts/${project.name}")
}

tasks.javadoc {
	if (JavaVersion.current().isJava9Compatible) {
		(options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
	}
	(options as StandardJavadocDocletOptions).encoding = "UTF-8"
	(options as StandardJavadocDocletOptions).charSet = "UTF-8"
	(options as StandardJavadocDocletOptions).docEncoding = "UTF-8"

}

dependencies {
	implementation("org.slf4j:slf4j-api:1.7.32")
	implementation("org.jetbrains:annotations:21.0.1")

	implementation("org.jsoup:jsoup:1.14.1")

	compileOnly("org.projectlombok:lombok:1.18.20")
	annotationProcessor("org.projectlombok:lombok:1.18.20")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.20")

	implementation("ch.qos.logback:logback-classic:1.2.5")
	testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}
