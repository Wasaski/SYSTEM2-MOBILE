package com.system2.mobile.files

import java.io.File

data class FileEntry(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val modifiedMs: Long
)

/**
 * Gerenciador de arquivos — escopos separados:
 * 1) interno SYSTEM2 (/data/data/com.system2.mobile/)
 * 2) Ubuntu (via Bridge: ls no PRoot)
 * 3) compartilhado Android (MediaStore/storage)
 * 4) Termux ($PREFIX)
 * Listagem local aqui e apenas para o escopo 1 e 3 acessivel.
 */
object FileScopeResolver {
    fun internalDir(context: android.content.Context): File = context.filesDir
    fun cacheDir(context: android.content.Context): File = context.cacheDir

    fun listDir(dir: File): List<FileEntry> {
        val children = dir.listFiles() ?: return emptyList()
        return children.map { f ->
            FileEntry(
                path = f.absolutePath,
                name = f.name,
                isDirectory = f.isDirectory,
                sizeBytes = if (f.isFile) f.length() else 0L,
                modifiedMs = f.lastModified()
            )
        }.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    }

    fun createDirectory(parent: File, name: String): File? {
        val f = File(parent, name)
        return if (f.mkdirs()) f else null
    }

    fun deleteWithConfirmation(file: File): Boolean = file.deleteRecursively()
}
