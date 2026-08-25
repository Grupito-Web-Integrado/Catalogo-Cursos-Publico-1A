package com.example.Catalogo_Cursos.domain.model.course.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CoursePublishedEvent
    extends BaseDomainEvent {

  private final UUID courseId;

  private final String code;

  private final String name;

  private final Instant publishedAt;

  public CoursePublishedEvent(

      UUID courseId,

      String code,

      String name,

      Instant publishedAt

  ) {

    this.courseId = courseId;

    this.code = code;

    this.name = name;

    this.publishedAt = publishedAt;
  }

  @Override
  public UUID aggregateId() {

    return courseId;
  }

  @Override
  public String aggregateType() {

    return "Course";
  }

  @Override
  public String eventType() {

    return CourseEventType.COURSE_PUBLISHED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseId() {

    return courseId;
  }

  public String getCode() {

    return code;
  }

  public String getName() {

    return name;
  }

  public Instant getPublishedAt() {

    return publishedAt;
  }
}
