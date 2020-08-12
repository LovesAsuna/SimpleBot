package me.lovesasuna.bot.util.interfaces.file

import java.io.File

interface FileManipulate {
    fun writeDefault()

    fun writeValue()

    fun readValue()

    val file: File
}