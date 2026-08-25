package com.example.Catalogo_Cursos.domain.model.courseSchedules.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public class CourseScheduleCreatedEvent
    extends BaseDomainEvent {

  private final UUID courseScheduleId;

  private final UUID courseId;

  private final DayOfWeek dayOfWeek;

  private final LocalTime startTime;

  private final LocalTime endTime;

  private final String room;

  private final Instant createdAt;

  public CourseScheduleCreatedEvent(

      UUID courseScheduleId,

      UUID courseId,

      DayOfWeek dayOfWeek,

      LocalTime startTime,

      LocalTime endTime,

      String room,

      Instant createdAt

  ) {

    this.courseScheduleId = courseScheduleId;

    this.courseId = courseId;

    this.dayOfWeek = dayOfWeek;

    this.startTime = startTime;

    this.endTime = endTime;

    this.room = room;

    this.createdAt = createdAt;
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

    return CourseScheduleEventType.COURSE_SCHEDULE_CREATED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseScheduleId() {

    return courseScheduleId;
  }

  public UUID getCourseId() {

    return courseId;
  }

  public DayOfWeek getDayOfWeek() {

    return dayOfWeek;
  }

  public LocalTime getStartTime() {

    return startTime;
  }

  public LocalTime getEndTime() {

    return endTime;
  }

  public String getRoom() {

    return room;
  }

  public Instant getCreatedAt() {

    return createdAt;
  }
}
