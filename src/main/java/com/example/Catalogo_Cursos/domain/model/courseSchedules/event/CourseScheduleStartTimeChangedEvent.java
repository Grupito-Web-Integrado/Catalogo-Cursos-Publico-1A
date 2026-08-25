package com.example.Catalogo_Cursos.domain.model.courseSchedules.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public class CourseScheduleStartTimeChangedEvent
    extends BaseDomainEvent {

  private final UUID courseScheduleId;

  private final LocalTime startTime;

  private final Instant changedAt;

  public CourseScheduleStartTimeChangedEvent(

      UUID courseScheduleId,

      LocalTime startTime,

      Instant changedAt

  ) {

    this.courseScheduleId = courseScheduleId;

    this.startTime = startTime;

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

    return CourseScheduleEventType.COURSE_SCHEDULE_START_TIME_CHANGED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseScheduleId() {

    return courseScheduleId;
  }

  public LocalTime getStartTime() {

    return startTime;
  }

  public Instant getChangedAt() {

    return changedAt;
  }
}
