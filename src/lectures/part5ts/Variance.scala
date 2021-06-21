package lectures.part5ts

object Variance extends App {

  trait Animal

  class Dog extends Animal

  class Cat extends Animal

  class Crocodile extends Animal

  // what is variance?
  // "inheritance" - type substitution of generics

  class Cage[T]

  // yes - covariance
  class CCage[+T]

  val cCage: CCage[Animal] = new CCage[Cat]

  // no - invariance
  class ICage[T]

  //  val iCage: ICage[Animal] = new ICage[Cat] // this is wrong
  // val x: Int = "Hello"
  val iCage: ICage[Animal] = new ICage[Animal]

  // hell no - contravariance (opposite)
  class XCage[-T]

  val xCage: XCage[Cat] = new XCage[Animal]


  class InvariantCage[T](val animal: T) // invariant

  // covariant positions
  class CovariantCage[T](val animal: T) // covariant position

  //  class ContravariantCage[-T](val animal: T)
  //
  //  val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)

  //    class CovariantVariableCage[+T](var animal: T) // types of vars are in contravariant position
  //
  //  val cCage: CCage[Animal] = new CCage[Cat](new Cat)
  //  cCage.animal = new Crocodile

  //  class ContravariantCableCage[-T](var animal: T) // also in covariant position
  //    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)

  class InvariantVariableCage[T](var animal: T) // ok

  //  trait AnotherCovariantCage[+T] {
  //    def addAnimal(animal: T) // contravariant position
  //  }
  //
  //  val cCage: CCage[Animal] = new CCage[Cat]
  //  cCage.add(new Dog)

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }

  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  //  acc.addAnimal(new Crocodile) // this is wrong
  acc.addAnimal(new Cat)

  class Kitty extends Cat

  acc.addAnimal(new Kitty) // ok

  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)

  // method arguments are in contravariant position

  class PetShop[-T] {
    //    def get(isPuppy: Boolean): T // covariant position

    /*
      val catShop = new PetShop[Animal] {
        def get(isPuppy: Boolean): Animal = new Cat
      }

      val dogShop: PetShop[Dog] = catShop
      dogShop.get(true) // returns EVIL CAT!!

     */

    def get[S <: T](isPuppy: Boolean, defaultAnimal: S): S = defaultAnimal

  }

  val shop: PetShop[Dog] = new PetShop[Animal]

  //  shop.get(true, new Cat) // this is illegal
  class TerraNova extends Dog

  val bigFurry = shop.get(true, new TerraNova)

  /*
    Big rule
      - method arguments are in contravariant position
      - return types are in covariant position
   */

  /*
    Exercise
      1. Invariant, Covariant, Contravariant
          Parking[T](things: List[T]) {
            park(vehicle: T)
            impound(vehicles: List[T])
            checkVehicles(conditions: String): List[T]
          }

   */

  class Vehicle

  class Bike extends Vehicle

  class Car extends Vehicle

  class Bens extends Car

  // covariant
  class Parking[+T](things: List[T]) {
    def park[S >: T](vehicle: S): Parking[S] = new Parking(vehicle +: things)

    def impound[S >: T](vehicles: List[S]): Parking[S] = ???

    def checkVehicles(conditions: String): List[T] = ???
  }

  class Parking2[T](things: List[T]) {
    def park(vehicle: T): Parking2[T] = new Parking2(vehicle +: things)

    def impound(vehicles: List[T]): Parking2[T] = ???

    def checkVehicles(conditions: String): List[T] = ???
  }

  class Parking3[-T](things: List[T]) {
    def park(vehicle: T): Parking3[T] = new Parking3(vehicle +: things)

    def impound(vehicles: List[T]): Parking3[T] = ???

    def checkVehicles[S <: T](conditions: String): List[S] = ???
  }

  /*
    Rule of thumb
      - use covariance = COLLECTION OF THINGS
      - use contravariance = GROUP OF ACTIONS

   */

  val carList: Parking[Vehicle] = new Parking[Car](List(new Car, new Car))
  val newList = carList.park(new Bike)
  val newList2 = carList.park(new Car)

  val carList3 = new Parking3[Car](List(new Car, new Car))
  val newCarList3 = carList3.park(new Bens)

}
