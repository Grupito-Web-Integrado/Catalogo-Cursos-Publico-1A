package com.example.Catalogo_Cursos.domain.model.course.event;

import com.example.Catalogo_Cursos.domain.model.shared.event.BaseDomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class CourseCreatedEvent
    extends BaseDomainEvent {

  private final UUID courseId;

  private final String code;

  private final String name;

  private final String description;

  private final String modality;

  private final BigDecimal price;

  private final String currency;

  private final LocalDate startDate;

  private final LocalDate endDate;

  private final LocalTime startTime;

  private final Integer durationHours;

  private final Integer capacity;

  private final Integer availableSlots;

  private final String status;

  private final Instant createdAt;

  public CourseCreatedEvent(

      UUID courseId,

      String code,

      String name,

      String description,

      String modality,

      BigDecimal price,

      String currency,

      LocalDate startDate,

      LocalDate endDate,

      LocalTime startTime,

      Integer durationHours,

      Integer capacity,

      Integer availableSlots,

      String status,

      Instant createdAt

  ) {

    this.courseId = courseId;

    this.code = code;

    this.name = name;

    this.description = description;

    this.modality = modality;

    this.price = price;

    this.currency = currency;

    this.startDate = startDate;

    this.endDate = endDate;

    this.startTime = startTime;

    this.durationHours = durationHours;

    this.capacity = capacity;

    this.availableSlots = availableSlots;

    this.status = status;

    this.createdAt = createdAt;
  }

  @Override
  public UUID aggregateId() {

    return courseId;
  }

  @Override
  public String aggregateType() {

    return "Course";
  }

  @Override
  public String eventType() {

    return CourseEventType.COURSE_CREATED
        .name();
  }

  @Override
  public int eventVersion() {

    return 1;
  }

  public UUID getCourseId() {

    return courseId;
  }

  public String getCode() {

    return code;
  }

  public String getName() {

    return name;
  }

  public String getDescription() {

    return description;
  }

  public String getModality() {

    return modality;
  }

  public BigDecimal getPrice() {

    return price;
  }

  public String getCurrency() {

    return currency;
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

  public Integer getDurationHours() {

    return durationHours;
  }

  public Integer getCapacity() {

    return capacity;
  }

  public Integer getAvailableSlots() {

    return availableSlots;
  }

  public String getStatus() {

    return status;
  }

  public Instant getCreatedAt() {

    return createdAt;
  }
}
