from dataclasses import dataclass
from typing import Literal, Protocol


# Types
@dataclass
class OpenDoor:
    pass


@dataclass
class ClosedDoor:
    is_locked: bool


type Door = OpenDoor | ClosedDoor


def log_door_state(door: Door) -> None:
    if isinstance(door, OpenDoor):
        print("Door opened")
        return

    print(f"Door closed {'and locked' if door.is_locked else 'unlocked'}")


if __name__ == "__main__":
    # Usage
    door1: Door = OpenDoor()
    door2: Door = ClosedDoor(is_locked=False)
    door3: Door = ClosedDoor(is_locked=True)

    log_door_state(door1)
    log_door_state(door2)
    log_door_state(door3)
