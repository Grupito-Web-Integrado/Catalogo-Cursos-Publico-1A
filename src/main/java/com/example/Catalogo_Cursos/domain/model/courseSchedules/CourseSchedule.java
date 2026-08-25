package com.example.Catalogo_Cursos.domain.model.courseSchedules;

import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.event.CourseScheduleCreatedEvent;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.event.CourseScheduleDayOfWeekChangedEvent;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.event.CourseScheduleEndTimeChangedEvent;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.event.CourseScheduleRoomChangedEvent;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.event.CourseScheduleStartTimeChangedEvent;
import com.example.Catalogo_Cursos.domain.model.shared.aggregate.AuditableAggregateRoot;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;

public class CourseSchedule
    extends AuditableAggregateRoot<CourseScheduleId> {

  // =========================================================
  // RELATION
  // =========================================================

  private CourseId courseId;

  // =========================================================
  // SCHEDULE
  // =========================================================

  private DayOfWeek dayOfWeek;

  private ScheduleStartTime startTime;

  private ScheduleEndTime endTime;

  // =========================================================
  // LOCATION
  // =========================================================

  private ScheduleRoom room;

  // =========================================================
  // ORM
  // =========================================================

  protected CourseSchedule() {
  }

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  private CourseSchedule(

      CourseScheduleId id,

      CourseId courseId,

      DayOfWeek dayOfWeek,

      ScheduleStartTime startTime,

      ScheduleEndTime endTime,

      ScheduleRoom room

  ) {

    this.id = Objects.requireNonNull(
        id,
        "CourseScheduleId cannot be null");

    this.courseId = Objects.requireNonNull(
        courseId,
        "CourseId cannot be null");

    this.dayOfWeek = Objects.requireNonNull(
        dayOfWeek,
        "Day of week cannot be null");

    this.startTime = Objects.requireNonNull(
        startTime,
        "Start time cannot be null");

    this.endTime = Objects.requireNonNull(
        endTime,
        "End time cannot be null");

    this.room = room;

    validateTimeRange();

    // =========================================================
    // AUDIT
    // =========================================================

    markAsCreated();

    isNew();
    // =========================================================
    // DOMAIN EVENT
    // =========================================================

    registerEvent(

        new CourseScheduleCreatedEvent(

            id.value(),

            courseId.value(),

            dayOfWeek,

            startTime.value(),

            endTime.value(),

            room != null
                ? room.value()
                : null,

            getCreatedAt()

        ));
  }

  // =========================================================
  // FACTORY
  // =========================================================

  public static CourseSchedule create(

      CourseId courseId,

      DayOfWeek dayOfWeek,

      LocalTime startTime,

      LocalTime endTime,

      String room

  ) {

    return new CourseSchedule(

        CourseScheduleId.generate(),

        courseId,

        dayOfWeek,

        ScheduleStartTime.of(
            startTime),

        ScheduleEndTime.of(
            endTime),

        room == null
            ? null
            : ScheduleRoom.of(room)

    );
  }

  // =========================================================
  // REHYDRATE
  // =========================================================

  public static CourseSchedule rehydrate(

      CourseScheduleId id,

      CourseId courseId,

      DayOfWeek dayOfWeek,

      ScheduleStartTime startTime,

      ScheduleEndTime endTime,

      ScheduleRoom room,

      Instant createdAt,

      Instant updatedAt,

      Long version,

      Boolean deleted

  ) {

    CourseSchedule schedule = new CourseSchedule();

    schedule.id = id;

    schedule.courseId = courseId;

    schedule.dayOfWeek = dayOfWeek;

    schedule.startTime = startTime;

    schedule.endTime = endTime;

    schedule.room = room;

    schedule.createdAt = createdAt;

    schedule.updatedAt = updatedAt;

    schedule.version = version;

    schedule.deleted = Boolean.TRUE.equals(
        deleted);

    schedule.markAsPersisted();
    return schedule;
  }

  // =========================================================
  // DOMAIN BEHAVIOR
  // =========================================================

  public void changeDayOfWeek(
      DayOfWeek dayOfWeek) {

    Objects.requireNonNull(
        dayOfWeek,
        "Day of week cannot be null");

    if (Objects.equals(
        this.dayOfWeek,
        dayOfWeek)) {

      return;
    }

    this.dayOfWeek = dayOfWeek;

    touch();

    registerEvent(

        new CourseScheduleDayOfWeekChangedEvent(

            id.value(),

            dayOfWeek,

            Instant.now()

        ));
  }

  public void changeStartTime(
      LocalTime value) {

    ScheduleStartTime newStartTime = ScheduleStartTime.of(value);

    if (Objects.equals(
        this.startTime,
        newStartTime)) {

      return;
    }

    if (!isValidTimeRange(
        newStartTime,
        this.endTime)) {

      throw new IllegalArgumentException(
          "Start time must be before end time");
    }

    this.startTime = newStartTime;

    touch();

    registerEvent(

        new CourseScheduleStartTimeChangedEvent(

            id.value(),

            newStartTime.value(),

            Instant.now()

        ));
  }

  public void changeEndTime(
      LocalTime value) {

    ScheduleEndTime newEndTime = ScheduleEndTime.of(value);

    if (Objects.equals(
        this.endTime,
        newEndTime)) {

      return;
    }

    if (!isValidTimeRange(
        this.startTime,
        newEndTime)) {

      throw new IllegalArgumentException(
          "End time must be after start time");
    }

    this.endTime = newEndTime;

    touch();

    registerEvent(

        new CourseScheduleEndTimeChangedEvent(

            id.value(),

            newEndTime.value(),

            Instant.now()

        ));
  }

  public void changeRoom(
      String value) {

    ScheduleRoom newRoom = value == null
        ? null
        : ScheduleRoom.of(value);

    if (Objects.equals(
        this.room,
        newRoom)) {

      return;
    }

    this.room = newRoom;

    touch();

    registerEvent(

        new CourseScheduleRoomChangedEvent(

            id.value(),

            newRoom != null
                ? newRoom.value()
                : null,

            Instant.now()

        ));
  }

  // =========================================================
  // VALIDATION
  // =========================================================

  private void validateTimeRange() {

    if (!isValidTimeRange(
        startTime,
        endTime)) {

      throw new IllegalArgumentException(
          "Start time must be before end time");
    }
  }

  private boolean isValidTimeRange(

      ScheduleStartTime startTime,

      ScheduleEndTime endTime

  ) {

    return startTime.value()
        .isBefore(
            endTime.value());
  }

  // =========================================================
  // GETTERS
  // =========================================================

  public CourseScheduleId getId() {

    return id;
  }

  public CourseId getCourseId() {

    return courseId;
  }

  public DayOfWeek getDayOfWeek() {

    return dayOfWeek;
  }

  public ScheduleStartTime getStartTime() {

    return startTime;
  }

  public ScheduleEndTime getEndTime() {

    return endTime;
  }

  public ScheduleRoom getRoom() {

    return room;
  }
}
