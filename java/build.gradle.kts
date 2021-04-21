plugins {
    java
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "it.unibo.pcd"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(fileTree("lib").also { it.include("**/*.jar") })
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0-M1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
