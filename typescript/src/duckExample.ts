interface Quacker {
  quack(): string;
}

// True quackers
class Duck implements Quacker {
  public quack(): string {
    return "QUACK";
  }
}

class BabyDuck extends Duck {
  public override quack(): string {
    return "QUACK QUACK QUACK";
  }
}

class RubberDuck implements Quacker {
  public quack(): string {
    return "SQUEAK";
  }
}

// Structural quacker
class FreckledDuck {
  public quack(): string {
    return "quack";
  }
}

// Not a quacker
class Dog {
  public woof(): string {
    return "WOOF";
  }
}

// Uses class... or does it?
function makeDuckQuack(duck: Duck) {
  console.log(`Duck says: ${duck.quack()}!`);
}

// Type narrowing
function isQuacker(obj: unknown): obj is Quacker {
  if (typeof obj !== "object") {
    return false;
  }

  return typeof (obj as Quacker).quack === "function";
}

// Uses introspection
function makeStuffQuack(stuff: unknown) {
  if (!isQuacker(stuff)) {
    console.warn("Passed object is not a Quacker");
    return;
  }
  console.log(`Verified quacker says: ${stuff.quack()}!`);
}

const duck = new Duck();
const babyDuck = new BabyDuck();
const rubberDuck = new RubberDuck();
const freckledDuck = new FreckledDuck();
const notQuacker = new Dog();

makeDuckQuack(duck); // OK
makeDuckQuack(babyDuck); // OK
makeDuckQuack(rubberDuck); // OK
makeDuckQuack(freckledDuck); // OK

// makeDuckQuack(notQuacker); // TS2345: Argument of type 'Dog' is not assignable to parameter of type 'Duck'.

makeStuffQuack(duck); // OK
makeStuffQuack(babyDuck); // OK
makeStuffQuack(rubberDuck); // OK
makeStuffQuack(freckledDuck); // OK
makeStuffQuack(notQuacker); // OK with warning
