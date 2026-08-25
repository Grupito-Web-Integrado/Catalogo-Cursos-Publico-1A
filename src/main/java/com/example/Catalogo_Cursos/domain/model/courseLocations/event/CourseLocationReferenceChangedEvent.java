package com.example.Catalogo_Cursos.domain.model.courseLocations.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseLocationReferenceChangedEvent
    extends BaseDomainEvent {

  private final UUID courseLocationId;

  private final String reference;

  private final Instant changedAt;

  public CourseLocationReferenceChangedEvent(

      UUID courseLocationId,

      String reference,

      Instant changedAt

  ) {

    this.courseLocationId = courseLocationId;

    this.reference = reference;

    this.changedAt = changedAt;
  }

  @Override
  public UUID aggregateId() {

    return courseLocationId;
  }

  @Override
  public String aggregateType() {

    return "CourseLocation";
  }

  @Override
  public String eventType() {

    return CourseLocationEventType.COURSE_LOCATION_REFERENCE_CHANGED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseLocationId() {

    return courseLocationId;
  }

  public String getReference() {

    return reference;
  }

  public Instant getChangedAt() {

    return changedAt;
  }
}
