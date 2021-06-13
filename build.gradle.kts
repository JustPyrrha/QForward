plugins {
    id("fabric-loom") version "0.7-SNAPSHOT"
}

val minecraftVersion = "1.17"
val yarnBuild = "10"
val loaderVersion = "0.11.3"

val modVersion = "2.2.0"
val modGroup = "dev.joezwet"
val modName = "FabricForwarding"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
    withSourcesJar()
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

tasks.jar {
    from("LICENSE")
}
