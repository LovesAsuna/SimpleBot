package me.lovesasuna.bot.util.file

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object FileUtil {
    fun getFileMD5(file: File): String? {
        return try {
            val bytes = ByteArray(8192)
            var len: Int
            val inputStream = FileInputStream(file)
            val messageDigest = MessageDigest.getInstance("MD5")
            while (inputStream.read(bytes).also { len = it } != -1) {
                messageDigest.update(bytes, 0, len)
            }
            inputStream.close()
            val md5bytes = messageDigest.digest()
            BigInteger(1, md5bytes).toString(16)
        } catch (e: IOException) {
            null
        } catch (e: NoSuchAlgorithmException) {
            null
        }
    }

    fun toZip(srcDir: String, destFile: String) {
        var zos: ZipOutputStream? = null
        val fos = FileOutputStream(destFile)
        try {
            zos = ZipOutputStream(fos)
            val sourceFile = File(srcDir)
            compress(sourceFile, zos, sourceFile.name)
        } catch (e: Exception) {
            throw RuntimeException("zip error", e)
        } finally {
            if (zos != null) {
                try {
                    zos.close()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile 源文件
     * @param zos zip输出流
     * @param name 压缩后的名称
     * @throws Exception
     */
    private fun compress(sourceFile: File, zos: ZipOutputStream, name: String) {
        val buf = ByteArray(2048)
        if (sourceFile.isFile) {
            zos.putNextEntry(ZipEntry(name))
            var len: Int
            val `in` = FileInputStream(sourceFile)
            while (`in`.read(buf).also { len = it } != -1) {
                zos.write(buf, 0, len)
            }
            zos.closeEntry()
            `in`.close()
        } else {
            val listFiles = sourceFile.listFiles()
            if (listFiles == null || listFiles.isEmpty()) {
                zos.putNextEntry(ZipEntry("$name/"))
                zos.closeEntry()
            } else {
                for (file in listFiles) {
                    compress(file, zos, name + "/" + file.name)
                }
            }
        }
    }

    fun delete(file: File) {
        if (file.isDirectory) {
            file.listFiles().forEach {
                delete(it)
            }
        }
        file.delete()
    }
}
