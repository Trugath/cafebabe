package cafebabe

import scala.language.dynamics

/** Do not rely on this object, as it will change/be renamed, etc.
 * The goal is for `CafebabeClassLoader`s to generate `DynObj`s or
 * equivalent, so that users of dynamic class generation have to
 * write explicit reflection code as little as possible. */
class DynObj private[cafebabe](base : Any) extends Dynamic {

  import reflect.runtime.{universe => ru}
  import ru._

  private val im =
    ru.runtimeMirror(base.getClass.getClassLoader)
      .reflect(base)

  def applyDynamic(name: String)(args: Any*) : Any = {
    im.reflectMethod( im.symbol
        .typeSignature
        .member(TermName(name))
        .asMethod
      ).apply(args: _*)
  }
}
