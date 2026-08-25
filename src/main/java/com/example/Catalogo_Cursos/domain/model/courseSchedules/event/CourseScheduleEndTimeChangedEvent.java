package com.example.Catalogo_Cursos.domain.model.courseSchedules.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public class CourseScheduleEndTimeChangedEvent
    extends BaseDomainEvent {

  private final UUID courseScheduleId;

  private final LocalTime endTime;

  private final Instant changedAt;

  public CourseScheduleEndTimeChangedEvent(

      UUID courseScheduleId,

      LocalTime endTime,

      Instant changedAt

  ) {

    this.courseScheduleId = courseScheduleId;

    this.endTime = endTime;

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

    return CourseScheduleEventType.COURSE_SCHEDULE_END_TIME_CHANGED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseScheduleId() {

    return courseScheduleId;
  }

  public LocalTime getEndTime() {

    return endTime;
  }

  public Instant getChangedAt() {

    return changedAt;
  }
}
