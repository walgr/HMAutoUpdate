plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.0.3")
    implementation("io.ktor:ktor-server-cio:3.0.3")
    implementation("io.ktor:ktor-server-auto-head-response:3.0.3")
    implementation("io.ktor:ktor-client-core:3.0.3")
    implementation("io.ktor:ktor-client-cio:3.0.3")
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("ohos.tools:unpack:1.0.0")
    implementation("com.alibaba:fastjson:2.0.56")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

tasks.register("打包", Jar::class) {
    group = "upload"
    archiveFileName = "hmfileserver.jar"
    destinationDirectory.set(file("D:\\Android\\ShareFile\\tools"))
    manifest {
        attributes["Main-Class"] = "com.wpf.tools.hm.fileserver.MainKt"
        attributes["Manifest-Version"] = "1.0.0"
    }
    from(
        sourceSets.main.get().output,
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    )
    exclude(
        "META-INF/*.RSA",
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/versions/9/module-info.class",
        "META-INF/INDEX.LIST",
        "module-info.class",
        "META-INF/LICENSE.txt",
    )
}

tasks.register("上传", Exec::class) {
    group = "upload"
    setIgnoreExitValue(true)
    dependsOn("打包")
    val uploadUrl = "http://192.168.1.186:6458/uploadNew"
    val uploadFile = file("D:\\Android\\ShareFile\\tools\\hmfileserver.jar")
    commandLine("cmd.exe", "/c", "curl -X PUT -F file=@$uploadFile $uploadUrl")
}