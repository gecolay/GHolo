rootProject.name = "GHolo"

include(":core")
include(":v1_19_4")
project(":v1_19_4").projectDir = file("mcv/v1_19_4")
include(":v1_20")
project(":v1_20").projectDir = file("mcv/v1_20")
include(":v1_20_2")
project(":v1_20_2").projectDir = file("mcv/v1_20_2")
include(":v1_20_3")
project(":v1_20_3").projectDir = file("mcv/v1_20_3")
include(":v1_20_5")
project(":v1_20_5").projectDir = file("mcv/v1_20_5")
include(":v1_21")
project(":v1_21").projectDir = file("mcv/v1_21")
include(":v1_21_2")
project(":v1_21_2").projectDir = file("mcv/v1_21_2")
include(":v1_21_4")
project(":v1_21_4").projectDir = file("mcv/v1_21_4")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}