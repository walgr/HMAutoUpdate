package com.wpf.tools.hm.fileserver.server.route

import com.wpf.tools.hm.fileserver.utils.CurVersion
import com.wpf.tools.hm.fileserver.utils.jarFilePath
import com.wpf.tools.hm.fileserver.server.HttpService
import com.wpf.tools.hm.fileserver.utils.createCheck
import io.ktor.http.content.PartData
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import java.io.File
import kotlin.system.exitProcess

fun base(route: Route) {
    route.apply {
        get("/") {
            call.respondText("欢迎使用我的文件服务系统,版本:${CurVersion}")
        }
        put("/uploadNew") {
            val part = call.receiveMultipart(formFieldLimit = Long.MAX_VALUE)
            val file = part.readPart() as? PartData.FileItem ?: return@put call.respondText("请上传更新包")
            val jarFilePath = jarFilePath()
            File(jarFilePath).createCheck(true)
                .writeBytes(file.provider.invoke().readRemaining().readByteArray())
            file.dispose()
            println("更新包已替换:${jarFilePath}")
            call.respondText("更新成功")
            HttpService.stopServer()
            ProcessBuilder("./start_hm_fileserver.sh").apply {
                directory(File(jarFilePath).parentFile)
            }.start()
            exitProcess(0)
        }
    }
}