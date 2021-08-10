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
	`maven-publish`
	id("com.github.ben-manes.versions") version "0.39.0"
	id("com.jaredsburrows.license") version "0.8.90"
}

group = "com.elex-project"
version = "1.0-SNAPSHOT"
description = ""//todo

repositories {
	maven {
		url = uri("https://repository.elex-project.com/repository/maven")
	}
}
application{
	mainClass.set("com.elex_project.sample.Application")
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
				"Main-Class" to application.mainClass,
				"Automatic-Module-Name" to "com.elex_project.${project.name}"
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

tasks.test {
	useJUnitPlatform()
}

tasks.javadoc {
	if (JavaVersion.current().isJava9Compatible) {
		(options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
	}
	(options as StandardJavadocDocletOptions).encoding = "UTF-8"
	(options as StandardJavadocDocletOptions).charSet = "UTF-8"
	(options as StandardJavadocDocletOptions).docEncoding = "UTF-8"

}

tasks.licenseReport {
	generateCsvReport = false
	generateHtmlReport = false
	generateJsonReport = true
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			pom {
				// todo
				name.set(project.name)
				description.set(project.description)
				url.set("https://")
				inceptionYear.set("2021")
				properties.set(mapOf(
						"prop.with.dots" to "anotherValue"
				))
				organization {
					name.set("Elex co.,Pte.")
					url.set("https://www.elex-project.com/")
				}
				licenses {
					license {
						// todo
						name.set("licenseName")
						url.set("licenseUrl")
						comments.set("")
					}
				}
				developers {
					developer {
						id.set("elex")
						name.set("Elex")
						url.set("https://www.elex.pe.kr/")
						email.set("developer@elex-project.com")
						organization.set("Elex Co.,Pte.")
						organizationUrl.set("https://www.elex-project.com/")
						roles.set(arrayListOf("Developer", "CEO"))
						timezone.set("Asia/Seoul")
						properties.set(mapOf("" to ""))
					}
				}
				contributors {
					contributor {
						name.set("")
						email.set("")
						url.set("")
					}
				}
				scm {
					// todo
					connection.set("scm:git:https://github.com/my-library.git")
					developerConnection.set("scm:git:https://github.com/my-library.git")
					url.set("https://github.com/my-library/")
				}
			}
		}
	}

	repositories {
		maven {
			name = "mavenElex"
			val urlRelease = uri("https://repository.elex-project.com/repository/maven-releases")
			val urlSnapshot = uri("https://repository.elex-project.com/repository/maven-snapshots")
			url = if (version.toString().endsWith("SNAPSHOT")) urlSnapshot else urlRelease
			credentials {
				username = project.findProperty("repo.username") as String
				password = project.findProperty("repo.password") as String
			}
		}

	}
}

dependencies {
	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
	implementation("org.slf4j:slf4j-api:1.7.30")
	implementation("org.jetbrains:annotations:21.0.1")

	compileOnly("org.projectlombok:lombok:1.18.20")
	annotationProcessor("org.projectlombok:lombok:1.18.20")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.20")

	implementation("ch.qos.logback:logback-classic:1.2.3")
	testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}
