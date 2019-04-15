tasks.withType<Test> {
    maxParallelForks = Math.max(Runtime.getRuntime().availableProcessors() / 2, 1)
}

plugins {
    kotlin("jvm")
    id("jps-compatible")
}

sourceSets {
    "main" { }
    "test" { projectDefault() }
}

val builtinsSourceSet = sourceSets.create("builtins") {
    java.srcDir("builtins")
}
val builtinsCompile by configurations

dependencies {
    compile(projectTests(":compiler:cli"))
    compile(projectTests(":idea:idea-maven"))
    compile(projectTests(":j2k"))
    compile(projectTests(":nj2k"))
    compile(projectTests(":idea:idea-android"))
    compile(projectTests(":jps-plugin"))
    compile(projectTests(":plugins:jvm-abi-gen"))
    compile(projectTests(":plugins:android-extensions-compiler"))
    compile(projectTests(":plugins:android-extensions-ide"))
    compile(projectTests(":kotlin-annotation-processing"))
    compile(projectTests(":kotlin-annotation-processing-cli"))
    compile(projectTests(":kotlin-allopen-compiler-plugin"))
    compile(projectTests(":kotlin-noarg-compiler-plugin"))
    compile(projectTests(":kotlin-sam-with-receiver-compiler-plugin"))
    compile(projectTests(":generators:test-generator"))
    builtinsCompile("org.jetbrains.kotlin:kotlin-stdlib:$bootstrapKotlinVersion")
    testCompileOnly(project(":kotlin-reflect-api"))
    testCompile(builtinsSourceSet.output)
    testRuntime(intellijDep()) { includeJars("idea_rt") }
    testRuntime(project(":kotlin-reflect"))

    if (Ide.IJ()) {
        testCompileOnly(jpsBuildTest())
        testCompile(jpsBuildTest())
    }
}


projectTest {
    workingDir = rootDir
}

val generateTests by generator("org.jetbrains.kotlin.generators.tests.GenerateTestsKt")

val generateProtoBuf by generator("org.jetbrains.kotlin.generators.protobuf.GenerateProtoBufKt")
val generateProtoBufCompare by generator("org.jetbrains.kotlin.generators.protobuf.GenerateProtoBufCompare")

val generateGradleOptions by generator("org.jetbrains.kotlin.generators.arguments.GenerateGradleOptionsKt")

val generateBuiltins by generator("org.jetbrains.kotlin.generators.builtins.generateBuiltIns.GenerateBuiltInsKt", builtinsSourceSet)

testsJar()
