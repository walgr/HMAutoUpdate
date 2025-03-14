package com.wpf.tools.hm.fileserver.utils

fun String.subString(firstStr: String, endStr: String): String {
    val findStartPos = indexOf(firstStr)
    if (findStartPos == -1) {
        return ""
    }
    val findEndPos = indexOf(endStr, findStartPos + firstStr.length)
    if (findEndPos == -1) {
        return ""
    }
    return substring(findStartPos + firstStr.length, findEndPos)
}

fun String.checkWinPath(): String {
    return this.replace("\\", "\\\\")
}