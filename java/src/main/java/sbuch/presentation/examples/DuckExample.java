package sbuch.presentation.examples;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class DuckExample {
    private static final Logger LOGGER = Logger.getLogger(DuckExample.class.getName());

    public static void main(String[] args) {
        Duck duck = new Duck();
        BabyDuck babyDuck = new BabyDuck();
        RubberDuck rubberDuck = new RubberDuck();
        FreckledDuck freckledDuck = new FreckledDuck();
        Dog notQuacker = new Dog();

        makeDuckQuack(duck); // OK
        makeDuckQuack(babyDuck); // OK
//        makeDuckQuack(rubberDuck); // incompatible types
//        makeDuckQuack(freckledDuck); // incompatible types
//        makeDuckQuack(notQuacker); // incompatible types

        makeQuackerQuack(duck); // OK
        makeQuackerQuack(babyDuck); // OK
        makeQuackerQuack(rubberDuck); // OK
//        makeQuackerQuack(freckledDuck); // incompatible types
//        makeQuackerQuack(notQuacker); // incompatible types

        makeStuffQuack(duck); // OK
        makeStuffQuack(babyDuck); // OK
        makeStuffQuack(rubberDuck); // OK
        makeStuffQuack(freckledDuck); // OK
        makeStuffQuack(notQuacker); // OK with warning

    }

    private static void makeDuckQuack(Duck duck) {
        LOGGER.info("Duck says: " + duck.quack());
    }

    private static void makeQuackerQuack(Quacker quacker) {
        LOGGER.info("Quacker says: " + quacker.quack());
    }

    private static void makeStuffQuack(Object maybeQuacker) {
        Method quackMethod;
        try {
            quackMethod = maybeQuacker.getClass().getMethod("quack");
        } catch (NoSuchMethodException _) {
            LOGGER.warning(maybeQuacker.getClass().getName() + " is not a Quacker");
            return;
        }

        try {
            LOGGER.info("Verified quacker says: " + quackMethod.invoke(maybeQuacker));
        } catch (InvocationTargetException | IllegalAccessException _) {
            LOGGER.severe(maybeQuacker.getClass().getName() + " is an unknown type of quacker");
        }
    }

}

interface Quacker {
    String quack();
}

class Duck implements Quacker {
    public String quack() {
        return "QUACK";
    }
}

class BabyDuck extends Duck {
    @Override
    public String quack() {
        return "QUACK QUACK QUACK";
    }
}

class RubberDuck implements Quacker {
    @Override
    public String quack() {
        return "SQUEAK";
    }
}

class FreckledDuck {
    public String quack() {
        return "quack";
    }
}

class Dog {
    public String woof() {
        return "WOOF";
    }
}
