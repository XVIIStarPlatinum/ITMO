plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")
    repositories {
        mavenCentral()
    }
}

project(":server") {
    apply(plugin = "application")
    configure<JavaApplication> {
        mainClass = "server.ru.itmo.se.App"
    }
    dependencies {
        implementation(project(":common"))
        implementation("com.google.code.gson:gson:2.10.1")
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
    }
    tasks.withType<Jar>{
        manifest {
            attributes ["Main-Class"] = "server.ru.itmo.se.App"
        }
        destinationDirectory = File("$rootDir/Deploy")
    }
}
project(":common") {
    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
    }
}
project(":client") {
    apply(plugin = "application")
    configure<JavaApplication> {
        mainClass = "client.ru.itmo.se.App"
    }
    dependencies {
        implementation(project(":common"))
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
    }
    tasks.withType<JavaExec>{
        standardInput = System.`in`
    }
    tasks.withType<Jar> {
        manifest {
            attributes["Main-Class"] = "client.ru.itmo.se.App"
        }
        destinationDirectory = File("$rootDir/Deploy")
    }
}

group = "ru.itmo.se"
version = "2.0"
description = "Lab6"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    mavenCentral()
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven"){
            from(components["java"])
        }
    }
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}
