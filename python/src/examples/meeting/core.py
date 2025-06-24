from dataclasses import dataclass
from typing import Literal, assert_never

# Constants
type MeetingRoom = Literal["Room-101", "Room-42", "Amphitheater"]
type VideoPlatform = Literal["teams", "meet", "zoom"]


@dataclass(frozen=True)
class ValidationError:
    """Error returned when validation fails."""

    message: str


@dataclass
class CorporateContact:
    firstname: str
    lastname: str
    email: str

    def __init__(self, email: str) -> None:
        username, domain = email.split("@", maxsplit=1)

        if domain != "acme.org":
            raise ValueError(
                f"Email domain must be 'acme.org', '{domain}' is not valid."
            )

        try:
            firstname, lastname = username.split(".", maxsplit=1)
        except ValueError as err:
            raise ValueError(
                f"Username must be 'firstname.lastname', '{username}' is not valid."
            ) from err

        self.firstname = firstname.capitalize()
        self.lastname = lastname.upper()
        self.email = email


@dataclass
class InPersonMeeting:
    type = "in_person"

    title: str
    contact: CorporateContact

    meeting_room: MeetingRoom
    n_guests: int

    def __init__(
        self,
        title: str,
        contact_email: str,
        meeting_room: MeetingRoom,
        n_guests: int,
    ) -> None:
        self.title = title
        self.contact = CorporateContact(contact_email)

        match meeting_room:
            case "Room-101":
                if n_guests > 20:
                    raise ValueError(
                        f"max capacity for room 101 is 20 ({n_guests} guests required)"
                    )
            case "Room-42":
                if n_guests > 50:
                    raise ValueError(
                        f"max capacity for room 42 is 50 ({n_guests} guests required)"
                    )
            case "Amphitheater":
                if n_guests > 200:
                    raise ValueError(
                        "max capacity for the amphitheater is 200"
                        + f" ({n_guests} guests required)"
                    )
            case _:
                assert_never(meeting_room)

        self.meeting_room = meeting_room
        self.n_guests = n_guests


@dataclass
class OnlineMeeting:
    type = "online"

    title: str
    contact: CorporateContact

    video_platform: VideoPlatform
    video_link: str

    def __init__(
        self,
        title: str,
        contact_email: str,
        video_platform: VideoPlatform,
        video_link: str,
    ) -> None:
        self.title = title
        self.contact = CorporateContact(contact_email)

        self.video_platform = video_platform
        self.video_link = video_link


Meeting = InPersonMeeting | OnlineMeeting
