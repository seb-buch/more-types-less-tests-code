// TrafficLight = (Boolean, Boolean, Boolean)
// type TrafficLight = [boolean, boolean, boolean];

// TrafficLightColor = String
// type TrafficLightColor = string

// TrafficLightColor = "red" | "orange" | "green"
type TrafficLightColor = "red" | "orange" | "green"

function logTrafficLightState(color: TrafficLightColor): void {
  console.info(`Traffic Light Color: ${color}!`);
}

// This is OK
logTrafficLightState("red");
logTrafficLightState("orange");
logTrafficLightState("green");

// This is also fine
const red = "red";
logTrafficLightState(red);

// This is not OK and tsc won't compile
// logTrafficLightState("violet");
