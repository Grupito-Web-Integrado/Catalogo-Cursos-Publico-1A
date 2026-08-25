package com.example.Catalogo_Cursos.domain.model.course;

import com.example.Catalogo_Cursos.domain.model.course.event.*;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;
import com.example.Catalogo_Cursos.domain.model.shared.aggregate.AuditableAggregateRoot;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Slf4j
public class Course extends AuditableAggregateRoot<CourseId> {

  // =========================================================
  // IDENTITY
  // =========================================================

  private CourseCode code;

  // =========================================================
  // CONTENT
  // =========================================================

  private CourseName name;

  private CourseDescription description;

  // =========================================================
  // CONFIGURATION
  // =========================================================

  private CourseModality modality;

  private CoursePrice price;

  // =========================================================
  // PERIOD
  // =========================================================

  private LocalDate startDate;

  private LocalDate endDate;

  private LocalTime startTime;

  private CourseDuration durationHours;

  // =========================================================
  // CAPACITY
  // =========================================================

  private CourseCapacity capacity;

  private AvailableSlots availableSlots;

  // =========================================================
  // RELATIONS
  // =========================================================

  private CourseCategoryId categoryId;

  // =========================================================
  // STATE
  // =========================================================

  private CourseStatus status;

  // =========================================================
  // ORM
  // =========================================================

  protected Course() {
  }

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  private Course(

      CourseId id,

      CourseCode code,

      CourseName name,

      CourseDescription description,

      CourseModality modality,

      CoursePrice price,

      LocalDate startDate,

      LocalDate endDate,

      LocalTime startTime,

      CourseDuration durationHours,

      CourseCapacity capacity,

      AvailableSlots availableSlots,

      CourseCategoryId categoryId,

      CourseStatus status

  ) {

    this.id = Objects.requireNonNull(
        id,
        "CourseId cannot be null");

    this.code = Objects.requireNonNull(
        code,
        "CourseCode cannot be null");

    this.name = Objects.requireNonNull(
        name,
        "CourseName cannot be null");

    this.description = description;

    this.modality = Objects.requireNonNull(
        modality,
        "CourseModality cannot be null");

    this.price = Objects.requireNonNull(
        price,
        "CoursePrice cannot be null");

    this.startDate = Objects.requireNonNull(
        startDate,
        "Start date cannot be null");

    this.endDate = Objects.requireNonNull(
        endDate,
        "End date cannot be null");

    this.startTime = Objects.requireNonNull(
        startTime,
        "Start time cannot be null");

    this.durationHours = Objects.requireNonNull(
        durationHours,
        "Course duration cannot be null");

    this.capacity = Objects.requireNonNull(
        capacity,
        "Course capacity cannot be null");

    this.availableSlots = availableSlots != null
        ? availableSlots
        : AvailableSlots.of(
            capacity.value());

    this.categoryId = Objects.requireNonNull(
        categoryId,
        "CourseCategoryId cannot be null");

    this.status = status != null
        ? status
        : CourseStatus.DRAFT;

    validatePeriod();

    validateCapacity();

    markAsCreated();
    isNew();
    registerEvent(

        new CourseCreatedEvent(

            id.value(),

            code.value(),

            name.value(),

            description != null
                ? description.value()
                : null,

            modality.name(),

            price.amount(),

            price.currency(),

            startDate,

            endDate,

            startTime,

            durationHours.value(),

            capacity.value(),

            this.availableSlots.value(),

            this.status.name(),

            getCreatedAt()

        ));
  }

  // =========================================================
  // FACTORY
  // =========================================================

  public static Course create(

      String code,

      String name,

      String description,

      CourseModality modality,

      BigDecimal price,

      String currency,

      LocalDate startDate,

      LocalDate endDate,

      LocalTime startTime,

      Integer durationHours,

      Integer capacity,

      CourseCategoryId categoryId,

      CourseStatus status

  ) {

    CourseCapacity courseCapacity = CourseCapacity.of(capacity);

    return new Course(

        CourseId.generate(),

        CourseCode.of(code),

        CourseName.of(name),

        description == null
            ? null
            : CourseDescription.of(description),

        modality,

        CoursePrice.of(
            price,
            currency),

        startDate,

        endDate,

        startTime,

        CourseDuration.of(
            durationHours),

        courseCapacity,

        AvailableSlots.of(
            courseCapacity.value()),

        categoryId,

        status

    );
  }

  // =========================================================
  // REHYDRATE
  // =========================================================
  public static Course rehydrate(

      CourseId id,
      CourseCode code,
      CourseName name,
      CourseDescription description,
      CourseModality modality,
      CoursePrice price,
      LocalDate startDate,
      LocalDate endDate,
      LocalTime startTime,
      CourseDuration durationHours,
      CourseCapacity capacity,
      AvailableSlots availableSlots,
      CourseCategoryId categoryId,
      CourseStatus status,
      Instant createdAt,
      Instant updatedAt,
      Long version,
      Boolean deleted

  ) {

    Course course = new Course();

    course.id = id;
    course.code = code;
    course.name = name;
    course.description = description;
    course.modality = modality;
    course.price = price;
    course.startDate = startDate;
    course.endDate = endDate;
    course.startTime = startTime;
    course.durationHours = durationHours;
    course.capacity = capacity;
    course.availableSlots = availableSlots;
    course.categoryId = categoryId;
    course.status = status;
    course.createdAt = createdAt;
    course.updatedAt = updatedAt;
    course.version = version;
    course.deleted = Boolean.TRUE.equals(deleted);

    course.markAsPersisted();
    return course;
  }
  // =========================================================
  // DOMAIN BEHAVIOR
  // =========================================================

  public void changeName(
      String value) {

    CourseName newName = CourseName.of(value);

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

    CourseDescription newDescription = value == null
        ? null
        : CourseDescription.of(value);

    if (Objects.equals(
        this.description,
        newDescription)) {

      return;
    }

    this.description = newDescription;

    touch();
  }

  public void changePrice(

      BigDecimal amount,

      String currency

  ) {

    CoursePrice newPrice = CoursePrice.of(
        amount,
        currency);

    if (Objects.equals(
        this.price,
        newPrice)) {

      return;
    }

    this.price = newPrice;

    touch();
  }

  public void changeCategory(
      CourseCategoryId categoryId) {

    Objects.requireNonNull(
        categoryId,
        "CourseCategoryId cannot be null");

    if (Objects.equals(
        this.categoryId,
        categoryId)) {

      return;
    }

    this.categoryId = categoryId;

    touch();
  }

  public void reserveSlot() {

    if (availableSlots.value() <= 0) {

      throw new IllegalStateException(
          "Course has no available slots");
    }

    availableSlots = AvailableSlots.of(
        availableSlots.value() - 1);

    touch();
  }

  public void releaseSlot() {

    if (availableSlots.value() >= capacity.value()) {

      throw new IllegalStateException(
          "Available slots cannot exceed course capacity");
    }

    availableSlots = AvailableSlots.of(
        availableSlots.value() + 1);

    touch();
  }

  public void publish() {

    if (status == CourseStatus.PUBLISHED) {

      return;
    }

    validateForPublishing();

    this.status = CourseStatus.PUBLISHED;

    touch();

    registerEvent(

        new CoursePublishedEvent(

            id.value(),

            code.value(),

            name.value(),

            Instant.now()

        ));
  }

  public void cancel() {

    if (status == CourseStatus.CANCELLED) {

      return;
    }

    if (status == CourseStatus.COMPLETED) {

      throw new IllegalStateException(
          "Completed course cannot be cancelled");
    }

    this.status = CourseStatus.CANCELLED;

    touch();

    registerEvent(

        new CourseCancelledEvent(

            id.value(),

            Instant.now()

        ));
  }

  public void complete() {

    if (status != CourseStatus.PUBLISHED) {

      throw new IllegalStateException(
          "Only published courses can be completed");
    }

    this.status = CourseStatus.COMPLETED;

    touch();
  }

  // =========================================================
  // VALIDATIONS
  // =========================================================

  private void validatePeriod() {

    if (endDate.isBefore(startDate)) {

      throw new IllegalArgumentException(
          "Course end date cannot be before start date");
    }
  }

  private void validateCapacity() {

    if (availableSlots.value() > capacity.value()) {

      throw new IllegalArgumentException(
          "Available slots cannot exceed capacity");
    }
  }

  private void validateForPublishing() {

    if (name == null) {

      throw new IllegalStateException(
          "Course requires a name");
    }

    if (price == null) {

      throw new IllegalStateException(
          "Course requires a price");
    }

    if (capacity.value() <= 0) {

      throw new IllegalStateException(
          "Course requires a valid capacity");
    }

    if (startDate == null ||
        endDate == null) {

      throw new IllegalStateException(
          "Course requires a valid period");
    }

    if (categoryId == null) {

      throw new IllegalStateException(
          "Course requires a category");
    }
  }

  // =========================================================
  // GETTERS
  // =========================================================

  public CourseCode getCode() {
    return code;
  }

  public String getName() {
    return name.value();
  }

  public String getDescription() {
    return description != null
        ? description.value()
        : null;
  }

  public CourseModality getModality() {
    return modality;
  }

  public CoursePrice getPrice() {
    return price;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public CourseDuration getDurationHours() {
    return durationHours;
  }

  public CourseCapacity getCapacity() {
    return capacity;
  }

  public AvailableSlots getAvailableSlots() {
    return availableSlots;
  }

  public CourseCategoryId getCategoryId() {
    return categoryId;
  }

  public CourseStatus getStatus() {
    return status;
  }
}
