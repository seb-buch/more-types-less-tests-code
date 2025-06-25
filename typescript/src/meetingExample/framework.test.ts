import { describe, expect, it } from "vitest";
import { MeetingFramework } from "./framework.js";
import { ValidationError } from "./core.js";
import { validateRawObjectUsingFramework } from "./meetingExample.js";

describe("validateRawObjectUsingFramework", () => {
  it.each<[object | undefined | null, MeetingFramework]>([
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
        contactEmail: "john.doe@acme.org",
        type: "in_person",
        meetingRoom: "Room-101",
        nGuests: 5,
      },
    ],
    [
      {
        title: "Department Meeting",
        contactEmail: "jane.smith@acme.org",
        type: "in_person",
        meetingRoom: "medium",
        nGuests: 15,
      },
      {
        title: "Department Meeting",
        contactEmail: "jane.smith@acme.org",
        type: "in_person",
        meetingRoom: "Room-42",
        nGuests: 15,
      },
    ],
    [
      {
        title: "Company Meeting",
        contactEmail: "ceo.boss@acme.org",
        type: "in_person",
        meetingRoom: "large",
        nGuests: 100,
      },
      {
        title: "Company Meeting",
        contactEmail: "ceo.boss@acme.org",
        type: "in_person",
        meetingRoom: "Amphitheater",
        nGuests: 100,
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
        contactEmail: "team.lead@acme.org",
        type: "online",
        videoPlatform: "zoom",
        videoLink: "https://zoom.us/j/123456789",
      },
    ],
    [
      {
        title: "Project Review",
        contactEmail: "project.manager@acme.org",
        type: "online",
        videoPlatform: "meet",
        videoLink: "https://meet.google.com/abc-defg-hij",
      },
      {
        title: "Project Review",
        contactEmail: "project.manager@acme.org",
        type: "online",
        videoPlatform: "meet",
        videoLink: "https://meet.google.com/abc-defg-hij",
      },
    ],
    [
      {
        title: "Client Presentation",
        contactEmail: "sales.rep@acme.org",
        type: "online",
        videoPlatform: "teams",
        videoLink: "https://teams.microsoft.com/l/meetup-join/123456789",
      },
      {
        title: "Client Presentation",
        contactEmail: "sales.rep@acme.org",
        type: "online",
        videoPlatform: "teams",
        videoLink: "https://teams.microsoft.com/l/meetup-join/123456789",
      },
    ],
  ])("should handle valid raw objects (case %$)", (input, expected) => {
    const result = validateRawObjectUsingFramework(input);

    expect(result).toEqual(expected);
  });

  it.each<object | undefined | null>([
    {
      // Missing title
      contactEmail: "john.doe@acme.org",
      type: "in_person",
      meetingRoom: "small",
      nGuests: 5,
    },
    {
      title: "Team Meeting",
      // Missing contactEmail
      type: "in_person",
      meetingRoom: "small",
      nGuests: 5,
    },

    {
      title: "Team Meeting",
      contactEmail: "john.doe@acme.org",
      // Missing type
      meetingRoom: "small",
      nGuests: 5,
    },
    {
      title: "Room Size Mapping Test",
      contactEmail: "john.doe@acme.org",
      type: "in_person",
      meetingRoom: "Room-101", // Direct room name instead of size
      nGuests: 5,
    },
    {
      title: "   ", // Title with just whitespace
      contactEmail: "john.doe@acme.org",
      type: "in_person",
      meetingRoom: "small",
      nGuests: 5,
    },
    {
      title: "Team Meeting",
      contactEmail: "invalid-email", // Invalid email format
      type: "in_person",
      meetingRoom: "small",
      nGuests: 5,
    },

    {
      title: "Team Meeting",
      contactEmail: "john.doe@acme.org",
      type: "in_person",
      meetingRoom: "invalid-room", // Invalid meeting room
      nGuests: 5,
    },

    {
      title: "Team Meeting",
      contactEmail: "john.doe@acme.org",
      type: "in_person",
      meetingRoom: "small",
      nGuests: 0, // Non-positive number of guests
    },

    {
      title: "Weekly Standup",
      contactEmail: "team.lead@acme.org",
      type: "online",
      videoPlatform: "skype", // Invalid video platform
      videoLink: "https://zoom.us/j/123456789",
    },

    {
      title: "Weekly Standup",
      contactEmail: "team.lead@acme.org",
      type: "online",
      videoPlatform: "zoom",
      videoLink: "invalid-url", // Invalid URL
    },

    {
      title: "Invalid Meeting",
      contactEmail: "john.doe@acme.org",
      type: "hybrid", // Invalid meeting type
    },
    {
      // Empty object
    },
    undefined, // Undefined object
    null, // Null object
  ])("should invalidate invalid objects (case %$)", (dto) => {
    const expected: ValidationError = {
      message: "Raw object is not valid",
    };
    const result = validateRawObjectUsingFramework(dto);
    expect(result).toEqual(expected);
  });
});
