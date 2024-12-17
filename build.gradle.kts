plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
    id("io.papermc.paperweight.userdev") version "1.7.7" apply false
}

allprojects {
    apply(plugin = "java-library")

    repositories {
        mavenLocal()
        mavenCentral()

        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    tasks.javadoc {
        options.encoding = "UTF-8"
    }
}

dependencies {
    api(project(":core"))
    api(project(":v1_19_4"))
    api(project(":v1_20"))
    api(project(":v1_20_2"))
    api(project(":v1_20_3"))
    api(project(":v1_20_5", configuration = "reobf"))
    api(project(":v1_21", configuration = "reobf"))
    api(project(":v1_21_2", configuration = "reobf"))
    api(project(":v1_21_4", configuration = "reobf"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.release.set(17)
    }

    processResources {
        from("src/resources")
        expand(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "main" to "${project.group}.${project.name}Main"
        )
    }

    publishToMavenLocal {
        dependsOn(build)
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
}