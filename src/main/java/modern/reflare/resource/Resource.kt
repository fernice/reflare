package modern.reflare.resource

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path

interface Resource {

    fun openInputStream(): InputStream

    fun getPath(): Path?
}

class ClasspathResource(private val resource: String) : Resource {

    private val classLoader = Thread.currentThread().contextClassLoader

    override fun openInputStream(): InputStream {
        return classLoader.getResourceAsStream(resource)
    }

    override fun getPath(): Path? {
        return null
    }

    override fun equals(other: Any?): Boolean {
        return other is ClasspathResource && other.resource == resource
    }

    override fun hashCode(): Int {
        return resource.hashCode()
    }
}

class FileResource(private val resource: File) : Resource {

    override fun openInputStream(): FileInputStream {
        return resource.inputStream()
    }

    override fun getPath(): Path {
        return resource.toPath()
    }

    override fun equals(other: Any?): Boolean {
        return other is FileResource && other.resource == resource
    }

    override fun hashCode(): Int {
        return resource.hashCode()
    }
}
