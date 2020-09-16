package me.lovesasuna.bot.file

import java.io.File

interface FileManipulate {
    fun writeDefault()

    fun writeValue()

    fun readValue()

    val file: File
}