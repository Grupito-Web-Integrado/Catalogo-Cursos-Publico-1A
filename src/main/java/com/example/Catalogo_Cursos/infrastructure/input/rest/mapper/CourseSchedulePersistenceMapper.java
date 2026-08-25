package com.example.Catalogo_Cursos.infrastructure.input.rest.mapper;

import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseSchedule;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseScheduleId;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.ScheduleEndTime;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.ScheduleRoom;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.ScheduleStartTime;
import com.example.Catalogo_Cursos.infrastructure.input.rest.entity.CourseScheduleEntity;

import java.time.DayOfWeek;

public class CourseSchedulePersistenceMapper {

  private CourseSchedulePersistenceMapper() {
  }

  // =========================================================
  // DOMAIN -> ENTITY
  // =========================================================

  public static CourseScheduleEntity toEntity(
      CourseSchedule schedule) {

    if (schedule == null) {
      return null;
    }

    CourseScheduleEntity entity = CourseScheduleEntity.builder()

        .id(
            schedule.getId().value())

        .courseId(
            schedule.getCourseId().value())

        .dayOfWeek(
            schedule.getDayOfWeek().name())

        .startTime(
            schedule.getStartTime().value())

        .endTime(
            schedule.getEndTime().value())

        .room(
            schedule.getRoom() != null
                ? schedule.getRoom().value()
                : null)

        .createdAt(
            schedule.getCreatedAt())

        .updatedAt(
            schedule.getUpdatedAt())

        .version(
            schedule.getVersion())

        .deleted(
            schedule.isDeleted())

        .build();

    // =========================================================
    // PERSISTENCE STATE
    // =========================================================
    //
    // Si el aggregate ya existe en BD:
    //
    // persisted = true
    // isNew = false
    // => UPDATE
    //
    // Si el aggregate es nuevo:
    //
    // persisted = false
    // isNew = true
    // => INSERT
    //

    if (schedule.isPersisted()) {
      entity.markNotNew();
    } else {
      entity.markAsNew();
    }

    return entity;
  }

  // =========================================================
  // ENTITY -> DOMAIN
  // =========================================================

  public static CourseSchedule toDomain(
      CourseScheduleEntity entity) {

    if (entity == null) {
      return null;
    }

    CourseSchedule schedule = CourseSchedule.rehydrate(

        CourseScheduleId.of(
            entity.getId()),

        CourseId.of(
            entity.getCourseId()),

        DayOfWeek.valueOf(
            entity.getDayOfWeek()),

        ScheduleStartTime.of(
            entity.getStartTime()),

        ScheduleEndTime.of(
            entity.getEndTime()),

        entity.getRoom() != null
            ? ScheduleRoom.of(
                entity.getRoom())
            : null,

        entity.getCreatedAt(),

        entity.getUpdatedAt(),

        entity.getVersion(),

        entity.getDeleted());

    // =========================================================
    // PERSISTENCE STATE
    // =========================================================
    //
    // La entidad fue obtenida desde BD.
    // Por lo tanto, el aggregate ya existe.
    //

    schedule.markAsPersisted();

    return schedule;
  }
}
