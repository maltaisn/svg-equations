plugins {
    kotlin("jvm")
}

dependencies {
    val jcommanderVersion: String by project

    implementation(kotlin("stdlib"))

    implementation("com.beust:jcommander:$jcommanderVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

sourceSets {
    java {
        named("test") {
            resources.srcDir("src/test/res")
        }
    }
}

kotlin {
    sourceSets.all {
        languageSettings.apply {
            enableLanguageFeature("InlineClasses")
            useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
}

// Use this task to create a fat jar.
val dist = tasks.register<Jar>("dist") {
    from(files(sourceSets.main.get().output.classesDirs))
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    archiveBaseName.set("svgeq")

    manifest {
        attributes["Main-Class"] = "com.maltaisn.svgequations.MainKt"
    }
    finalizedBy(tasks.named("shrinkJar"))
}

tasks.register<proguard.gradle.ProGuardTask>("shrinkJar") {
    val distFile = dist.get().archiveFile.get().asFile
    configuration("proguard-rules.pro")
    injars(distFile)
    outjars(distFile.resolveSibling("svgeq-release.jar"))
    libraryjars(configurations.runtimeClasspath.get().files)
}
