import com.github.dockerjava.client.DockerException
import com.teamsamst.gitflow.GitflowPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import se.transmode.gradle.plugins.docker.DockerPluginExtension
import se.transmode.gradle.plugins.docker.DockerTask

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn how to create Gradle builds at https://guides.gradle.org/creating-new-gradle-builds/
 */

buildscript {
	repositories {
		mavenCentral()
		maven {
			setUrl("https://artifactory.valhalla-game.com/libs-release")
		}
		maven {
			setUrl("https://artifactory.valhalla-game.com/libs-snapshot")
		}
		mavenLocal()
	}
	dependencies {
		val springBootVersion = "2.0.3.RELEASE"
		val kotlinVersion = "1.2.70"
		classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
		classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
		classpath("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
        classpath("se.transmode.gradle:gradle-docker:1.2")
		classpath("com.teamsamst:gitflow:1.7")
	}
}

ext["spring_boot_version"] = "2.0.3.RELEASE"

apply {
	plugin("docker")
	plugin("com.teamsamst.gitflow")
}

plugins {
	val kotlinVersion = "1.2.70"
	application
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	eclipse
	id("org.springframework.boot") version "2.0.3.RELEASE"
	id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

group = "com.valhallagame.valhalla"
version = "1.7-SNAPSHOT"

setProperty("sourceCompatibility", JavaVersion.VERSION_1_8)
setProperty("mainClassName", "com.valhallagame.valhalla.currencyserviceserver.AppKt")

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
compileKotlin.kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"
compileTestKotlin.kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")

repositories {
	mavenCentral()
	maven {
		setUrl("https://artifactory.valhalla-game.com/libs-release")
	}
    maven {
        setUrl("https://artifactory.valhalla-game.com/libs-snapshot")
    }
	mavenLocal()
}

dependencies {
	compile("org.springframework.boot:spring-boot-starter-data-jpa")
	compile("org.springframework.boot:spring-boot-starter-web")
	compile("com.fasterxml.jackson.module:jackson-module-kotlin")
	compile("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
	compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	compile("org.flywaydb:flyway-core")
	compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	compile("org.jetbrains.kotlin:kotlin-reflect")
    compile("com.valhallagame.valhalla:common:1.2")
	compile("com.valhallagame.valhalla:currency-service-client:1.4")

	runtime("org.springframework.boot:spring-boot-devtools")
	runtime("org.postgresql:postgresql")

	testCompile("org.springframework.boot:spring-boot-starter-test")
	testCompile("com.h2database:h2:1.4.197")
}

val copyJar = tasks.create<Exec>("copyJar") {
	executable = "sh"
	setArgs(listOf("-c", "mkdir -p build/docker && cp build/libs/${application.applicationName}-$version.jar build/docker/${application.applicationName}-$version.jar"))
}

tasks.create<DockerTask>("buildDocker") {
	dependsOn(copyJar)
	baseImage = "frolvlad/alpine-oraclejdk8:slim"
	registry = "https://registry.valhalla-game.com"
	if(project.hasProperty("tagVersion")) {
		tagVersion = project.properties["tagVersion"] as String
	}
	push = true
    tag = "registry.valhalla-game.com/saiaku/${application.applicationName}"
    entryPoint(listOf("java", "-Xmx256m", "-Xss512k", "-Xms32m", "-jar", "/${application.applicationName}-$version.jar"))
	addFile("${application.applicationName}-$version.jar", "/")
}
