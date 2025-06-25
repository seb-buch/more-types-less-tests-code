import {
  buildMessage,
  createMeetingFromFrameworkValidated,
  createMeetingFromRaw,
} from "./meetingExample.js";

import { describe, expect, it } from "vitest";
import { MeetingFramework } from "./framework.js";
import { CorporateContact, InPersonMeeting, ValidationError } from "./core.js";

function validCorporateContact(email: string): CorporateContact {
  const corporateEmail = CorporateContact.new(email);
  if (corporateEmail instanceof ValidationError) {
    throw new Error("Broken test");
  }

  return corporateEmail;
}

describe("createMeetingFromFrameworkValidated", () => {
  it.each<MeetingFramework>([
    {
      title: "Team Meeting",
      contactEmail: "john.doe@acme.org",
      type: "in_person",
      meetingRoom: "Room-101",
      nGuests: 5,
    },
    {
      title: "Department Meeting",
      contactEmail: "jane.smith@acme.org",
      type: "in_person",
      meetingRoom: "Room-42",
      nGuests: 15,
    },
    {
      title: "Weekly Standup",
      contactEmail: "team.lead@acme.org",
      type: "online",
      videoPlatform: "zoom",
      videoLink: "https://zoom.us/j/123456789",
    },
    {
      title: "Project Review",
      contactEmail: "project.manager@acme.org",
      type: "online",
      videoPlatform: "meet",
      videoLink: "https://meet.google.com/abc-defg-hij",
    },
    {
      title: "Exact Room Capacity - Small",
      contactEmail: "john.doe@acme.org",
      type: "in_person",
      meetingRoom: "Room-101",
      nGuests: 20, // Exactly at capacity
    },
    {
      title: "Exact Room Capacity - Medium",
      contactEmail: "jane.smith@acme.org",
      type: "in_person",
      meetingRoom: "Room-42",
      nGuests: 50, // Exactly at capacity
    },
    {
      title: "Exact Room Capacity - Large",
      contactEmail: "ceo.boss@acme.org",
      type: "in_person",
      meetingRoom: "Amphitheater",
      nGuests: 200, // Exactly at capacity
    },
    {
      title: "Minimum Guests",
      contactEmail: "john.doe@acme.org",
      type: "in_person",
      meetingRoom: "Room-101",
      nGuests: 1, // Minimum valid number
    },
  ])(
    "should convert valid framework objects to Meeting objects (case %$)",
    (input) => {
      const result = createMeetingFromFrameworkValidated(input);

      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { contactEmail, ...partlyExpected } = input;

      expect(result).toEqual({
        ...partlyExpected,
        contact: validCorporateContact(input.contactEmail),
      });
    },
  );

  it.each<[MeetingFramework, string]>([
    [
      {
        title: "Invalid Meeting",
        contactEmail: "firstname.lastname@gmail.com",
        type: "in_person",
        meetingRoom: "Room-101",
        nGuests: 5,
      },
      "Email domain must be 'acme.org', 'gmail.com' is not valid.",
    ],
    [
      {
        title: "Invalid Meeting",
        contactEmail: "firstname.lastname@gmail.com",
        type: "online",
        videoPlatform: "meet",
        videoLink: "https://meet.google.com/abc-defg-hij",
      },
      "Email domain must be 'acme.org', 'gmail.com' is not valid.",
    ],
    [
      {
        title: "Invalid Meeting",
        contactEmail: "johndoe@acme.org",
        type: "in_person",
        meetingRoom: "Room-101",
        nGuests: 5,
      },
      "Username must be 'firstname.lastname', 'johndoe' is not valid.",
    ],
    [
      {
        title: "Room Capacity Exceeded",
        contactEmail: "john.doe@acme.org",
        type: "in_person",
        meetingRoom: "Room-101",
        nGuests: 21,
      },
      "max capacity for room 101 is 20 (21 guests required)",
    ],
    [
      {
        title: "Room Capacity Exceeded",
        contactEmail: "jane.smith@acme.org",
        type: "in_person",
        meetingRoom: "Room-42",
        nGuests: 51,
      },
      "max capacity for room 42 is 50 (51 guests required)",
    ],
    [
      {
        title: "Room Capacity Exceeded",
        contactEmail: "ceo.boss@acme.org",
        type: "in_person",
        meetingRoom: "Amphitheater",
        nGuests: 201,
      },
      "max capacity for the amphitheater is 200 (201 guests required)",
    ],
  ])(
    "should invalidates framework-validated objects based on domain rules (case %$)",
    (input, expectedMessage) => {
      const result = createMeetingFromFrameworkValidated(input);

      expect(result).toBeInstanceOf(ValidationError);
      expect((result as ValidationError).message).toBe(expectedMessage);
    },
  );
});

describe("createMeetingFromRaw", () => {
  it.each<[object, object]>([
    [
      {
        title: "Team Meeting",
        contactEmail: "john.doe@acme.org",
        type: "in_person",
        meetingRoom: "small",
        nGuests: 5,
      },
      {
        title: "Team Meeting",
        type: "in_person",
        meetingRoom: "Room-101",
        nGuests: 5,
        contact: validCorporateContact("john.doe@acme.org"),
      },
    ],
    [
      {
        title: "Weekly Standup",
        contactEmail: "team.lead@acme.org",
        type: "online",
        videoPlatform: "zoom",
        videoLink: "https://zoom.us/j/123456789",
      },
      {
        title: "Weekly Standup",
        type: "online",
        videoPlatform: "zoom",
        videoLink: "https://zoom.us/j/123456789",
        contact: validCorporateContact("team.lead@acme.org"),
      },
    ],
  ])(
    "should convert valid raw objects to Meeting objects (case %$)",
    (input, expected) => {
      const result = createMeetingFromRaw(input);
      expect(result).toEqual(expected);
    },
  );

  it.each<[object, string]>([
    [
      {
        // Missing title
        contactEmail: "john.doe@acme.org",
        type: "in_person",
        meetingRoom: "small",
        nGuests: 5,
      },
      "Raw object is not valid",
    ],
    [
      {
        title: "Invalid Meeting",
        contactEmail: "firstname.lastname@gmail.com",
        type: "in_person",
        meetingRoom: "medium",
        nGuests: 5,
      },
      "Email domain must be 'acme.org', 'gmail.com' is not valid.",
    ],
  ])(
    "should handle invalid raw objects (case %$)",
    (input, expectedMessage) => {
      const result = createMeetingFromRaw(input);
      expect(result).toBeInstanceOf(ValidationError);
      expect((result as ValidationError).message).toBe(expectedMessage);
    },
  );
});

describe("buildRoomBookingConfirmationMessage", () => {
  it("should build a confirmation message with the contact name and room", () => {
    const meeting = InPersonMeeting.new({
      title: "Team Meeting",
      contactEmail: "john.doe@acme.org",
      meetingRoom: "Room-101",
      nGuests: 5,
    });

    if (meeting instanceof ValidationError) {
      throw new Error("Failed to create meeting for test");
    }

    const message = buildMessage(meeting);
    expect(message).toBe("John DOE booked Room-101");
  });
});
