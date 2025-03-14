package com.wpf.tools.hm.fileserver.server

import com.wpf.tools.hm.fileserver.utils.BASE_URL
import com.wpf.tools.hm.fileserver.utils.CurVersion
import com.wpf.tools.hm.fileserver.utils.HOST
import com.wpf.tools.hm.fileserver.utils.PORT
import com.wpf.tools.hm.fileserver.utils.savePath
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.autohead.AutoHeadResponse

object HttpService {
    private var engine: ApplicationEngine? = null
    fun startServer() {
        println("文件服务已启动，当前版本:${CurVersion}，地址：${BASE_URL}:${PORT}，当前运行目录:${savePath}")
        engine = embeddedServer(CIO, port = PORT, host = HOST, module = Application::module).start(wait = true).engine
    }

    fun stopServer() {
        println("文件服务已关闭")
        engine?.stop()
    }
}

fun Application.module() {
//    install(PartialContent)
    install(AutoHeadResponse)
    configureRouting()
}