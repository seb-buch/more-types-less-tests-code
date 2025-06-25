// Useful functions
function logDoorState(door: Door): void {
  if (door.state === "open") {
    console.log("door is opened");
    return;
  }

  if (door.isLocked) {
    console.log("door is closed and locked");
    return;
  }

  console.log("door is locked but unlocked");
}

// Types
interface OpenDoor {
  state: "open";
}

interface CloseDoor {
  state: "closed",
  isLocked: boolean
}

type Door = OpenDoor | CloseDoor

// Usage
const door1: Door = { state: "open" };
const door2: Door = { state: "closed", isLocked: false };
const door3: Door = { state: "closed", isLocked: true };

logDoorState(door1);
logDoorState(door2);
logDoorState(door3);

// These won't be accepted by tsc
// const door4: Door = {state: "open", isLocked: false};
// const door5: Door = {state: "open", isLocked: true};
// const door6: Door = {state: "closed"};
