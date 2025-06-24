from typing import Any, Optional

import pytest
from pydantic import HttpUrl

from src.examples.meeting.framework import (
    InPersonMeetingSchema,
    OnlineMeetingSchema,
    ValidationError,
    validate_raw_object_using_framework,
)


@pytest.mark.parametrize(
    "input_data,raw_expected",
    [
        # Test case 1: Small in-person meeting
        (
            {
                "title": "Team Meeting",
                "contact_email": "john.doe@acme.org",
                "type": "in_person",
                "meeting_room": "small",
                "n_guests": 5,
            },
            {
                "title": "Team Meeting",
                "contact_email": "john.doe@acme.org",
                "type": "in_person",
                "meeting_room": "Room-101",
                "n_guests": 5,
            },
        ),
        # Test case 2: Medium in-person meeting
        (
            {
                "title": "Department Meeting",
                "contact_email": "jane.smith@acme.org",
                "type": "in_person",
                "meeting_room": "medium",
                "n_guests": 15,
            },
            {
                "title": "Department Meeting",
                "contact_email": "jane.smith@acme.org",
                "type": "in_person",
                "meeting_room": "Room-42",
                "n_guests": 15,
            },
        ),
        # Test case 3: Large in-person meeting
        (
            {
                "title": "Company Meeting",
                "contact_email": "ceo.boss@acme.org",
                "type": "in_person",
                "meeting_room": "large",
                "n_guests": 100,
            },
            {
                "title": "Company Meeting",
                "contact_email": "ceo.boss@acme.org",
                "type": "in_person",
                "meeting_room": "Amphitheater",
                "n_guests": 100,
            },
        ),
        # Test case 4: Zoom online meeting
        (
            {
                "title": "Weekly Standup",
                "contact_email": "team.lead@acme.org",
                "type": "online",
                "video_platform": "zoom",
                "video_link": "https://zoom.us/j/123456789",
            },
            {
                "title": "Weekly Standup",
                "contact_email": "team.lead@acme.org",
                "type": "online",
                "video_platform": "zoom",
                "video_link": HttpUrl("https://zoom.us/j/123456789"),
            },
        ),
        # Test case 5: Google Meet online meeting
        (
            {
                "title": "Project Review",
                "contact_email": "project.manager@acme.org",
                "type": "online",
                "video_platform": "meet",
                "video_link": "https://meet.google.com/abc-defg-hij",
            },
            {
                "title": "Project Review",
                "contact_email": "project.manager@acme.org",
                "type": "online",
                "video_platform": "meet",
                "video_link": HttpUrl("https://meet.google.com/abc-defg-hij"),
            },
        ),
        # Test case 6: Microsoft Teams online meeting
        (
            {
                "title": "Client Presentation",
                "contact_email": "sales.rep@acme.org",
                "type": "online",
                "video_platform": "teams",
                "video_link": "https://teams.microsoft.com/l/meetup-join/123456789",
            },
            {
                "title": "Client Presentation",
                "contact_email": "sales.rep@acme.org",
                "type": "online",
                "video_platform": "teams",
                "video_link": HttpUrl(
                    "https://teams.microsoft.com/l/meetup-join/123456789"
                ),
            },
        ),
    ],
)
def test_valid_raw_objects(
    input_data: dict[str, Any], raw_expected: dict[str, Any]
) -> None:
    """Test that valid raw objects are correctly validated."""
    result = validate_raw_object_using_framework(input_data)

    expected = (
        InPersonMeetingSchema.model_construct(**raw_expected)
        if raw_expected["type"] == "in_person"
        else OnlineMeetingSchema.model_construct(**raw_expected)
    )

    assert result == expected


@pytest.mark.parametrize(
    "input_data",
    [
        # Missing title
        {
            "contact_email": "john.doe@acme.org",
            "type": "in_person",
            "meeting_room": "small",
            "n_guests": 5,
        },
        # Missing contact_email
        {
            "title": "Team Meeting",
            "type": "in_person",
            "meeting_room": "small",
            "n_guests": 5,
        },
        # Missing type
        {
            "title": "Team Meeting",
            "contact_email": "john.doe@acme.org",
            "meeting_room": "small",
            "n_guests": 5,
        },
        # Direct room name instead of size
        {
            "title": "Room Size Mapping Test",
            "contact_email": "john.doe@acme.org",
            "type": "in_person",
            "meeting_room": "Room-101",
            "n_guests": 5,
        },
        # Title with just whitespace
        {
            "title": "   ",
            "contact_email": "john.doe@acme.org",
            "type": "in_person",
            "meeting_room": "small",
            "n_guests": 5,
        },
        # Invalid email format
        {
            "title": "Team Meeting",
            "contact_email": "invalid-email",
            "type": "in_person",
            "meeting_room": "small",
            "n_guests": 5,
        },
        # Invalid meeting room
        {
            "title": "Team Meeting",
            "contact_email": "john.doe@acme.org",
            "type": "in_person",
            "meeting_room": "invalid-room",
            "n_guests": 5,
        },
        # Non-positive number of guests
        {
            "title": "Team Meeting",
            "contact_email": "john.doe@acme.org",
            "type": "in_person",
            "meeting_room": "small",
            "n_guests": 0,
        },
        # Invalid video platform
        {
            "title": "Weekly Standup",
            "contact_email": "team.lead@acme.org",
            "type": "online",
            "video_platform": "skype",
            "video_link": "https://zoom.us/j/123456789",
        },
        # Invalid URL
        {
            "title": "Weekly Standup",
            "contact_email": "team.lead@acme.org",
            "type": "online",
            "video_platform": "zoom",
            "video_link": "invalid-url",
        },
        # Invalid meeting type
        {
            "title": "Invalid Meeting",
            "contact_email": "john.doe@acme.org",
            "type": "hybrid",
        },
        # Empty object
        {},
        # None object
        None,
    ],
)
def test_invalid_objects(input_data: Optional[dict[str, Any]]) -> None:
    """Test that invalid raw objects are correctly invalidated."""
    expected = ValidationError("Raw object is not valid")
    result = validate_raw_object_using_framework(input_data)

    assert isinstance(result, ValidationError)
    assert result.message == expected.message
