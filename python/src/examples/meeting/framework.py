from enum import Enum
from typing import Annotated, Any, Literal, Union

from pydantic import (
    AfterValidator,
    BaseModel,
    BeforeValidator,
    EmailStr,
    Field,
    HttpUrl,
    PositiveInt,
    field_validator,
)
from pydantic import ValidationError as PydanticValidationError

from src.examples.meeting.core import MeetingRoom, VideoPlatform, ValidationError


# Common schema
class CommonSchema(BaseModel, str_strip_whitespace=True):
    title: str = Field(..., min_length=1)
    contact_email: EmailStr


def _preprocess_meeting_room(value: str) -> MeetingRoom | Literal["Unknown"]:
    match value:
        case "small":
            return "Room-101"
        case "medium":
            return "Room-42"
        case "large":
            return "Amphitheater"
        case _:
            return "Unknown"


TranslatedMeetingRoom = Annotated[
    MeetingRoom, BeforeValidator(_preprocess_meeting_room)
]


# In-person meeting schema
class InPersonMeetingSchema(CommonSchema):
    type: Literal["in_person"]
    meeting_room: TranslatedMeetingRoom
    n_guests: PositiveInt


# Online meeting schema
class OnlineMeetingSchema(CommonSchema):
    type: Literal["online"]
    video_platform: VideoPlatform
    video_link: HttpUrl


# Combined schema using discriminated union
MeetingFramework = Annotated[
    Union[InPersonMeetingSchema, OnlineMeetingSchema], "Meeting framework data model"
]



BAD_RAW_OBJECT = ValidationError("Raw object is not valid")


def validate_raw_object_using_framework(
    raw_object: Any,
) -> MeetingFramework | ValidationError:
    """Validate a raw object using the framework schema."""
    if raw_object is None or not isinstance(raw_object, dict):
        return BAD_RAW_OBJECT

    try:
        # Determine the type of meeting and validate accordingly
        if raw_object.get("type") == "in_person":
            return InPersonMeetingSchema(**raw_object)
        if raw_object.get("type") == "online":
            return OnlineMeetingSchema(**raw_object)
        return BAD_RAW_OBJECT
    except PydanticValidationError:
        return BAD_RAW_OBJECT
