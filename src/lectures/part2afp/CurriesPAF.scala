package lectures.part2afp

object CurriesPAF extends App {

  // curried functions
  val supperAdder: Int => Int => Int =
    x => y => x + y

  val add3 = supperAdder(3) // Int => Int = y => 3 + y
  println(add3(5))

  println(supperAdder(3)(5)) // curried function

  // METHOD!
  def curriedAdder(x: Int)(y: Int): Int = x + y // curried method

  val add: Int => Int = curriedAdder(4) // converted method into function values
  // we cannot use methods as HOF unless you converted it to function values
  // lifting = ETA-EXPANSION (transforming methods into functions)

  // functions != methods (JVM limitation)
  def inc(x: Int): Int = x + 1

  List(1, 2, 3).map(inc) // ETA-EXPANSION
  //  List(1, 2, 3).map(x => inc(x)) // compiler converts into this

  // Partial function applications
  val add5 = curriedAdder(5) _ // _ tells compiler to convert it to partial function after applying first param

  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int): Int = x + y

  def curriedMethod(x: Int)(y: Int): Int = x + y

  // add7: Int => Int = y => 7 + y
  // as many as different implementations of add7 method using above

  val add7 = curriedMethod(7) _ // partially applied functions
  val add7$1$2 = curriedMethod(7)(_) // paf - alternative syntax
  val add7$2 = (x: Int) => simpleAddFunction(7, x)
  val add7$4 = simpleAddFunction.curried(7)

  val add7$5 = simpleAddMethod(7, _: Int) // alternative syntax to turning method into functions
  // y => simpleAddMethod(7, y)
  val add7$6 = simpleAddFunction(7, _: Int) //works as well

  // underscores are powerful
  def concatenator(a: String, b: String, c: String): String = a + b + c

  val insertName = concatenator("Hello, I'm ", _: String, " , how are you?")
  println(insertName("Asindu"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String)
  println(fillInTheBlanks("Asindu", "Chamika"))

  // EXERCISE
  /*
  1. Process a list of numbers and return their string representations with different formats
     Use the %4.2f, %8.6f and %14.12f with a curried formatter function.
   */

  def curriedFormatter(s: String)(number: Double): String = s.format(number)

  val numbers = List(Math.PI, Math.E, 1, 9.8, 1, 3e-12)
  val simpleFormat = curriedFormatter("%4.2f") _ //lift
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _

  println(numbers.map(simpleFormat))
  println(numbers.map(seriousFormat))
  println(numbers.map(preciseFormat))

  println(numbers.map(curriedFormatter("%14.12f"))) // compiler does sweet eta-expansion for us

  /*
  2. difference between
      - functions vs method
      - parameters: by-name vs 0-lambda
   */

  def byName(n: => Int): Int = n + 1

  def byFunction(f: () => Int): Int = f() + 1

  def method: Int = 42

  def parenMethod(): Int = 42

  /*
  calling byName and byFunction
    - int
    - method
    - parenMethod
    - lambda
    - PAF
   */

  byName(23) //ok
  byName(method) // ok
  byName(parenMethod()) // ok
  byName(parenMethod) // ok but beware ==> byName(parenMethod())
  //  byName(() => 42) // not ok
  byName((() => 42) ()) // ok
  //  byName(parenMethod _) // not ok

  //  byFunction(45) // not ok
  //  byFunction(method) // not ok does not do ETA-expansion
  byFunction(parenMethod) // compiler does ETA-expansion
  byFunction(() => 46) //ok
  byFunction(parenMethod _) // ok, also works, but warning - unnecessary

}
