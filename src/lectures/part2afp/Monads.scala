package lectures.part2afp

object Monads extends App {

  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*
  left-identity
      unit.flatMap(f) = f(x)
      Attempt(x).flatMap(f) = f(x) // success case
      Success(x).flatMap(f) = f(x) // proved

  right-identity
      attempt.flatMap(unit) = attempt
      Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
      Fail(e).flatMap(...) = Fail(e)

  associativity
      attempt.flatMap(f).flatMap(g) == attempt.flatMap(x => f(x).flatMap(g))
      Fail(e).flatMap(f).flatMap(g) = Fail(e)
      Fail(e).flatMap(x => f(x).flatMap(g)) = Fail(e)

      Success(v).flatMap(f).flatMap(g) = f(v).flatMap(g) OR Fail(e)

      Success(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g) OR Fail(e)

   */

  val attempt = Attempt(throw new RuntimeException("My monad, yes!"))

  println(attempt)

  /*
  EXERCISE:
    1) implement a lazy[T] monad = computation which will only be executed when it's needed
        unit/apply
        flatMap

    2) Monads = unit + flatMap
        Monads = unit + map + flatten

        Monad[T] {
          def flatMap[B](f: T => Monad[B]): Monad[B] = ...(implemented)

          def map[B](f: T => B): Monad[B] = ???
          def flatten(m: Monad[Monad[T]]): Monad[T] = ???

          (have list in mind)
   */

  // 1 - Lazy Monad
  class Lazy[+A](value: => A) {
    // call by need
    private lazy val internalValue = value

    def use: A = internalValue

    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(value)
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy[A](value) // unit
  }

  val lazyInstance = Lazy {
    println("I am not feeling well today")
    42
  }

  val flatMapInstance = lazyInstance.flatMap(x => Lazy {
    10 * x
  })
  val flatMapInstance2 = lazyInstance.flatMap(x => Lazy {
    10 * x
  })

  flatMapInstance.use
  flatMapInstance.use

  /*
    left-identity
      unit.flatMap(f) = f(v)
      Lazy(v).flatMap(f) = f(v)

    right-identity
      l.flatMap(unit) = l
      Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

    associativity
      Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
      Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)
   */

}
