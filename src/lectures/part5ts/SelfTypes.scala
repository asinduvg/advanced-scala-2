package lectures.part5ts

object SelfTypes extends App {

  // requiring a type to be mixed in
  trait InstrumentList {
    def play(): Unit
  }

  trait Singer {
    self: InstrumentList => // SELF TYPE -> whoever implements Singer should implement InstrumentList

    // rest of the implementation of API
    def sing(): Unit

  }

  class LeadSinger extends Singer with InstrumentList {
    override def play(): Unit = ???

    override def sing(): Unit = ???
  }

  //  class Vocalist extends Singer {
  //    override def sing(): Unit = ???
  //  } // illegal

  val jamesHetfield = new Singer with InstrumentList {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  class Guitarist extends InstrumentList {
    override def play(): Unit = println("(Guitar solo)")
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // vs inheritance
  class A

  class B extends A // B is an A

  trait T

  trait S {
    self: T =>} // S requires T

  // CAKE PATTERN => "dependency injection"

  class Component {
    // API
  }

  class ComponentA extends Component

  class ComponentB extends Component

  class DependentComponent(val component: Component)

  // CAKE PATTERN
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }

  trait ScalaDependentComponent {
    self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + " this rocks!"
  }

  trait ScalaApplication {
    self: ScalaDependentComponent =>}

  // layer1 - small components
  trait Picture extends ScalaComponent

  trait Stats extends ScalaComponent

  // layer2 - compose
  trait Profile extends ScalaDependentComponent with Picture

  trait Analytics extends ScalaDependentComponent with Stats

  // layer3 - app
  trait AnalyticsApp extends ScalaApplication with Analytics

  // cyclical dependencies
  //  class X extends Y
  //  class Y extends X // this is wrong
  //
  trait X {
    self: Y =>}

  trait Y {
    self: X =>}

}
