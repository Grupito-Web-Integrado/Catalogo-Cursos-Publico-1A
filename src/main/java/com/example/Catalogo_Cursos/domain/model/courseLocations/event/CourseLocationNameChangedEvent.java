package com.example.Catalogo_Cursos.domain.model.courseLocations.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseLocationNameChangedEvent
    extends BaseDomainEvent {

  private final UUID courseLocationId;

  private final String name;

  private final Instant changedAt;

  public CourseLocationNameChangedEvent(

      UUID courseLocationId,

      String name,

      Instant changedAt

  ) {

    this.courseLocationId = courseLocationId;

    this.name = name;

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

    return CourseLocationEventType.COURSE_LOCATION_NAME_CHANGED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseLocationId() {

    return courseLocationId;
  }

  public String getName() {

    return name;
  }

  public Instant getChangedAt() {

    return changedAt;
  }
}
