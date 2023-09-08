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
    implementation("org.postgresql:postgresql:42.5.3")
    implementation("org.mongodb:mongo-java-driver:3.12.10")
    implementation("org.mongodb:mongodb-driver-sync:4.9.0")
    implementation("org.mongodb:bson:4.2.3")
    implementation ("org.neo4j.driver:neo4j-java-driver:5.6.0")
    implementation("org.jfree:jfreechart:1.5.3")
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