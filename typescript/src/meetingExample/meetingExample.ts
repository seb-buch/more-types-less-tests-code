// Example functions

import { MeetingFramework, MeetingFrameworkSchema } from "./framework.js";
import {
  InPersonMeeting,
  Meeting,
  OnlineMeeting,
  ValidationError,
} from "./core.js";

export function createMeetingFromRaw(
  rawObject: unknown,
): Meeting | ValidationError {
  // Step 1: use framework to "prevalidate" the object
  const frameworkValidated: MeetingFramework | ValidationError =
    validateRawObjectUsingFramework(rawObject);

  if (frameworkValidated instanceof ValidationError) {
    return frameworkValidated;
  }

  // Step 2: use smart constructors to make sure valid object are created
  return createMeetingFromFrameworkValidated(frameworkValidated);
}

export function validateRawObjectUsingFramework(
  rawObject: unknown,
): MeetingFramework | ValidationError {
  const result = MeetingFrameworkSchema.safeParse(rawObject);

  if (!result.success) {
    return new ValidationError("Raw object is not valid");
  }
  return result.data;
}

export function createMeetingFromFrameworkValidated(
  frameworkValidated: MeetingFramework,
): Meeting | ValidationError {
  switch (frameworkValidated.type) {
    case "in_person":
      return InPersonMeeting.new({
        title: frameworkValidated.title,
        contactEmail: frameworkValidated.contactEmail,
        meetingRoom: frameworkValidated.meetingRoom,
        nGuests: frameworkValidated.nGuests,
      });
    case "online":
      return OnlineMeeting.new({
        title: frameworkValidated.title,
        contactEmail: frameworkValidated.contactEmail,
        videoPlatform: frameworkValidated.videoPlatform,
        videoLink: frameworkValidated.videoLink,
      });
  }
}

export function buildMessage(meeting: InPersonMeeting) {
  const firstname = meeting.contact.firstname;
  return `${firstname} booked ${meeting.meetingRoom}`;
}
