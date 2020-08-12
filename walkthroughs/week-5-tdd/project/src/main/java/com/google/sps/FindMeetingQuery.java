// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public final class FindMeetingQuery {

  /**
   * Find and return a set of time ranges in which a meeting can be scheduled.
   *
   * <p>This operation is O(A + B*log(B) + E), where A is total amount of attendees across all
   * events, B is the total amount of optional and required attendees for the meeting, and E is the
   * total amount of events.
   *
   * @param events  The set of events the potential attendees have to attend on the chosen day
   * @param request The meeting details, including attendees and duration
   * @return The set of time ranges in which the meeting can be scheduled
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    // Store all events each person has to go to
    // This operation is O(N) where N is the sum of the amount of attendees for all events
    final Map<String, List<Event>> eventsPerPerson = new HashMap<>();

    // Run through all events, for each event get all attendees,
    // for each attendee add to the relevant list in the map
    for (Event event : events) {
      for (String attendee : event.getAttendees()) {
        eventsPerPerson.computeIfAbsent(attendee, k -> new ArrayList<>()).add(event);
      }
    }

    // Get all events for the required attendees, and sort them by event start time.
    // This operation is O(N*log(N)) where N is the total number of attendees for the meeting
    // and log(N) is the guaranteed performance for an insertion into a TreeSet
    final Collection<Event> requiredAttendeeEvents = new TreeSet<>(ORDER_BY_EVENT_START);
    request
            .getAttendees()
            .forEach(
                    attendee ->
                            requiredAttendeeEvents.addAll(
                                    eventsPerPerson.getOrDefault(attendee, Collections.emptyList())));

    // Get all events for required and optional attendees, and sort them by event start time
    // This operation is O(N*log(N)) where N is the total number of optional attendees for the
    // meeting and log(N) is the guaranteed performance for an insertion into a TreeSet
    final Collection<Event> allAttendeeEvents = new TreeSet<>(ORDER_BY_EVENT_START);
    allAttendeeEvents.addAll(requiredAttendeeEvents);
    request
            .getOptionalAttendees()
            .forEach(
                    attendee ->
                            allAttendeeEvents.addAll(
                                    eventsPerPerson.getOrDefault(attendee, Collections.emptyList())));

    final int requestedDuration = Math.toIntExact(request.getDuration());
    // The list of viable time ranges for the meeting, including optional attendees
    Collection<TimeRange> ranges = checkRanges(allAttendeeEvents, requestedDuration);
    if (ranges.size() > 0) return ranges;

    return checkRanges(requiredAttendeeEvents, requestedDuration);
  }

  /**
   * Finds all time ranges in which a meeting can be scheduled.
   *
   * <p>This operation is O(N) where N is the amount of events
   *
   * @param events The set of events, sorted by start time, of all required participants
   * @param minDuration The minimum duration time of the meeting
   * @return The set of time ranges in which the meeting can be scheduled
   */
  private Collection<TimeRange> checkRanges(Collection<Event> events, int minDuration) {
    final Collection<TimeRange> ranges = new ArrayList<>();
    // Start trying to schedule a meeting at the start of the day
    int start = TimeRange.START_OF_DAY;
    int end = start; // End of the current event. Assume no events at first

    // Run through the events in order, checking if there is enough time in-between for a meeting
    for (Event event : events) {
      final TimeRange eventRange = event.getWhen();
      final int eventStart = eventRange.start();
      final int eventEnd = eventRange.end();

      final TimeRange tentativeRange = TimeRange.fromStartEnd(start, eventStart, false);
      if (checkViability(tentativeRange, minDuration)) ranges.add(tentativeRange);

      start =
              Math.max(
                      start,
                      Math.max(eventEnd, end)); // Set new potential start to whichever meeting ends later
      end = eventEnd;
    }

    // At the end, check if we can go from end of last meeting to end of day:
    final TimeRange tentativeRange = TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true);
    if (checkViability(tentativeRange, minDuration)) ranges.add(tentativeRange);

    return ranges;
  }

  /**
   * Check if a meeting of a minimum duration can be scheduled between gives times.
   *
   * @param range The potential range of time for the meeting
   * @param minDuration The minimum duration for the meeting
   * @return Whether a meeting of at least {@code minDuration} can be scheduled within given times
   */
  private boolean checkViability(TimeRange range, int minDuration) {
    // If the meeting doesn't overlap, and the timeslot is long enough
    return range.start() < range.end() && range.duration() >= minDuration;
  }

  // Comparator for sorting events by their start time
  public static final Comparator<Event> ORDER_BY_EVENT_START =
          (a, b) -> TimeRange.ORDER_BY_START.compare(a.getWhen(), b.getWhen());
}
