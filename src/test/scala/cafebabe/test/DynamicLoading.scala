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
    val ch = cf.addMethod("I", "plusOne", "I").codeHandler
    ch << ILoad(1) << Ldc(1) << IADD << IRETURN
    ch.freeze
    cf
  }

  test("DL 1") {
    val cf = mkMinimalClassFile("MyTest")

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val c = cl.loadClass("MyTest")
    val o = c.newInstance().asInstanceOf[AnyRef]
    val m = c.getMethod("plusOne", Integer.TYPE)

    assert(m.invoke(o, 41: java.lang.Integer) === 42)
  }

  test("DL 2") {
    val cf = mkMinimalClassFile("MyTest", Some("cafebabe/test/MyTestBase"))

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val dynObj = cl.newInstance("MyTest").asInstanceOf[MyTestBase]

    assert(dynObj.plusOne(41) === 42)
  }

  test("DL 3") {

    val cf = mkMinimalClassFile("MyTest")

    val cl = new CafebabeClassLoader
    cl.register(cf)

    val dynObj = cl.newDynamicInstance("MyTest")

    assert(dynObj.plusOne(41) === 42)
  }
}