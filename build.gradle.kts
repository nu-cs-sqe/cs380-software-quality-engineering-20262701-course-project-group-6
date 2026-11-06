import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask

plugins {
    id("java")
    id("application")
    id("checkstyle")
    id("jacoco")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.spotbugs") version "6.0.26"
    id("info.solidsoft.pitest") version "1.15.0"
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
    testImplementation("org.easymock:easymock:5.4.0")
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
    finalizedBy(tasks.jacocoTestReport) // coverage report is always generated after tests run
    finalizedBy(tasks.pitest)
}

tasks.build {
    dependsOn(tasks.pitest)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = false
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("reports/jacoco")
    }
    // The UI (View and Main) is exempt from the coverage requirement.
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) { exclude("ui/**") }
    }))
}

pitest {
    targetClasses = setOf("domain.*", "i18n.*")
    targetTests = setOf("domain.*", "i18n.*")
    junit5PluginVersion = "1.2.1"
    pitestVersion = "1.15.0"
    threads = 4
    outputFormats = setOf("HTML")
    timestampedReports = false // overwrite the report each run instead of adding a new folder
    failWhenNoMutations = false // domain has only enums until the first class with logic is added
    mutationThreshold = 100 // rubric: every mutant must be killed, so a survivor fails the build
    testSourceSets.set(listOf(sourceSets.test.get()))
    mainSourceSets.set(listOf(sourceSets.main.get()))
    jvmArgs.set(listOf("-Xmx1024m"))
    useClasspathFile.set(true) // avoids command-line length limits on Windows
    exportLineCoverage = true
}

checkstyle {
    toolVersion = "10.12.5"
    maxWarnings = 0
}

spotbugs {
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.MEDIUM)
    excludeFilter.set(file("config/spotbugs/exclude.xml"))
}

tasks.withType<SpotBugsTask>().configureEach {
    reports.create("html") {
        required.set(true)
    }
}
