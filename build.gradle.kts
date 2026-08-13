plugins {
    java
    `maven-publish`
}

group = "com.witchcraft"
version = "1.1.0"
description = "An immersive magical progression system for Minecraft"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly(files("OptionalPlugins/PlaceholderAPI-2.12.3.jar"))
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
    }

    jar {
        archiveFileName.set("Witchcraft-${project.version}.jar")
    }
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            from(components["java"])
        }
    }
}
