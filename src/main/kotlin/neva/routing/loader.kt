package neva.routing


import fi.iki.elonen.NanoHTTPD
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import java.io.*
import java.lang.reflect.Method
import java.nio.file.Paths

/**
 * This monstrosity will be responsible for hot-loading any classes in the application's package. The idea is that the
 * application packages should be stateless. The rest of the app (database connections, web server, session store) won't
 * need to be restarted so it should be quite snappy.
 */

class Reloaded(private val directory: String) : ClassLoader() {
  private val classes = mutableMapOf<String, Class<*>>()

  override fun loadClass(s: String): Class<*> {
    return classes[s] ?: findClass(s)
  }

  override fun findClass(name: String): Class<*> {
    val file = loadClassData(name)
    val clazz = if (file.exists()){
      if(name.startsWith("exampleapp")){
        val bytes = file.readBytes()
        defineClass(name, bytes, 0, bytes.size)
      } else {
        Reloaded::class.java.classLoader.loadClass(name)
      }

    } else {
      Reloaded::class.java.classLoader.loadClass(name)
    }

    classes[name] = clazz
    return clazz
  }

  private fun loadClassData(className: String): File {
    // replace . with / to convert from package notation to file path
    val classLocation = "$directory/${className.replace(".", "/")}.class"
    return File(classLocation)
  }
}

data class Action(private val obj: Any, private val method: Method, val path: String){
  fun invoke() : NanoHTTPD.Response {
    return method.invoke(obj) as NanoHTTPD.Response
  }
}

/**
 * Currently always reloads from disk however it should eventually check to see if it needs to
 */
fun getRoutes(directory: String): List<Action> {
  val classLoader = Reloaded(directory)
  val directoryPath = Paths.get(directory)!!
  File(directory).walkTopDown().toList()
    .filter { it.extension == "class" }
    .map {
      val className = it.name.replace(".${it.extension}", "")
      val path = directoryPath.relativize(Paths.get("${it.parent}/$className"))

      classLoader.loadClass(path.toString().replace("\\", "."))
    }

  val reflections = Reflections(ClasspathHelper.classLoaders(classLoader), MethodAnnotationsScanner())
  val getEndpoints = reflections.getMethodsAnnotatedWith(Get::class.java)



  return getEndpoints.map { Action(it.declaringClass.newInstance(), it, it.getDeclaredAnnotation(Get::class.java).path )}

}