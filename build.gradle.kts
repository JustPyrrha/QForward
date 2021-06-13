plugins {
    id("fabric-loom") version "0.7-SNAPSHOT"
    `maven-publish`
}

val minecraftVersion = "1.16.2"
val yarnBuild = "21"
val loaderVersion = "0.9.2+build.206"

val modVersion = "2.0.0"
val modGroup = "dev.joezwet"
val modName = "FabricForwarding"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base { archivesBaseName = modName }
version = "$modVersion+$minecraftVersion"
group = modGroup

minecraft {
    refmapName = "fabricforwarding.refmap.json"
    accessWidener = project.file("src/main/resources/fabricforwarding.accesswidener")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$minecraftVersion+build.$yarnBuild:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

//task sourcesJar(type: Jar, dependsOn: classes) {
//    classifier = "sources"
//    from sourceSets.main.allSource
//}

tasks.jar {
    from("LICENSE")
}
//
//publishing {
//    publications {
//        mavenJava(MavenPublication) {
//            artifact(remapJar) {
//                builtBy remapJar
//            }
//            artifact(sourcesJar) {
//                builtBy remapSourcesJar
//            }
//        }
//    }
//    repositories {
//        // mavenLocal()
//    }
//}
