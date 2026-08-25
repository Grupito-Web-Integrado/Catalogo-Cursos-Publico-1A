package com.example.Catalogo_Cursos.domain.model.courseLocations.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseLocationCityChangedEvent
    extends BaseDomainEvent {

  private final UUID courseLocationId;

  private final String city;

  private final Instant changedAt;

  public CourseLocationCityChangedEvent(

      UUID courseLocationId,

      String city,

      Instant changedAt

  ) {

    this.courseLocationId = courseLocationId;

    this.city = city;

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

    return CourseLocationEventType.COURSE_LOCATION_CITY_CHANGED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseLocationId() {

    return courseLocationId;
  }

  public String getCity() {

    return city;
  }

  public Instant getChangedAt() {

    return changedAt;
  }
}
