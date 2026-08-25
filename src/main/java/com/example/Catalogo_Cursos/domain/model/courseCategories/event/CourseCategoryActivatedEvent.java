package com.example.Catalogo_Cursos.domain.model.courseCategories.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseCategoryActivatedEvent
    extends BaseDomainEvent {

  private final UUID courseCategoryId;

  private final String name;

  private final Instant activatedAt;

  public CourseCategoryActivatedEvent(

      UUID courseCategoryId,

      String name,

      Instant activatedAt

  ) {

    this.courseCategoryId = courseCategoryId;

    this.name = name;

    this.activatedAt = activatedAt;
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

    return CourseCategoryEventType.COURSE_CATEGORY_ACTIVATED
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

  public Instant getActivatedAt() {

    return activatedAt;
  }
}
