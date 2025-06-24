from typing import Any, Dict, Tuple, Union

import pytest

from src.examples.meeting.core import (
    CorporateContact,
    InPersonMeeting,
    Meeting,
    ValidationError,
)
from src.examples.meeting.framework import MeetingFramework
from src.examples.meeting.meeting_example import (
    create_meeting_from_framework_validated,
    create_meeting_from_raw,
)


def valid_corporate_contact(email: str) -> CorporateContact:
    """Helper function to create a valid corporate contact."""
    try:
        return CorporateContact(email)
    except ValueError:
        pytest.fail("Broken test: Could not create valid corporate contact")


def build_message(meeting: InPersonMeeting) -> str:
    """Build a confirmation message with the contact name and room."""
    firstname = meeting.contact.firstname
    lastname = meeting.contact.lastname
    return f"{firstname} {lastname.upper()} booked {meeting.meeting_room}"


class TestCreateMeetingFromFrameworkValidated:
    @pytest.mark.parametrize(
        "input_data",
        [
            # Test case 1: In-person meeting
            {
                "title": "Team Meeting",
                "contact_email": "john.doe@acme.org",
                "type": "in_person",
                "meeting_room": "Room-101",
                "n_guests": 5,
            },
            # Test case 2: In-person meeting
            {
                "title": "Department Meeting",
                "contact_email": "jane.smith@acme.org",
                "type": "in_person",
                "meeting_room": "Room-42",
                "n_guests": 15,
            },
            # Test case 3: Online meeting
            {
                "title": "Weekly Standup",
                "contact_email": "team.lead@acme.org",
                "type": "online",
                "video_platform": "zoom",
                "video_link": "https://zoom.us/j/123456789",
            },
            # Test case 4: Online meeting
            {
                "title": "Project Review",
                "contact_email": "project.manager@acme.org",
                "type": "online",
                "video_platform": "meet",
                "video_link": "https://meet.google.com/abc-defg-hij",
            },
            # Test case 5: Exact Room Capacity - Small
            {
                "title": "Exact Room Capacity - Small",
                "contact_email": "john.doe@acme.org",
                "type": "in_person",
                "meeting_room": "Room-101",
                "n_guests": 20,  # Exactly at capacity
            },
            # Test case 6: Exact Room Capacity - Medium
            {
                "title": "Exact Room Capacity - Medium",
                "contact_email": "jane.smith@acme.org",
                "type": "in_person",
                "meeting_room": "Room-42",
                "n_guests": 50,  # Exactly at capacity
            },
            # Test case 7: Exact Room Capacity - Large
            {
                "title": "Exact Room Capacity - Large",
                "contact_email": "ceo.boss@acme.org",
                "type": "in_person",
                "meeting_room": "Amphitheater",
                "n_guests": 200,  # Exactly at capacity
            },
            # Test case 8: Minimum Guests
            {
                "title": "Minimum Guests",
                "contact_email": "john.doe@acme.org",
                "type": "in_person",
                "meeting_room": "Room-101",
                "n_guests": 1,  # Minimum valid number
            },
        ],
    )
    def test_valid_framework_objects(self, input_data: Dict[str, Any]) -> None:
        # Create a MeetingFramework object from the input data
        if input_data["type"] == "in_person":
            from src.examples.meeting.framework import InPersonMeetingSchema

            framework_validated = InPersonMeetingSchema.model_construct(**input_data)
        else:
            from src.examples.meeting.framework import OnlineMeetingSchema

            framework_validated = OnlineMeetingSchema.model_construct(**input_data)

        # Call the function under test
        result = create_meeting_from_framework_validated(framework_validated)

        # Check that the result is not a ValidationError
        assert not isinstance(result, ValidationError)

        # Check that the result has the expected properties
        contact_email = input_data["contact_email"]
        input_data_copy = input_data.copy()
        del input_data_copy["contact_email"]

        # Check each property individually
        for key, value in input_data_copy.items():
            assert getattr(result, key) == value

        # Check the contact property
        assert result.contact.email == contact_email
        assert isinstance(result.contact, CorporateContact)

    @pytest.mark.parametrize(
        ("input_data", "expected_message"),
        [
            # Test case 1: Invalid email domain
            (
                {
                    "title": "Invalid Meeting",
                    "contact_email": "firstname.lastname@gmail.com",
                    "type": "in_person",
                    "meeting_room": "Room-101",
                    "n_guests": 5,
                },
                "Email domain must be 'acme.org', 'gmail.com' is not valid.",
            ),
            # Test case 2: Invalid email domain (online meeting)
            (
                {
                    "title": "Invalid Meeting",
                    "contact_email": "firstname.lastname@gmail.com",
                    "type": "online",
                    "video_platform": "meet",
                    "video_link": "https://meet.google.com/abc-defg-hij",
                },
                "Email domain must be 'acme.org', 'gmail.com' is not valid.",
            ),
            # Test case 3: Invalid username format
            (
                {
                    "title": "Invalid Meeting",
                    "contact_email": "johndoe@acme.org",
                    "type": "in_person",
                    "meeting_room": "Room-101",
                    "n_guests": 5,
                },
                "Username must be 'firstname.lastname', 'johndoe' is not valid.",
            ),
            # Test case 4: Room capacity exceeded - small room
            (
                {
                    "title": "Room Capacity Exceeded",
                    "contact_email": "john.doe@acme.org",
                    "type": "in_person",
                    "meeting_room": "Room-101",
                    "n_guests": 21,
                },
                "max capacity for room 101 is 20 (21 guests required)",
            ),
            # Test case 5: Room capacity exceeded - medium room
            (
                {
                    "title": "Room Capacity Exceeded",
                    "contact_email": "jane.smith@acme.org",
                    "type": "in_person",
                    "meeting_room": "Room-42",
                    "n_guests": 51,
                },
                "max capacity for room 42 is 50 (51 guests required)",
            ),
            # Test case 6: Room capacity exceeded - large room
            (
                {
                    "title": "Room Capacity Exceeded",
                    "contact_email": "ceo.boss@acme.org",
                    "type": "in_person",
                    "meeting_room": "Amphitheater",
                    "n_guests": 201,
                },
                "max capacity for the amphitheater is 200 (201 guests required)",
            ),
        ],
    )
    def test_invalid_framework_objects(
        self, input_data: Dict[str, Any], expected_message: str
    ) -> None:
        """Test that invalid framework objects are correctly invalidated."""
        # Create a MeetingFramework object from the input data
        if input_data["type"] == "in_person":
            from src.examples.meeting.framework import InPersonMeetingSchema

            framework_validated = InPersonMeetingSchema.model_construct(**input_data)
        else:
            from src.examples.meeting.framework import OnlineMeetingSchema

            framework_validated = OnlineMeetingSchema.model_construct(**input_data)

        # Call the function under test
        result = create_meeting_from_framework_validated(framework_validated)

        # Check that the result is a ValidationError with the expected message
        assert isinstance(result, ValidationError)
        assert result.message == expected_message


class TestCreateMeetingFromRaw:
    @pytest.mark.parametrize(
        ("input_data", "expected"),
        [
            # Test case 1: In-person meeting
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
                    "type": "in_person",
                    "meeting_room": "Room-101",
                    "n_guests": 5,
                    "contact_email": "john.doe@acme.org",
                },
            ),
            # Test case 2: Online meeting
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
                    "type": "online",
                    "video_platform": "zoom",
                    "video_link": "https://zoom.us/j/123456789",
                    "contact_email": "team.lead@acme.org",
                },
            ),
        ],
    )
    def test_valid_raw_objects(
        self, input_data: Dict[str, Any], expected: Dict[str, Any]
    ) -> None:
        """Test that valid raw objects are correctly converted to Meeting objects."""
        # Call the function under test
        result = create_meeting_from_raw(input_data)

        # Check that the result is not a ValidationError
        assert not isinstance(result, ValidationError)

        # Check that the result has the expected properties
        contact_email = expected["contact_email"]
        expected_copy = expected.copy()
        del expected_copy["contact_email"]

        # Check each property individually
        for key, value in expected_copy.items():
            assert getattr(result, key) == value

        # Check the contact property
        assert result.contact.email == contact_email
        assert isinstance(result.contact, CorporateContact)

    @pytest.mark.parametrize(
        ("input_data", "expected_message"),
        [
            # Test case 1: Missing title
            (
                {
                    # Missing title
                    "contact_email": "john.doe@acme.org",
                    "type": "in_person",
                    "meeting_room": "small",
                    "n_guests": 5,
                },
                "Raw object is not valid",
            ),
            # Test case 2: Invalid email domain
            (
                {
                    "title": "Invalid Meeting",
                    "contact_email": "firstname.lastname@gmail.com",
                    "type": "in_person",
                    "meeting_room": "medium",
                    "n_guests": 5,
                },
                "Email domain must be 'acme.org', 'gmail.com' is not valid.",
            ),
        ],
    )
    def test_invalid_raw_objects(
        self, input_data: Dict[str, Any], expected_message: str
    ) -> None:
        """Test that invalid raw objects are correctly invalidated."""
        # Call the function under test
        result = create_meeting_from_raw(input_data)

        # Check that the result is a ValidationError with the expected message
        assert isinstance(result, ValidationError)
        assert result.message == expected_message


class TestBuildMessage:
    def test_build_message(self) -> None:
        # Create a meeting
        meeting = InPersonMeeting(
            title="Team Meeting",
            contact_email="john.doe@acme.org",
            meeting_room="Room-101",
            n_guests=5,
        )

        # Call the function under test
        message = build_message(meeting)

        # Check that the message is as expected
        assert message == "John DOE booked Room-101"
