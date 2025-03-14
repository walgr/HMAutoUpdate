package com.wpf.tools.hm.fileserver.utils

import java.io.File
import java.util.jar.JarFile

var curPath = File(".").canonicalPath
val jarFileName = "hmfileserver.jar"
fun jarFilePath() = curPath + File.separator + jarFileName

val CurVersion: String by lazy {
    if (File(jarFilePath()).exists()) {
        JarFile(jarFilePath()).manifest.mainAttributes.getValue("Manifest-Version")
    } else "1.0.0"
}
val HOST = "0.0.0.0"
val BASE_URL = "http://$HOST"
var PORT = 8081

var savePath = File(".").canonicalPath
val RootPath: String by lazy {
    savePath + File.separator + "HMUpload" + File.separator
}
val isLinuxRuntime = System.getProperties().getProperty("os.name").contains("Linux")
val isWinRuntime = System.getProperties().getProperty("os.name").contains("Windows")
val isMacRuntime = System.getProperties().getProperty("os.name").contains("Mac")
