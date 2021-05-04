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
    implementation("io.reactivex.rxjava3:rxjava:3.0.13-RC2")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.github.akarnokd:rxjava3-extensions:3.0.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
