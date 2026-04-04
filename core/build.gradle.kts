plugins {
    `maven-publish`
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.1.build.+")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.googlecode.json-simple:json-simple:1.1.1")
}

java {
    disableAutoTargetJvm()
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.compileJava {
    options.release = 17
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(project.components["java"])
        }
    }
}