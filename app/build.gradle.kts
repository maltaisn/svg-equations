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
            resources.srcDir("test/res")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
}
