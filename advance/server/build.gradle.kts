plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
}

group = "org.iproute"
version = "1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(":commons"))
}

tasks.compileJava {
    dependsOn(":commons:build")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "org.iproute.server.ServerMain")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    configurations.runtimeClasspath.get()
        .filter { it.name.endsWith("jar") }
        .forEach { file: File -> from(zipTree(file.absoluteFile)) }

    archiveFileName = "server.jar"

}
