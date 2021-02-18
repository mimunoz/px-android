package com.mercadopago.android.px.internal.core

import android.os.Parcelable
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.internal.util.ParcelableUtil
import java.io.File

class FileManager(private val rootCacheDir: File) {

    fun create(fileName: String): File {
        val fileNameBuilder = StringBuilder()
        fileNameBuilder.append(rootCacheDir.path)
        fileNameBuilder.append(File.separator)
        fileNameBuilder.append(fileName)
        return File(fileNameBuilder.toString())
    }

    @Synchronized
    fun <T> writeToFile(file: File, fileContent: T) {
        when (fileContent) {
            is String -> file.writeText(fileContent)
            is Parcelable -> file.writeBytes(ParcelableUtil.marshall(fileContent))
            else -> file.writeText(JsonUtil.toJson(fileContent))
        }
    }

    @Synchronized
    fun readText(file: File): String {
        return if (file.exists()) file.readText() else ""
    }

    @Synchronized
    fun <T> readAnyList(file: File, tClass: Class<T>): List<T> = JsonUtil.getListFromJson(readText(file), tClass)

    @Synchronized
    fun <K, V> readAnyMap(file: File, kClass: Class<K>, vClass: Class<V>): Map<K, V> =
        JsonUtil.getCustomMapFromJson(readText(file), kClass, vClass)

    @Synchronized
    fun <T> readParcelable(file: File, creator: Parcelable.Creator<T>): T? {
        return if (file.exists()) ParcelableUtil.unmarshall(file.readBytes(), creator) else null
    }

    @Synchronized
    fun exists(file: File) = file.exists()

    @Synchronized
    fun removeFile(file: File) = exists(file) && file.delete()
}