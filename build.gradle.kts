plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.slf4j:slf4j-log4j12:2.0.7")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.qiniu:qiniu-java-sdk:7.13.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("commons-io:commons-io:2.12.0")
    implementation("org.slf4j:slf4j-api:2.0.7")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}