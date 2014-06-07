package jawn

/**
 * Facade is a type class that describes how Jawn should construct
 * JSON AST elements of type J.
 * 
 * Facade[J] also uses FContext[J] instances, so implementors will
 * usually want to define both.
 */
trait SimpleFacade[J] extends Facade[J] {
  def jarray(vs: List[J]): J
  def jobject(vs: Map[String, J]): J

  def singleContext() = new FContext[J] {
    var value: J = _
    def add(s: String) { value = jstring(s) }
    def add(v: J) { value = v }
    def finish: J = value
    def isObj: Boolean = false
  }

  def arrayContext() = new FContext[J] {
    var vs = List.empty[J]
    def add(s: String) { vs = jstring(s) :: vs }
    def add(v: J) { vs = v :: vs }
    def finish: J = jarray(vs)
    def isObj: Boolean = false
  }

  def objectContext() = new FContext[J] {
    var key: String = null
    var vs = Map.empty[String, J]
    def add(s: String): Unit =
      if (key == null) { key = s } else { vs = vs.updated(key, jstring(s)); key = null }
    def add(v: J): Unit =
      { vs = vs.updated(key, v); key = null }
    def finish = jobject(vs)
    def isObj = true
  }
}