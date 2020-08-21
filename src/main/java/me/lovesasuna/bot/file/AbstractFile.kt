package me.lovesasuna.bot.file

import me.lovesasuna.bot.util.interfaces.file.FileManipulate
import java.io.File

/**
 * @author LovesAsuna
 * @date 2020/8/21 10:16
 **/
abstract class AbstractFile : FileManipulate {
    override val file: File = File("")
}