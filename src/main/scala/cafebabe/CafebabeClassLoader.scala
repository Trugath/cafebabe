package cafebabe

import scala.collection.mutable.{Map=>MutableMap}

/** A `ClassLoader` with the capability for loading cafebabe
 *  `ClassFile`s directly from memory. */
class CafebabeClassLoader(parent : ClassLoader) extends ClassLoader(parent) {
  def this() {
    this(ClassLoader.getSystemClassLoader)
  }

  private val classBytes : MutableMap[String,Array[Byte]] = MutableMap.empty

  private def canonicalName(name: String): String = name.replace('/', '.').replace('$', '.')

  def register(classFile : ClassFile) {
    val name = classFile.className
    val canonical = canonicalName(name)
    if(classBytes.isDefinedAt(canonical)) {
      throw new IllegalArgumentException("Cannot define the same class twice (%s).".format(name))
    }

    val byteStream = new ByteStream << classFile
    classBytes(canonical) = byteStream.getBytes
  }

  // Takes the classes binary name eg. com/foo/bar
  override def findClass(name: String) : Class[_] = {
    classBytes.get(canonicalName(name)) match {
      case Some(ba) =>
        defineClass(name, ba, 0, ba.length)

      case None => super.findClass(name)
    }
  }

  def newInstance(name : String) : DynObj = {
    new DynObj(this.loadClass(name).newInstance())
  }
}
