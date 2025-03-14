package com.wpf.tools.hm.fileserver.utils

import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.header
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingContext
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray

class StringSynchronize {
    var value: String = ""
    fun get() = value
    fun set(value: String) {
        this.value = value
    }

    override fun toString(): String {
        return value
    }
}

class ByteArraySynchronize {
    var value: ByteArray? = null
    fun get() = value
    fun set(value: ByteArray) {
        this.value = value
    }
}

open class MultiPartDataItem(
    val name: String = "",
    var value: StringSynchronize = StringSynchronize(),
    var fileName: StringSynchronize = StringSynchronize(),
    var fileByteArray: ByteArraySynchronize = ByteArraySynchronize(),
) {
    var haveValue: Boolean = false
}
class MultiPartDataHelper(private val context: RoutingContext) {

    private val dealReturns = mutableMapOf<String, String?>()
    private val dealSuspendData = mutableMapOf<String, MultiPartDataItem>()
    fun getValue(name: String, emptyReturnStr: String? = null): MultiPartDataItem {
        var data = dealSuspendData[name]
        if (data == null) {
            data = MultiPartDataItem(name = name)
            dealReturns[name] = emptyReturnStr
        }
        dealSuspendData[name] = data
        return data
    }

    suspend fun dealAll(): String? {
        var nullReturnStr: String? = "Success"
        val headers = context.call.request.header(HttpHeaders.ContentType)
        val hasMultipart = headers != null
        if (!hasMultipart) {
            val firstNullReturnStr = dealReturns.values.firstOrNull { it?.isNotEmpty() == true }
            if (firstNullReturnStr?.isNotEmpty() == true) {
                context.call.respondText(firstNullReturnStr)
                nullReturnStr = null
            }
            return nullReturnStr
        }
        context.call.receiveMultipart(formFieldLimit = Long.MAX_VALUE).forEachPart { item ->
            dealSuspendData[item.name]?.run {
                if (item is PartData.FormItem) {
                    this.value.set(item.value)
                    this.haveValue = this.value.get().isNotEmpty() == true
                } else if (item is PartData.FileItem) {
                    this.fileByteArray.set(item.provider().readRemaining().readByteArray())
                    this.fileName.set(item.originalFileName ?: "")
                    this.haveValue = this.fileName.get().isNotEmpty() == true
                }
                if (!this.haveValue && dealReturns[item.name]?.isNotEmpty() == true) {
                    context.call.respondText(dealReturns[item.name] ?: "")
                    nullReturnStr = null
                }
            }
            dealSuspendData.remove(item.name)
            item.dispose()
        }
        dealSuspendData.forEach {
            if (dealReturns[it.key]?.isNotEmpty() == true) {
                context.call.respondText(dealReturns[it.key] ?: "")
                nullReturnStr = null
            }
        }
        return nullReturnStr
    }
}