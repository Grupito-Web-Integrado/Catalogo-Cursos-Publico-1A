package com.example.Catalogo_Cursos.domain.model.courseCategories;

import com.example.Catalogo_Cursos.domain.model.courseCategories.event.CourseCategoryActivatedEvent;
import com.example.Catalogo_Cursos.domain.model.courseCategories.event.CourseCategoryCreatedEvent;
import com.example.Catalogo_Cursos.domain.model.courseCategories.event.CourseCategoryDeactivatedEvent;
import com.example.Catalogo_Cursos.domain.model.shared.aggregate.AuditableAggregateRoot;

import java.time.Instant;
import java.util.Objects;

public class CourseCategory
    extends AuditableAggregateRoot<CourseCategoryId> {

  // =========================================================
  // CONTENT
  // =========================================================

  private CategoryName name;

  private CategoryDescription description;

  // =========================================================
  // STATE
  // =========================================================

  private CategoryStatus status;

  // =========================================================
  // ORM
  // =========================================================

  protected CourseCategory() {
  }

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  private CourseCategory(

      CourseCategoryId id,

      CategoryName name,

      CategoryDescription description,

      CategoryStatus status

  ) {

    this.id = Objects.requireNonNull(
        id,
        "CourseCategoryId cannot be null");

    this.name = Objects.requireNonNull(
        name,
        "CategoryName cannot be null");

    this.description = description;

    this.status = status != null
        ? status
        : CategoryStatus.ACTIVE;

    // =========================================================
    // AUDIT
    // =========================================================

    markAsCreated();
    isNew();
    // =========================================================
    // DOMAIN EVENT
    // =========================================================

    registerEvent(

        new CourseCategoryCreatedEvent(

            id.value(),

            name.value(),

            description != null
                ? description.value()
                : null,

            this.status.name(),

            getCreatedAt()

        ));
  }

  // =========================================================
  // FACTORY
  // =========================================================

  public static CourseCategory create(

      String name,

      String description,

      CategoryStatus status

  ) {

    return new CourseCategory(

        CourseCategoryId.generate(),

        CategoryName.of(name),

        description == null
            ? null
            : CategoryDescription.of(
                description),

        status

    );
  }

  // =========================================================
  // REHYDRATE
  // =========================================================

  public static CourseCategory rehydrate(

      CourseCategoryId id,

      CategoryName name,

      CategoryDescription description,

      CategoryStatus status,

      Instant createdAt,

      Instant updatedAt,

      Long version,

      Boolean deleted

  ) {

    CourseCategory category = new CourseCategory();

    category.id = id;

    category.name = name;

    category.description = description;

    category.status = status;

    category.createdAt = createdAt;

    category.updatedAt = updatedAt;

    category.version = version;

    category.deleted = Boolean.TRUE.equals(
        deleted);

    category.markAsPersisted();
    return category;
  }

  // =========================================================
  // DOMAIN BEHAVIOR
  // =========================================================

  public void changeName(
      String value) {

    CategoryName newName = CategoryName.of(value);

    if (Objects.equals(
        this.name,
        newName)) {

      return;
    }

    this.name = newName;

    touch();
  }

  public void updateDescription(
      String value) {

    CategoryDescription newDescription = value == null
        ? null
        : CategoryDescription.of(
            value);

    if (Objects.equals(
        this.description,
        newDescription)) {

      return;
    }

    this.description = newDescription;

    touch();
  }

  public void activate() {

    if (status == CategoryStatus.ACTIVE) {

      return;
    }

    this.status = CategoryStatus.ACTIVE;

    touch();

    registerEvent(

        new CourseCategoryActivatedEvent(

            id.value(),

            name.value(),

            Instant.now()

        ));
  }

  public void deactivate() {

    if (status == CategoryStatus.INACTIVE) {

      return;
    }

    this.status = CategoryStatus.INACTIVE;

    touch();

    registerEvent(

        new CourseCategoryDeactivatedEvent(

            id.value(),

            name.value(),

            Instant.now()

        ));
  }

  // =========================================================
  // GETTERS
  // =========================================================

  public CourseCategoryId getId() {

    return id;
  }

  public CategoryName getName() {

    return name;
  }

  public CategoryDescription getDescription() {

    return description;
  }

  public CategoryStatus getStatus() {

    return status;
  }
}
