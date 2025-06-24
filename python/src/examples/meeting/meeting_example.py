from typing import assert_never, Any

from src.examples.meeting.core import (
    InPersonMeeting,
    Meeting,
    OnlineMeeting,
    ValidationError,
)
from src.examples.meeting.framework import (
    MeetingFramework,
    validate_raw_object_using_framework,
)


def create_meeting_from_framework_validated(
    framework_validated: MeetingFramework,
) -> Meeting | ValidationError:
    try:
        match framework_validated.type:
            case "in_person":
                return InPersonMeeting(
                    title=framework_validated.title,
                    contact_email=str(framework_validated.contact_email),
                    meeting_room=framework_validated.meeting_room,
                    n_guests=framework_validated.n_guests,
                )
            case "online":
                return OnlineMeeting(
                    title=framework_validated.title,
                    contact_email=str(framework_validated.contact_email),
                    video_platform=framework_validated.video_platform,
                    video_link=str(framework_validated.video_link),
                )
            case _:
                assert_never(framework_validated.type)
    except ValueError as err:
        return ValidationError(err.args[0])


def create_meeting_from_raw(raw_object: Any) -> Meeting | ValidationError:
    # Step
    validation_result = validate_raw_object_using_framework(raw_object)

    if isinstance(validation_result, ValidationError):
        return validation_result

    return create_meeting_from_framework_validated(validation_result)
