plugins {
    application
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("io.github.cdimascio:dotenv-java:2.3.2")
}

application {
    mainClass.set("databaseauditor.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    testLogging.showStandardStreams = true

}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("./docs/jacoco report"))
    }
}

jacoco {
    toolVersion = "0.8.8"
    reportsDirectory.set(layout.buildDirectory.dir("./docs/jacoco report"))
}