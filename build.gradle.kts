plugins {
    java
    id("org.quiltmc.loom") version "0.12.+"
    id("io.github.juuxel.loom-quiltflower") version "1.7.+"
}

val modVersion = "3.1.0"
val modGroup = "gay.pyrrha"
val modName = "QForward"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

base { archivesName.set(modName) }
version = "$modVersion+mc${libs.versions.minecraft.get()}"
group = modGroup

loom {
    runtimeOnlyLog4j.set(true)
    accessWidenerPath.set(project.file("src/main/resources/qforward.accesswidener"))
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        addLayer(quiltMappings.mappings("org.quiltmc:quilt-mappings:${libs.versions.quilt.mappings.get()}:v2"))
    })
    modImplementation(libs.quilt.loader)
    modImplementation(libs.quilt.qsl.core.base)
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
