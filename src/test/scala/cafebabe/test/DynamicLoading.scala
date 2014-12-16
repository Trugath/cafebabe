package cafebabe.test

abstract class MyTestBase {
  def plusOne( i: Int ): Int
}

import cafebabe._
import cafebabe.ByteCodes._
import cafebabe.AbstractByteCodes._

import org.scalatest.FunSuite

class DynamicLoading extends FunSuite {

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
    val cf = mkMinimalClassFile("MyTest")

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val c = cl.loadClass("MyTest")
    val o = c.newInstance().asInstanceOf[AnyRef]
    val m = c.getMethod("plusOne", Integer.TYPE)

    assert(m.invoke(o, 41: java.lang.Integer) === 42)
  }

  test("parent abstract instance to expose the method") {
    val cf = mkMinimalClassFile("MyTest", Some("cafebabe/test/MyTestBase"))

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val dynObj = cl.newInstance("MyTest").as[MyTestBase]

    assert(dynObj.plusOne(41) === 42)
  }

  test("scala Dynamic and applyDynamic to hide reflection") {

    val cf = mkMinimalClassFile("MyTest")

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val dynObj = cl.newInstance("MyTest")

    assert(dynObj.plusOne(41) === 42)
  }

  test("infix notation with Dynamic") {

    val cf = mkMinimalClassFile("MyTest")

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val dynObj = cl.newInstance("MyTest")

    assert((dynObj plusOne 41) === 42)
  }

  test("dynamic method with empty params") {

    import scala.language.postfixOps

    val cf = mkMinimalClassFile("MyTest")

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val dynObj = cl.newInstance("MyTest")

    assert(dynObj.fortytwo() === 42)
  }

  test("dynamic method with no params") {

    import scala.language.postfixOps

    val cf = mkMinimalClassFile("MyTest")

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val dynObj = cl.newInstance("MyTest")

    assert(dynObj.fortytwo === 42)
  }

  test("postfix notation with dynamic method") {

    import scala.language.postfixOps

    val cf = mkMinimalClassFile("MyTest")

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val dynObj = cl.newInstance("MyTest")

    assert((dynObj fortytwo) === 42)
  }

  test("Invoke a non-existent method") {
    val cf = mkMinimalClassFile("MyTest")

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val dynObj = cl.newInstance("MyTest")

    intercept[NoSuchMethodException] {
      dynObj.invalidMethod
    }
  }
}