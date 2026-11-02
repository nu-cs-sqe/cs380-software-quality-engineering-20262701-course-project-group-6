import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask

plugins {
    id("java")
    id("application")
    id("checkstyle")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.spotbugs") version "6.0.26"
}

javafx {
    version = "17.0.18"
    modules("javafx.controls")
}

application {
    mainClass.set("ui.Main")
}

group = "nu.csse.sqe"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.compileJava {
    options.release = 11
}

tasks.test {
    useJUnitPlatform()
}

checkstyle {
    toolVersion = "10.12.5"
    maxWarnings = 0
}

spotbugs {
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.MEDIUM)
}

tasks.withType<SpotBugsTask>().configureEach {
    reports.create("html") {
        required.set(true)
    }
}
