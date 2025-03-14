package com.wpf.tools.hm.fileserver.utils

import com.wpf.tools.hm.fileserver.http.MyHttpClient
import io.ktor.client.request.HttpRequestBuilder
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object CacheFile {

    /**
     * @param outFilePath 下载的文件目录带文件名
     */
    fun downloadFile(
        serverUrl: String,
        request: HttpRequestBuilder.() -> Unit = {},
        outFilePath: String,
        callback: ((File?) -> Unit)? = null
    ) {
        val cacheFile = File(outFilePath)
        if (cacheFile.exists() && cacheFile.length() != 0L) {
            callback?.invoke(cacheFile)
            return
        }
        cacheFile.createCheck(true)
        println("开始下载工具:${cacheFile.name}, 地址:$serverUrl")
        MyHttpClient.downloadFile(serverUrl, request, cacheFile.parent, "", true, callback)
    }

    suspend fun downloadFileSuspend(
        serverUrl: String,
        request: HttpRequestBuilder.() -> Unit = {},
        outFilePath: String,
    ): File? {
        return suspendCoroutine { continuation ->
            downloadFile(serverUrl, request, outFilePath) {
                continuation.resume(it)
            }
        }
    }
}