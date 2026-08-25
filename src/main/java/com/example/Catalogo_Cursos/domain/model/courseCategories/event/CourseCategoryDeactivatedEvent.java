package com.example.Catalogo_Cursos.domain.model.courseCategories.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CourseCategoryDeactivatedEvent
    extends BaseDomainEvent {

  private final UUID courseCategoryId;

  private final String name;

  private final Instant deactivatedAt;

  public CourseCategoryDeactivatedEvent(

      UUID courseCategoryId,

      String name,

      Instant deactivatedAt

  ) {

    this.courseCategoryId = courseCategoryId;

    this.name = name;

    this.deactivatedAt = deactivatedAt;
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

    return CourseCategoryEventType.COURSE_CATEGORY_DEACTIVATED
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

  public Instant getDeactivatedAt() {

    return deactivatedAt;
  }
}
