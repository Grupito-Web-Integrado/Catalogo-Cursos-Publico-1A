package com.example.Catalogo_Cursos.domain.model.course.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseCancelledEvent
    extends BaseDomainEvent {

  private final UUID courseId;

  private final Instant cancelledAt;

  public CourseCancelledEvent(

      UUID courseId,

      Instant cancelledAt

  ) {

    this.courseId = courseId;

    this.cancelledAt = cancelledAt;
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

    return CourseEventType.COURSE_CANCELLED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseId() {

    return courseId;
  }

  public Instant getCancelledAt() {

    return cancelledAt;
  }
}
