plugins {
    id("com.github.johnrengelman.shadow")
    java
    idea
    id("fabric-loom")
    `maven-publish`
    kotlin("jvm")
    kotlin("kapt")
}

val modVersion: String by project
val mavenGroup: String by project
val archivesBaseName: String by project

version = modVersion
group = mavenGroup

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceSets["main"].java {
        srcDir("${buildDir.absolutePath}/generated/source/kaptKotlin/")
    }
}

base {
    archivesBaseName = archivesBaseName
}

minecraft {
//    accessTransformer.set(file('src/main/resources/META-INF/accesstransformer.cfg'))
}

configurations.api {
    extendsFrom(configurations["shadow"])
}


repositories {
    jcenter()
    maven("https://jitpack.io")
    maven("http://maven.fabricmc.net/")
    maven("https://server.bbkr.space/artifactory/libs-release")
    maven("https://aperlambda.github.io/maven")
}

dependencies {
    // Fabric Versions
    val minecraftVersion: String by project
    val yarnMappings: String by project
    val loaderVersion: String by project
    val fabricVersion: String by project
    val fabricKotlinVersion: String by project

    // Dependency Versions
    val cardinalComponents: String by project
    val libGui: String by project
    val clothConfig: String by project
    val autoconfig1u: String by project
    val modMenu: String by project

    // Runtime mod Versions
    val reiVersion: String by project
    val databreakerVersion: String by project

    // Unit Test Versions
    val junitVersion: String by project
    val mockkVersion: String by project

    //to change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    // Cardinal Components
    modImplementation("com.github.OnyxStudios.Cardinal-Components-API:cardinal-components-base:$cardinalComponents")
    modImplementation("com.github.OnyxStudios.Cardinal-Components-API:cardinal-components-block:$cardinalComponents")
    include("com.github.OnyxStudios.Cardinal-Components-API:cardinal-components-base:$cardinalComponents")
    include("com.github.OnyxStudios.Cardinal-Components-API:cardinal-components-block:$cardinalComponents")

    // GUI Library
    // https://github.com/CottonMC/LibGui/wiki/Getting-Started-with-GUIs
    modImplementation("io.github.cottonmc:LibGui:$libGui")

    // ClothConfig and autoconfig1u
    modApi("me.shedaniel.cloth:config-2:$clothConfig") {
        exclude(group="net.fabricmc.fabric-api")
    }
    modApi("me.sargunvohra.mcmods:autoconfig1u:$autoconfig1u") {
        exclude(group="net.fabricmc.fabric-api")
    }
    include("me.shedaniel.cloth:config-2:$clothConfig")
    include("me.sargunvohra.mcmods:autoconfig1u:$autoconfig1u")

    // ModMenu
    modImplementation("io.github.prospector:modmenu:$modMenu")

    // JEI style mod
    modRuntime("me.shedaniel:RoughlyEnoughItems:$reiVersion")

    // Tech Reborn Testing
    modRuntime("TechReborn:TechReborn-1.16:+")

    // Databreaker
    modRuntime ("com.github.SuperCoder7979:databreaker:$databreakerVersion") {
        exclude(module="fabric-loader")
    }

    val rcVersion = "RebornCore:RebornCore-1.16:+"
    modApi (rcVersion) {
        exclude("net.fabricmc.fabric-api")
    }
    include(rcVersion)

    compileOnly(project(":annotations"))
    kapt(project(":processor"))

    // Unit Tests
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    // https://github.com/natanfudge/Working-Scheduler
    // https://github.com/Siphalor/nbt-crafting
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val processResources = tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", project.version)
//
//    from(sourceSets.main.resources.srcDirs) {
//        include("META-INF/mods.toml")
//        expand("version": project.version)
//    }

//    from(sourceSets.main.resources.srcDirs) {
//        exclude("META-INF/mods.toml")
//    }

    filesMatching("fabric.mod.json") {
        filter { line -> line.replace("%VERSION%", "${project.version}") }
    }
}



val javaCompile = tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val jar = tasks.getByName<Jar>("jar") {
    from("LICENSE")
}

val shadowJar = tasks.withType<Jar> {
    archiveClassifier.set("dev")

    from(sourceSets.main.get().output)

    val shadowFiles = configurations.shadow.get().files
        .map { if(it.isDirectory) it else zipTree(it) }
    from(shadowFiles)
}

val shadowRemapJar = tasks.withType<net.fabricmc.loom.task.RemapJarTask> {
    dependsOn(shadowJar)

    input.set(file("${project.buildDir}/libs/$archivesBaseName-$archiveVersion-dev.jar"))
    archiveFileName.set("${archivesBaseName}-${archiveVersion}.jar")
    addNestedDependencies.set(true)
}


// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
// if it is present.
// If you remove this task, sources will not be generated.
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions{
        jvmTarget = "1.8"
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs += listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
    }
}