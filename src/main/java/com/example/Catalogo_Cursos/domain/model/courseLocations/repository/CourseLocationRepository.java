package com.example.Catalogo_Cursos.domain.model.courseLocations.repository;

import com.example.Catalogo_Cursos.domain.model.courseLocations.CourseLocation;
import com.example.Catalogo_Cursos.domain.model.courseLocations.CourseLocationId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseLocationRepository {

  Mono<CourseLocation> findById(CourseLocationId id);

  Mono<CourseLocation> save(CourseLocation location);

  Mono<Void> delete(CourseLocationId id);

  Flux<CourseLocation> findAll();
}
