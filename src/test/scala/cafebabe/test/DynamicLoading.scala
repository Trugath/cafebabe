package cafebabe.test

import cafebabe._
import cafebabe.ByteCodes._
import cafebabe.AbstractByteCodes._

import org.scalatest.FunSuite

object DynamicLoading {
  abstract class MyTestBase {
    def plusOne( i: Int ): Int
  }
}

class DynamicLoading extends FunSuite {

  import cafebabe.test.DynamicLoading.MyTestBase

  private def mkMinimalClassFile(name: String, parent: Option[String] = None): ClassFile = {
    val cf = new ClassFile(name, parent)
    cf.addDefaultConstructor()
    val ch1 = cf.addMethod("I", "plusOne", "I").codeHandler
    ch1 << ILoad(1) << Ldc(1) << IADD << IRETURN
    ch1.freeze()
    val ch2 = cf.addMethod("I", "fortytwo").codeHandler
    ch2 << Ldc(42) << IRETURN
    ch2.freeze()
    cf
  }

  test("basic invoke using java reflection") {
    val cl = new CafebabeClassLoader
    cl.register(mkMinimalClassFile("MyTest"))

    val c = cl.loadClass("MyTest")
    val o = c.newInstance().asInstanceOf[AnyRef]
    val m = c.getMethod("plusOne", Integer.TYPE)

    assert(m.invoke(o, 41: java.lang.Integer) === 42)
  }

  test("parent abstract instance to expose the method") {
    val cl = new CafebabeClassLoader
    cl.register(mkMinimalClassFile("MyTest", Some("cafebabe/test/DynamicLoading$MyTestBase")))

    val dynObj = cl.newInstance("MyTest").as[MyTestBase]
    assert(dynObj.plusOne(41) === 42)
  }

  test("scala Dynamic and applyDynamic to hide reflection") {
    val cl = new CafebabeClassLoader
    cl.register(mkMinimalClassFile("MyTest"))

    val dynObj = cl.newInstance("MyTest")
    assert(dynObj.plusOne(41) === 42)
  }

  test("infix notation with Dynamic") {
    val cl = new CafebabeClassLoader
    cl.register(mkMinimalClassFile("MyTest"))

    val dynObj = cl.newInstance("MyTest")
    assert((dynObj plusOne 41) === 42)
  }

  test("dynamic method with empty params") {
    val cl = new CafebabeClassLoader
    cl.register(mkMinimalClassFile("MyTest"))

    val dynObj = cl.newInstance("MyTest")
    assert(dynObj.fortytwo() === 42)
  }

  test("dynamic method with no params") {
    val cl = new CafebabeClassLoader
    cl.register(mkMinimalClassFile("MyTest"))

    val dynObj = cl.newInstance("MyTest")
    assert(dynObj.fortytwo === 42)
  }

  test("postfix notation with dynamic method") {
    import scala.language.postfixOps

    val cl = new CafebabeClassLoader
    cl.register(mkMinimalClassFile("MyTest"))

    val dynObj = cl.newInstance("MyTest")
    assert((dynObj fortytwo) === 42)
  }

  test("Invoke a non-existent method") {
    val cl = new CafebabeClassLoader
    cl.register(mkMinimalClassFile("MyTest"))

    val dynObj = cl.newInstance("MyTest")
    intercept[NoSuchMethodException] {
      dynObj.invalidMethod
    }
  }
}