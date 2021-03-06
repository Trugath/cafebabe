package cafebabe

import scala.collection.mutable.{Map=>MutableMap}

import scala.language.dynamics

/** `CafebabeClassLoader`s generate `DynObj`s so that users
  * of dynamic class generation have to write explicit reflection
  * code as little as possible.
  */
class DynObj private[cafebabe] (base: Any) extends Dynamic {

  import reflect.runtime.{universe => ru}
  import ru._

  private val methodCache: MutableMap[String, MethodMirror] = MutableMap.empty

  private val im =
    ru.runtimeMirror(base.getClass.getClassLoader)
      .reflect(base)

  private def getMethod(name: String): MethodMirror = {
    if(methodCache.contains(name)) {
      methodCache(name)
    } else {
      try {
        val m = im.reflectMethod( im.symbol
          .typeSignature
          .member(TermName(name))
          .asMethod
        )
        methodCache += ((name, m))
        m
      } catch {
        case e: scala.ScalaReflectionException if e.msg == "<none> is not a method" =>
          throw new NoSuchMethodException(base.getClass.toString + " has no " + name + " method")
      }
    }
  }

  def selectDynamic(name: String) : Any = getMethod(name).apply()
  def applyDynamic(name: String)(args: Any*) : Any = getMethod(name).apply(args: _*)
  def as[A]: A = base.asInstanceOf[A]
}
