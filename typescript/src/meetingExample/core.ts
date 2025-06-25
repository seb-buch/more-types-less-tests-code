// Constants
type MeetingRoom = "Room-101" | "Room-42" | "Amphitheater";
export const MeetingRooms: MeetingRoom[] = [
  "Room-101",
  "Room-42",
  "Amphitheater",
];
type VideoPlatform = "zoom" | "meet" | "teams";
export const videoPlatforms: VideoPlatform[] = ["zoom", "meet", "teams"];

// Specific types
interface CorporateContactProps {
  firstname: string;
  lastname: string;
  email: string;
}

export class CorporateContact {
  readonly firstname: string;
  readonly lastname: string;
  readonly email: string;

  private constructor(props: CorporateContactProps) {
    this.firstname = props.firstname;
    this.lastname = props.lastname;
    this.email = props.email;
  }

  public static new(email: string): CorporateContact | ValidationError {
    const splittedEmail = email.split("@");

    const domain = splittedEmail[1];

    if (domain != "acme.org") {
      return validationFailure(
        `Email domain must be 'acme.org', '${domain}' is not valid.`,
      );
    }

    const splittedUsername = splittedEmail[0].split(".");
    if (splittedUsername.length != 2) {
      return validationFailure(
        `Username must be 'firstname.lastname', '${splittedUsername[0]}' is not valid.`,
      );
    }

    const firstname =
      splittedUsername[0][0].toUpperCase() + splittedUsername[0].substring(1);
    const lastname = splittedUsername[1].toUpperCase();

    return new CorporateContact({
      firstname,
      lastname,
      email,
    });
  }
}

interface CommonMeetingInfo {
  title: string;
  contact: CorporateContact;
}

interface InPersonMeetingProps {
  title: string;
  contactEmail: string;

  meetingRoom: MeetingRoom;
  nGuests: number;
}

export class InPersonMeeting implements CommonMeetingInfo {
  readonly type = "in_person";

  readonly title: string;
  readonly contact: CorporateContact;

  readonly meetingRoom: MeetingRoom;
  readonly nGuests: number;

  private constructor(
    props: Omit<InPersonMeetingProps, "contactEmail"> & {
      contact: CorporateContact;
    },
  ) {
    this.title = props.title;
    this.contact = props.contact;

    this.meetingRoom = props.meetingRoom;
    this.nGuests = props.nGuests;
  }

  public static new(
    props: InPersonMeetingProps,
  ): InPersonMeeting | ValidationError {
    const validatedContact = CorporateContact.new(props.contactEmail);
    if (!(validatedContact instanceof CorporateContact)) {
      return validatedContact;
    }

    switch (props.meetingRoom) {
      case "Room-101":
        if (props.nGuests > 20) {
          return validationFailure(
            `max capacity for room 101 is 20 (${props.nGuests.toString()} guests required)`,
          );
        }
        break;
      case "Room-42":
        if (props.nGuests > 50) {
          return validationFailure(
            `max capacity for room 42 is 50 (${props.nGuests.toString()} guests required)`,
          );
        }
        break;
      case "Amphitheater":
        if (props.nGuests > 200) {
          return validationFailure(
            `max capacity for the amphitheater is 200 (${props.nGuests.toString()} guests required)`,
          );
        }
    }

    return new InPersonMeeting({ ...props, contact: validatedContact });
  }
}

interface OnlineMeetingProps {
  title: string;
  contactEmail: string;

  videoPlatform: VideoPlatform;
  videoLink: string;
}

export class OnlineMeeting implements CommonMeetingInfo {
  readonly type = "online";

  readonly title: string;
  readonly contact: CorporateContact;

  readonly videoPlatform: VideoPlatform;
  readonly videoLink: string;

  private constructor(
    props: Omit<OnlineMeetingProps, "contactEmail"> & {
      contact: CorporateContact;
    },
  ) {
    this.title = props.title;
    this.contact = props.contact;

    this.videoPlatform = props.videoPlatform;
    this.videoLink = props.videoLink;
  }

  public static new(
    props: OnlineMeetingProps,
  ): OnlineMeeting | ValidationError {
    const validatedEmail = CorporateContact.new(props.contactEmail);
    if (!(validatedEmail instanceof CorporateContact)) {
      return validatedEmail;
    }

    return new OnlineMeeting({ ...props, contact: validatedEmail });
  }
}

export type Meeting = InPersonMeeting | OnlineMeeting;

export class ValidationError {
  readonly message: string;

  constructor(message: string) {
    this.message = message;
  }
}

function validationFailure(message: string): ValidationError {
  return new ValidationError(message);
}
