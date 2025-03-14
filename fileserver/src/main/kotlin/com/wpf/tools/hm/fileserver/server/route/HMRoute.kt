package com.wpf.tools.hm.fileserver.server.route

import com.wpf.tools.hm.fileserver.server.Error_File_Not_Find
import com.wpf.tools.hm.fileserver.utils.RootPath
import com.wpf.tools.hm.fileserver.utils.FileUtil
import com.wpf.tools.hm.fileserver.utils.HMClientManager
import com.wpf.tools.hm.fileserver.utils.MultiPartDataHelper
import com.wpf.tools.hm.fileserver.utils.createCheck
import com.wpf.tools.hm.fileserver.utils.md5
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import java.io.File
import kotlin.concurrent.thread

const val Upload_Hap_File = "newUpload.hap"

fun getHMRootPath(packageName: String, appVersion: String): String {
    return arrayOf(RootPath, packageName, appVersion).joinToString(File.separator)
}

fun getHMRootFolder(packageName: String, appVersion: String): File {
    return File(getHMRootPath(packageName, appVersion))
}

fun hm(route: Route) {
    route.apply {
        get("/getHap{package?}{appVersion?}") {
            val packageName = call.parameters["package"] ?: return@get call.respondText("请输入包名")
            val appVersion = call.parameters["appVersion"] ?: return@get call.respondText("请输入App版本号")
            kotlin.runCatching {
                val file = getHMRootFolder(packageName, appVersion)
                if (!file.exists() || !file.isDirectory) {
                    call.respondText(Error_File_Not_Find)
                    return@get
                }
                val findPatchFile = file.listFiles()?.findLast { it.isFile && it.extension == "hap" }
                if (findPatchFile == null) {
                    println("文件夹下未找到hap文件:${file.path}")
                    call.respondText(Error_File_Not_Find)
                    return@get
                }
                call.response.header(
                    HttpHeaders.ContentDisposition, ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName, findPatchFile.name
                    ).withParameter(
                        ContentDisposition.Parameters.Size, findPatchFile.length().toString()
                    ).withParameter("md5", findPatchFile.md5())
                        .withParameter("uploadTime", findPatchFile.lastModified().toString())
                        .toString()
                )
                call.respondFile(findPatchFile)
            }.onFailure {
                call.respondText(Error_File_Not_Find)
            }
        }
        put("/uploadHap") {
            val partHelper = MultiPartDataHelper(this)
            val packageName = partHelper.getValue("package", "请输入包名").value
            val appVersion = partHelper.getValue("appVersion", "请输入App版本号").value
            val fileStream = partHelper.getValue("file", "未接收到文件").fileByteArray
            val savaFileName = partHelper.getValue("savaFileName", "").value
            partHelper.dealAll() ?: return@put
            val rootFile = getHMRootFolder(packageName.get(), appVersion.get()).createCheck(false)
            val copyNewFile =
                File(rootFile.path + File.separator + savaFileName.get().ifEmpty { Upload_Hap_File }).createCheck(true)
            FileUtil.save2File(fileStream.get()!!, copyNewFile)
            HMClientManager.installHapToClients(copyNewFile.path)
            call.respondText("保存Hap成功")
        }
        delete("/deleteHap{package?}{appVersion?}") {
            val packageName = call.parameters["package"] ?: return@delete call.respondText("请输入包名")
            val appVersion = call.parameters["appVersion"] ?: return@delete call.respondText("请输入App版本号")
            getHMRootFolder(packageName, appVersion).listFiles()
                ?.filter { it.isFile && it.extension == "hap" }?.forEach {
                    it.deleteRecursively()
                }
            call.respondText("删除Hap" + "成功")
        }
        post("retryInstallNewHap") {
            val packageName = call.parameters["package"] ?: return@post call.respondText("请输入包名")
            val appVersion = call.parameters["appVersion"] ?: return@post call.respondText("请输入App版本号")
            kotlin.runCatching {
                val file = getHMRootFolder(packageName, appVersion)
                if (!file.exists() || !file.isDirectory) {
                    call.respondText(Error_File_Not_Find)
                    return@post
                }
                val findPatchFile = file.listFiles()?.findLast { it.isFile && it.extension == "hap" }
                if (findPatchFile == null) {
                    println("文件夹下未找到hap文件:${file.path}")
                    call.respondText(Error_File_Not_Find)
                    return@post
                }
                HMClientManager.installHapToClients(findPatchFile.path)
                call.respondText("重新安装成功")
            }.onFailure {
                call.respondText(Error_File_Not_Find)
            }
        }
    }
}