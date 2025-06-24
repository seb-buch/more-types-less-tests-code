import inspect
import logging
from typing import Any, Protocol

logger = logging.getLogger(__name__)


class Quacker(Protocol):
    def quack(self) -> str: ...


class Duck(Quacker):
    def quack(self) -> str:
        return "QUACK"


class BabyDuck(Duck):
    def quack(self) -> str:
        return "QUACK QUACK QUACK"


class RubberDuck(Quacker):
    def quack(self) -> str:
        return "SQUEAK"


class FreckledDuck:
    def quack(self) -> str:
        return "quack"


class Dog:
    def woof(self) -> str:
        return "WOOF"


# Uses class
def make_duck_quack(duck: Duck) -> None:
    logger.info(f"Duck says: {duck.quack()}!")


# Uses protocol
def make_quacker_quack(quacker: Quacker) -> None:
    logger.info(f"Quacker says: {quacker.quack()}!")


# No static code analysis because of Any
def make_stuff_quacks_bad(stuff: Any) -> None:
    logger.info(f"Unverified stuff says: {stuff.quack()}!")


# Uses introspection with  "Look Before You Leap"-style
def make_stuff_quacks_lbyl(stuff: object) -> None:
    if not hasattr(stuff, "quack"):
        logger.warning(f"{type(stuff).__name__} is not a quacker!")
        return

    quack_method = stuff.quack
    if (
        not inspect.ismethod(quack_method)
        or len(inspect.signature(quack_method).parameters) > 1
    ):
        logger.warning(f"{type(stuff).__name__} is a weird quacker!")
        return

    logger.info(f"Verified stuff says: {quack_method()}!")


# Uses introspection with "Easier to Ask Forgiveness than Permission"-style
def make_stuff_quacks_eafp(stuff: object) -> None:
    try:
        logger.info(f"Verified stuff says: {stuff.quack()}!")  # type: ignore
    except AttributeError:
        logger.warning(f"{type(stuff).__name__} is not a quacker!")


def main() -> None:
    logging.basicConfig(level=logging.INFO)

    duck = Duck()
    chatty_duck = BabyDuck()
    rubber_duck = RubberDuck()
    shy_duck = FreckledDuck()
    not_quacker = Dog()

    make_duck_quack(duck)  # OK
    make_duck_quack(chatty_duck)  # OK
    make_duck_quack(rubber_duck)  # OK (duck typing) - mypy: incompatible types
    make_duck_quack(shy_duck)  # OK (duck typing) - mypy: incompatible types
    # make_duck_quack(not_quacker)  # raises AttributeError - mypy: incompatible types

    make_quacker_quack(duck)  # OK
    make_quacker_quack(chatty_duck)  # OK
    make_quacker_quack(rubber_duck)  # OK
    make_quacker_quack(shy_duck)  # OK (structural typing)
    # make_quacker_quack(not_quacker)  # raises AttributeError - mypy: incompatible types

    make_stuff_quacks_bad(duck)  # OK
    make_stuff_quacks_bad(chatty_duck)  # OK
    make_stuff_quacks_bad(rubber_duck)  # OK
    make_stuff_quacks_bad(shy_duck)  # OK
    # make_stuff_quacks_bad(not_quacker)  # raises AttributeError - mypy says OK!

    make_stuff_quacks_lbyl(duck)  # OK
    make_stuff_quacks_lbyl(chatty_duck)  # OK
    make_stuff_quacks_lbyl(rubber_duck)  # OK
    make_stuff_quacks_lbyl(shy_duck)  # OK
    make_stuff_quacks_lbyl(not_quacker)  # OK with warning

    make_stuff_quacks_eafp(duck)  # OK
    make_stuff_quacks_eafp(chatty_duck)  # OK
    make_stuff_quacks_eafp(rubber_duck)  # OK
    make_stuff_quacks_eafp(shy_duck)  # OK
    make_stuff_quacks_eafp(not_quacker)  # OK with warning


if __name__ == "__main__":
    main()
