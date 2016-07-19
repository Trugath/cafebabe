package cafebabe.test

import cafebabe._
import cafebabe.ByteCodes._
import cafebabe.AbstractByteCodes._

import org.scalatest.FunSuite

class ClassLoaderTests extends FunSuite {
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


  test("Cannot register the same class twice") {
    val cl = new CafebabeClassLoader
    cl.register(mkMinimalClassFile("cafebabe/MyTest"))

    intercept[IllegalArgumentException] {
      cl.register(mkMinimalClassFile("cafebabe/MyTest"))
    }
  }
}
