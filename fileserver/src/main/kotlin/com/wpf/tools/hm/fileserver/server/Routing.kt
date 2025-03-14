package com.wpf.tools.hm.fileserver.server

import com.wpf.tools.hm.fileserver.server.route.hm
import com.wpf.tools.hm.fileserver.server.route.base
import io.ktor.http.content.PartData
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

const val Error_File_Not_Find = "文件未找到"

fun Application.configureRouting() {
    routing {
        //基础能力
        base(this)
        hm(this)
    }
}

fun List<PartData>.value(name: String) = try {
    (first { it.name == name } as? PartData.FormItem)?.value
} catch (e: Exception) {
    null
}

fun List<PartData>.file(name: String) = try {
    first { it.name == name } as? PartData.FileItem
} catch (e: Exception) {
    null
}
