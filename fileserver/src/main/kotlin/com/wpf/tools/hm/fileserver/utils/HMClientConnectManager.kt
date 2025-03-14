package com.wpf.tools.hm.fileserver.utils

import java.io.File
import kotlin.concurrent.thread

object HMClientManager {
    var clientInfoFilePath: String = curPath + File.separator + "clientInfo.txt"
    private var clientList = listOf<HMClientInfo>()

    fun getClientListInFile(): List<HMClientInfo> {
        val clientFile = File(clientInfoFilePath)
        val clientLineStr = clientFile.readLines()
        clientList = clientLineStr.map {
            HMClientInfo().apply {
                name = it
                connectInfo = it
            }
        }
        return clientList
    }

    fun connectClients(callback: ((connectSuccessClient: HMClientInfo) -> Unit)? = null) {
        getClientListInFile()
        clientList.forEach {
            HDCUtil.connectClient(it.connectInfo) {
                callback?.invoke(it)
            }
        }

    }

    fun installHapToClients(hapFilePath: String) {
        thread {
            runCatching {
                val hapFile = File(hapFilePath)
                if (!hapFile.exists() || !hapFile.isFile || hapFile.length() == 0L) return@runCatching
                val hapInfo = HMUnpackingUtil.getHapInfo(hapFile)
                if (hapInfo == null || hapInfo.bundleName.isNullOrEmpty()) return@runCatching
                connectClients {
                    HDCUtil.installHap(
                        hapFile,
                        hapInfo.bundleName ?: "",
                        hapInfo.abilityName ?: "",
                        it.connectInfo
                    )
                }
            }.getOrElse {
                it.printStackTrace()
                println("执行安装hap到客户端失败:${it.message}")
            }
        }
    }
}