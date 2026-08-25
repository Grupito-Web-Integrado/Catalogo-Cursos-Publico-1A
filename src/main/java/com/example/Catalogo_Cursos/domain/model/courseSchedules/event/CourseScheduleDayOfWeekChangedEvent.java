package com.example.Catalogo_Cursos.domain.model.courseSchedules.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.UUID;

public class CourseScheduleDayOfWeekChangedEvent
    extends BaseDomainEvent {

  private final UUID courseScheduleId;

  private final DayOfWeek dayOfWeek;

  private final Instant changedAt;

  public CourseScheduleDayOfWeekChangedEvent(

      UUID courseScheduleId,

      DayOfWeek dayOfWeek,

      Instant changedAt

  ) {

    this.courseScheduleId = courseScheduleId;

    this.dayOfWeek = dayOfWeek;

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

    return CourseScheduleEventType.COURSE_SCHEDULE_DAY_OF_WEEK_CHANGED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseScheduleId() {

    return courseScheduleId;
  }

  public DayOfWeek getDayOfWeek() {

    return dayOfWeek;
  }

  public Instant getChangedAt() {

    return changedAt;
  }
}
