plugins {
    kotlin("jvm")
    id("jps-compatible")
}

dependencies {
    implementation(project(":analysis:kt-references"))

    implementation(project(":compiler:psi"))
    implementation(project(":compiler:light-classes-base"))
    implementation(project(":compiler:frontend.java"))
    implementation(intellijCore())

    compileOnly(commonDependency("com.google.guava:guava"))
}

sourceSets {
    "main" { projectDefault() }
    "test" { none() }
}
