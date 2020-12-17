plugins {
    kotlin("jvm")
    id("com.github.breadmoirai.github-release")
}

dependencies {
    val jcommanderVersion: String by project

    implementation(kotlin("stdlib"))

    implementation("com.beust:jcommander:$jcommanderVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

sourceSets {
    main {
        resources.srcDir("src/main/res")
    }
    test {
        resources.srcDir("src/test/res")
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
    dependsOn("updateVersionRes")

    from(files(sourceSets.main.get().output.classesDirs))
    from(files(sourceSets.main.get().resources.srcDirs))
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

tasks.register("updateVersionRes") {
    doLast {
        val appVersion: String by project
        val versionResFile = file("src/main/res/version.txt")
        versionResFile.parentFile.mkdirs()
        versionResFile.writeText(appVersion)
    }
}

// Publish a new release to Github, using the lastest defined libVersion property,
// a git tag, and the release notes in CHANGELOG.md.
githubRelease {
    val appVersion: String by project

    if (project.hasProperty("githubReleasePluginToken")) {
        val githubReleasePluginToken: String by project
        token(githubReleasePluginToken)
    }
    owner("maltaisn")
    repo("svg-equations")

    tagName("v$appVersion")
    targetCommitish("master")
    releaseName("v$appVersion")

    body {
        // Get release notes for version from changelog file.
        val changelog = file("../CHANGELOG.md")
        val versionChanges = StringBuilder()
        var foundVersion = false
        for (line in changelog.readLines()) {
            if (foundVersion && line.matches("""^#+\s*v.+$""".toRegex())) {
                break
            } else if (line.matches("""^#+\s*v$appVersion$""".toRegex())) {
                foundVersion = true
            } else if (foundVersion) {
                versionChanges.append(line)
                versionChanges.append('\n')
            }
        }
        if (!foundVersion) {
            throw GradleException("No release notes for version $appVersion")
        }
        versionChanges.toString().trim()
    }

    releaseAssets("$buildDir/libs/svgeq-release.jar")

    overwrite(true)
}
