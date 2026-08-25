package com.example.Catalogo_Cursos.domain.model.courseLocations.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseLocationAddressChangedEvent
    extends BaseDomainEvent {

  private final UUID courseLocationId;

  private final String address;

  private final Instant changedAt;

  public CourseLocationAddressChangedEvent(

      UUID courseLocationId,

      String address,

      Instant changedAt

  ) {

    this.courseLocationId = courseLocationId;

    this.address = address;

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

    return CourseLocationEventType.COURSE_LOCATION_ADDRESS_CHANGED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseLocationId() {

    return courseLocationId;
  }

  public String getAddress() {

    return address;
  }

  public Instant getChangedAt() {

    return changedAt;
  }
}
