package com.example.Catalogo_Cursos.domain.model.courseLocations.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseLocationCreatedEvent
    extends BaseDomainEvent {

  private final UUID courseLocationId;

  private final UUID courseId;

  private final String name;

  private final String address;

  private final String city;

  private final String reference;

  private final Integer capacity;

  private final Instant createdAt;

  public CourseLocationCreatedEvent(

      UUID courseLocationId,

      UUID courseId,

      String name,

      String address,

      String city,

      String reference,

      Integer capacity,

      Instant createdAt

  ) {

    this.courseLocationId = courseLocationId;

    this.courseId = courseId;

    this.name = name;

    this.address = address;

    this.city = city;

    this.reference = reference;

    this.capacity = capacity;

    this.createdAt = createdAt;
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

    return CourseLocationEventType.COURSE_LOCATION_CREATED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseLocationId() {

    return courseLocationId;
  }

  public UUID getCourseId() {

    return courseId;
  }

  public String getName() {

    return name;
  }

  public String getAddress() {

    return address;
  }

  public String getCity() {

    return city;
  }

  public String getReference() {

    return reference;
  }

  public Integer getCapacity() {

    return capacity;
  }

  public Instant getCreatedAt() {

    return createdAt;
  }
}
