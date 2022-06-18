plugins {
    java
    id("org.quiltmc.loom") version "0.12.+"
    id("io.github.juuxel.loom-quiltflower") version "1.7.+"
}

val minecraftVersion = "1.18.2"
val mappings = "24"
val loaderVersion = "0.17.1-beta.1"
val qslCoreVersion = "1.1.0-beta.17+1.18.2"

val modVersion = "3.0.0"
val modGroup = "gay.pyrrha"
val modName = "QForward"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

base { archivesName.set(modName) }
version = "$modVersion+mc$minecraftVersion"
group = modGroup


loom {
    runtimeOnlyLog4j.set(true)
    accessWidenerPath.set(project.file("src/main/resources/qforward.accesswidener"))
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        addLayer(quiltMappings.mappings("org.quiltmc:quilt-mappings:${minecraftVersion}+build.${mappings}:v2"))
    })
    modImplementation("org.quiltmc:quilt-loader:$loaderVersion")
    modImplementation("org.quiltmc.qsl:core:$qslCoreVersion")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("quilt.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName}"}
    }
}
