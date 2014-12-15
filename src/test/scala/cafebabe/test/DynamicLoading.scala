
package cafebabe.test {

  abstract class MyTestBase {
    def plusOne( i: Int ): Int
  }

  import cafebabe._
  import cafebabe.ByteCodes._
  import cafebabe.AbstractByteCodes._

  import org.scalatest.FunSuite

  class DynamicLoading extends FunSuite {

    private def mkMinimalClassFile: ClassFile = {
      val cf = new ClassFile("cafebabe$test$MyTest", Some("cafebabe/test/MyTestBase"))
      cf.addDefaultConstructor()
      val ch = cf.addMethod("I", "plusOne", "I").codeHandler
      ch << ILoad(1) << Ldc(1) << IADD << IRETURN
      ch.freeze
      cf
    }

    test("DL 1") {
      val cf = mkMinimalClassFile

      val cl = new CafebabeClassLoader
      cl.register(cf)

      val c = cl.loadClass("cafebabe$test$MyTest")
      val o = c.newInstance().asInstanceOf[AnyRef]
      val m = c.getMethod("plusOne", Integer.TYPE)

      assert(m.invoke(o, 41: java.lang.Integer) === 42)
    }

    test("DL 2") {
      val cf = mkMinimalClassFile

      val cl = new CafebabeClassLoader
      cl.register(cf)
      val dynObj = cl.newInstance("cafebabe$test$MyTest").asInstanceOf[MyTestBase]
      assert(dynObj.plusOne(41) === 42)
    }
  }
}