package com.example.Catalogo_Cursos.domain.model.courseLocations;

import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import com.example.Catalogo_Cursos.domain.model.courseLocations.event.CourseLocationAddressChangedEvent;
import com.example.Catalogo_Cursos.domain.model.courseLocations.event.CourseLocationCapacityChangedEvent;
import com.example.Catalogo_Cursos.domain.model.courseLocations.event.CourseLocationCityChangedEvent;
import com.example.Catalogo_Cursos.domain.model.courseLocations.event.CourseLocationCreatedEvent;
import com.example.Catalogo_Cursos.domain.model.courseLocations.event.CourseLocationNameChangedEvent;
import com.example.Catalogo_Cursos.domain.model.courseLocations.event.CourseLocationReferenceChangedEvent;
import com.example.Catalogo_Cursos.domain.model.shared.aggregate.AuditableAggregateRoot;

import java.time.Instant;
import java.util.Objects;

public class CourseLocation
    extends AuditableAggregateRoot<CourseLocationId> {

  // =========================================================
  // RELATION
  // =========================================================

  private CourseId courseId;

  // =========================================================
  // LOCATION
  // =========================================================

  private LocationName name;

  private LocationAddress address;

  private LocationCity city;

  private LocationReference reference;

  // =========================================================
  // CAPACITY
  // =========================================================

  private LocationCapacity capacity;

  // =========================================================
  // ORM
  // =========================================================

  protected CourseLocation() {
  }

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  private CourseLocation(

      CourseLocationId id,

      CourseId courseId,

      LocationName name,

      LocationAddress address,

      LocationCity city,

      LocationReference reference,

      LocationCapacity capacity

  ) {

    this.id = Objects.requireNonNull(
        id,
        "CourseLocationId cannot be null");

    this.courseId = Objects.requireNonNull(
        courseId,
        "CourseId cannot be null");

    this.name = Objects.requireNonNull(
        name,
        "LocationName cannot be null");

    this.address = Objects.requireNonNull(
        address,
        "LocationAddress cannot be null");

    this.city = Objects.requireNonNull(
        city,
        "LocationCity cannot be null");

    this.reference = reference;

    this.capacity = Objects.requireNonNull(
        capacity,
        "LocationCapacity cannot be null");

    // =========================================================
    // AUDIT
    // =========================================================

    markAsCreated();

    isNew();
    // =========================================================
    // DOMAIN EVENT
    // =========================================================

    registerEvent(

        new CourseLocationCreatedEvent(

            id.value(),

            courseId.value(),

            name.value(),

            address.value(),

            city.value(),

            reference != null
                ? reference.value()
                : null,

            capacity.value(),

            getCreatedAt()

        ));
  }

  // =========================================================
  // FACTORY
  // =========================================================

  public static CourseLocation create(

      CourseId courseId,

      String name,

      String address,

      String city,

      String reference,

      Integer capacity

  ) {

    return new CourseLocation(

        CourseLocationId.generate(),

        courseId,

        LocationName.of(name),

        LocationAddress.of(address),

        LocationCity.of(city),

        reference == null
            ? null
            : LocationReference.of(reference),

        LocationCapacity.of(capacity)

    );
  }

  // =========================================================
  // REHYDRATE
  // =========================================================

  public static CourseLocation rehydrate(

      CourseLocationId id,

      CourseId courseId,

      LocationName name,

      LocationAddress address,

      LocationCity city,

      LocationReference reference,

      LocationCapacity capacity,

      Instant createdAt,

      Instant updatedAt,

      Long version,

      Boolean deleted

  ) {

    CourseLocation location = new CourseLocation();

    location.id = id;

    location.courseId = courseId;

    location.name = name;

    location.address = address;

    location.city = city;

    location.reference = reference;

    location.capacity = capacity;

    location.createdAt = createdAt;

    location.updatedAt = updatedAt;

    location.version = version;

    location.deleted = Boolean.TRUE.equals(
        deleted);

    location.markAsPersisted();
    return location;
  }

  // =========================================================
  // DOMAIN BEHAVIOR
  // =========================================================

  public void changeName(
      String value) {

    LocationName newName = LocationName.of(value);

    if (Objects.equals(
        this.name,
        newName)) {

      return;
    }

    this.name = newName;

    touch();

    registerEvent(

        new CourseLocationNameChangedEvent(

            id.value(),

            newName.value(),

            Instant.now()

        ));
  }

  public void changeAddress(
      String value) {

    LocationAddress newAddress = LocationAddress.of(value);

    if (Objects.equals(
        this.address,
        newAddress)) {

      return;
    }

    this.address = newAddress;

    touch();

    registerEvent(

        new CourseLocationAddressChangedEvent(

            id.value(),

            newAddress.value(),

            Instant.now()

        ));
  }

  public void changeCity(
      String value) {

    LocationCity newCity = LocationCity.of(value);

    if (Objects.equals(
        this.city,
        newCity)) {

      return;
    }

    this.city = newCity;

    touch();

    registerEvent(

        new CourseLocationCityChangedEvent(

            id.value(),

            newCity.value(),

            Instant.now()

        ));
  }

  public void changeReference(
      String value) {

    LocationReference newReference = value == null
        ? null
        : LocationReference.of(value);

    if (Objects.equals(
        this.reference,
        newReference)) {

      return;
    }

    this.reference = newReference;

    touch();

    registerEvent(

        new CourseLocationReferenceChangedEvent(

            id.value(),

            newReference != null
                ? newReference.value()
                : null,

            Instant.now()

        ));
  }

  public void changeCapacity(
      Integer value) {

    LocationCapacity newCapacity = LocationCapacity.of(value);

    if (Objects.equals(
        this.capacity,
        newCapacity)) {

      return;
    }

    this.capacity = newCapacity;

    touch();

    registerEvent(

        new CourseLocationCapacityChangedEvent(

            id.value(),

            newCapacity.value(),

            Instant.now()

        ));
  }

  // =========================================================
  // GETTERS
  // =========================================================

  public CourseLocationId getId() {

    return id;
  }

  public CourseId getCourseId() {

    return courseId;
  }

  public LocationName getName() {

    return name;
  }

  public LocationAddress getAddress() {

    return address;
  }

  public LocationCity getCity() {

    return city;
  }

  public LocationReference getReference() {

    return reference;
  }

  public LocationCapacity getCapacity() {

    return capacity;
  }
}
