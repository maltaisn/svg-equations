buildscript {
    val kotlinVersion: String by project
    val proguardLocation: String by project
    repositories {
        jcenter()
        flatDir("dirs" to proguardLocation)
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath(":proguard:")
    }
}

plugins {
    base
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

tasks.clean {
    delete(buildDir)
}
