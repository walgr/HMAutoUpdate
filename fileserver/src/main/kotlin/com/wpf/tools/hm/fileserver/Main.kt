package com.wpf.tools.hm.fileserver

import com.wpf.tools.hm.fileserver.server.HttpService
import com.wpf.tools.hm.fileserver.utils.HMClientManager
import com.wpf.tools.hm.fileserver.utils.HMClientManager.clientInfoFilePath
import com.wpf.tools.hm.fileserver.utils.PORT
import com.wpf.tools.hm.fileserver.utils.curPath
import com.wpf.tools.hm.fileserver.utils.savePath

fun main(args: Array<String>? = null) {
    if (args.isNullOrEmpty()) {
        println("参数异常，请检查输入参数")
        return
    }
    args.forEachIndexed { index, arg ->
        val nextInput = args.getOrNull(index + 1) ?: ""
        if (arg.startsWith("-") && nextInput.startsWith("-")) {
            println("参数异常，请检查输入${arg.replace("-", "")}")
            return
        }
        if ("-curPath" == arg) {
            curPath = nextInput
        }
        if ("-savePath" == arg) {
            savePath = nextInput
        }
        if ("-port" == arg) {
            PORT = nextInput.toInt()
        }
        if ("-clientInfoFilePath" == arg) {
            clientInfoFilePath = nextInput
        }
    }
    println("初始化读取客户端:${HMClientManager.getClientListInFile().joinToString(",") { it.name }}")
    HttpService.startServer()
}