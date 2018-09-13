package org.fernice.reflare.resource

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path

interface Resource {

    fun openInputStream(): InputStream

    fun getPath(): Path?
}

data class ClasspathResource(private val resource: String) : Resource {

    private val classLoader = Thread.currentThread().contextClassLoader

    override fun openInputStream(): InputStream {
        return classLoader.getResourceAsStream(resource)
    }

    override fun getPath(): Path? {
        return null
    }
}

data class FileResource(private val resource: File) : Resource {

    override fun openInputStream(): FileInputStream {
        return resource.inputStream()
    }

    override fun getPath(): Path {
        return resource.toPath()
    }
}
