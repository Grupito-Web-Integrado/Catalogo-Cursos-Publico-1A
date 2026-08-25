package com.example.Catalogo_Cursos.domain.model.courseLocations.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseLocationCapacityChangedEvent
    extends BaseDomainEvent {

  private final UUID courseLocationId;

  private final Integer capacity;

  private final Instant changedAt;

  public CourseLocationCapacityChangedEvent(

      UUID courseLocationId,

      Integer capacity,

      Instant changedAt

  ) {

    this.courseLocationId = courseLocationId;

    this.capacity = capacity;

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

    return CourseLocationEventType.COURSE_LOCATION_CAPACITY_CHANGED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseLocationId() {

    return courseLocationId;
  }

  public Integer getCapacity() {

    return capacity;
  }

  public Instant getChangedAt() {

    return changedAt;
  }
}
