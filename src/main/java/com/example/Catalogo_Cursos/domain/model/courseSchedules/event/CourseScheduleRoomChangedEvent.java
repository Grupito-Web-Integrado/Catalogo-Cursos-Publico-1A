package com.example.Catalogo_Cursos.domain.model.courseSchedules.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseScheduleRoomChangedEvent
    extends BaseDomainEvent {

  private final UUID courseScheduleId;

  private final String room;

  private final Instant changedAt;

  public CourseScheduleRoomChangedEvent(

      UUID courseScheduleId,

      String room,

      Instant changedAt

  ) {

    this.courseScheduleId = courseScheduleId;

    this.room = room;

    this.changedAt = changedAt;
  }

  @Override
  public UUID aggregateId() {

    return courseScheduleId;
  }

  @Override
  public String aggregateType() {

    return "CourseSchedule";
  }

  @Override
  public String eventType() {

    return CourseScheduleEventType.COURSE_SCHEDULE_ROOM_CHANGED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseScheduleId() {

    return courseScheduleId;
  }

  public String getRoom() {

    return room;
  }

  public Instant getChangedAt() {

    return changedAt;
  }
}
