package lectures.part5ts

object FBoundedPolymorphism extends App {

  //  trait Animal {
  //    def breed: List[Animal]
  //  }
  //
  //  class Cat extends Animal {
  //    override def breed: List[Animal] = ??? // List[Cat]
  //  }
  //
  //  class Dog extends Animal {
  //    override def breed: List[Animal] = ??? // List[Dog]
  //  }

  // solution 1 - naive

  //  trait Animal {
  //    def breed: List[Animal]
  //  }
  //
  //  class Cat extends Animal {
  //    override def breed: List[Cat] = ??? // List[Cat]
  //  }
  //
  //  class Dog extends Animal {
  //    override def breed: List[Dog] = ??? // List[Dog]
  //  }

  // solution 2 - FBP

  //  trait Animal[A <: Animal[A]] { // recursive type: F-Bounded polymorphism
  //    def breed: List[Animal[A]]
  //  }
  //
  //  class Cat extends Animal[Cat] {
  //    override def breed: List[Cat] = ??? // List[Cat]
  //  }
  //
  //  class Dog extends Animal[Dog] {
  //    override def breed: List[Dog] = ??? // List[Dog]
  //  }
  //
  //  class Crocodile extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ??? // this is wrong
  //  }

  // solution 3 - FBP + self types

  //  trait Animal[A <: Animal[A]] { self: A => // recursive type: F-Bounded polymorphism
  //    def breed: List[Animal[A]]
  //  }
  //
  //  class Cat extends Animal[Cat] {
  //    override def breed: List[Cat] = ??? // List[Cat]
  //  }
  //
  //  class Dog extends Animal[Dog] {
  //    override def breed: List[Dog] = ??? // List[Dog]
  //  }

  //  class Crocodile extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ??? // this is wrong
  //  }

  //  trait Fish extends Animal[Fish]
  //
  //  class Shark extends Fish {
  //    override def breed: List[Animal[Fish]] = List(new Cod) // this is wrong
  //  }
  //
  //  class Cod extends Fish {
  //    override def breed: List[Animal[Fish]] = ???
  //  }

  // solution 4 - type classes

  //  trait Animal
  //  trait CanBreed[A] {
  //    def breed(a: A): List[A]
  //  }
  //
  //  class Dog extends Animal
  //  object Dog {
  //    implicit object DogsCanBreed extends CanBreed[Dog] {
  //      override def breed(a: Dog): List[Dog] = List()
  //    }
  //  }
  //
  //  implicit class CanBreedOps[A](animal: A) { // conversion class (enrichment)
  //    def breed(implicit canBreed: CanBreed[A]): List[A] =
  //      canBreed.breed(animal)
  //  }
  //
  //  val dog = new Dog
  //  dog.breed // List[Dog]!!

  /*
    new CanBreedOps[Dog](dog).breed(Dog.DogsCanBreed)
    implicit value to pass to breed: Dog.DogsCanBreed
   */

  //  class Cat extends Animal
  //  object Cat {
  //    implicit object CatsCanBreed extends CanBreed[Dog] {
  //      override def breed(a: Dog): List[Dog] = List()
  //    }
  //  }
  //
  //  val cat = new Cat
  //  cat.breed

  // solution 5
  trait Animal[A] { // pure classes
    def breed(a: A): List[A]
  }

  class Dog

  object Dog {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class AnimalOps[A](animal: A) {
    def breed(implicit animalTypeClassInstance: Animal[A]): List[A] = {
      animalTypeClassInstance.breed(animal)
    }
  }

  val dog = new Dog
  dog.breed

}
