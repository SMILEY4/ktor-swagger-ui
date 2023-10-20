package io.github.smiley4.ktorswaggerui.data

object DataUtils {

    fun mergeBoolean(base: Boolean, value: Boolean) = if (value) true else base

    fun <T> mergeDefault(base: T, value: T, default: T) = if (value != default) value else base

    fun <T> merge(base: T?, value: T?) = value ?: base

}
