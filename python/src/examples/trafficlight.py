from typing import Literal, Tuple, TypeIs, reveal_type

# TrafficLight = (Boolean, Boolean, Boolean)
type TrafficLight = tuple[bool, bool, bool]

# TrafficLightColor = String
# type TrafficLightColor = str

# TrafficLightColor = "red" | "orange" | "green"
type TrafficLightColor = Literal["red", "orange", "green"]


def log_traffic_light(traffic_light: TrafficLightColor) -> None:
    print(f"Traffic light is {traffic_light}")


def is_valid(color: str) -> TypeIs[TrafficLightColor]:
    return color in ("red", "orange", "green")


if __name__ == "__main__":
    # This is OK
    log_traffic_light("red")
    log_traffic_light("orange")
    log_traffic_light("green")

    # This is not OK for Mypy
    color = "red"
    log_traffic_light(color)

    # This is also not OK for Mypy
    for color in ("red", "orange", "green"):
        log_traffic_light(color)

    # But this is OK because of type narrowing
    for color in ("red", "orange", "green"):
        if is_valid(color):
            log_traffic_light(color)

    # This one runs OK but Mypy complains about it
    # log_traffic_light("violet")

    # But this is OK... and never runs :)
    color = "violet"
    if is_valid(color):
        log_traffic_light(color)
