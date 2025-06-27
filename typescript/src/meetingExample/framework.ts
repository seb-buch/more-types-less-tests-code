import { z } from "zod/v4";
import { InPersonMeeting, MeetingRooms, videoPlatforms } from "./core.js";

// External
const commonSchema = z.object({
  title: z.string().trim().nonempty(),
  contactEmail: z.email(),
});

const inPersonMeetingSpecificSchema = z.object({
  type: z.literal("in_person"),
  meetingRoom: z.preprocess(
    (val): InPersonMeeting["meetingRoom"] | "Unknown" => {
      switch (val) {
        case "small":
          return "Room-101";
        case "medium":
          return "Room-42";
        case "large":
          return "Amphitheater";
        default:
          return "Unknown";
      }
    },
    z.enum(MeetingRooms)
  ),
  nGuests: z.int().gt(0),
});

const onlineMeetingSpecificSchema = z.object({
  type: z.literal("online"),
  videoPlatform: z.enum(videoPlatforms),
  videoLink: z.url(),
});

export const MeetingFrameworkSchema = z.intersection(
  commonSchema,
  z.discriminatedUnion("type", [
    inPersonMeetingSpecificSchema,
    onlineMeetingSpecificSchema,
  ])
);

export type MeetingFramework = z.infer<typeof MeetingFrameworkSchema>;
