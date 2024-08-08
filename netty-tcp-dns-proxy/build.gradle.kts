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

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
}

dependencies {
    implementation(project(":commons"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

tasks.test {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

tasks.compileJava {
    dependsOn(":commons:build")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "org.iproute.HexDumpProxy")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    configurations["runtimeClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }

    archiveFileName = "tcp-dns.jar"
}
