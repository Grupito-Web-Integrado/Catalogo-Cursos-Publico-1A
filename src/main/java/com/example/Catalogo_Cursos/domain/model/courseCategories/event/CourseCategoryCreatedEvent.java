package com.example.Catalogo_Cursos.domain.model.courseCategories.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseCategoryCreatedEvent
    extends BaseDomainEvent {

  private final UUID courseCategoryId;

  private final String name;

  private final String description;

  private final String status;

  private final Instant createdAt;

  public CourseCategoryCreatedEvent(

      UUID courseCategoryId,

      String name,

      String description,

      String status,

      Instant createdAt

  ) {

    this.courseCategoryId = courseCategoryId;

    this.name = name;

    this.description = description;

    this.status = status;

    this.createdAt = createdAt;
  }

  @Override
  public UUID aggregateId() {

    return courseCategoryId;
  }

  @Override
  public String aggregateType() {

    return "CourseCategory";
  }

  @Override
  public String eventType() {

    return CourseCategoryEventType.COURSE_CATEGORY_CREATED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseCategoryId() {

    return courseCategoryId;
  }

  public String getName() {

    return name;
  }

  public String getDescription() {

    return description;
  }

  public String getStatus() {

    return status;
  }

  public Instant getCreatedAt() {

    return createdAt;
  }
}
