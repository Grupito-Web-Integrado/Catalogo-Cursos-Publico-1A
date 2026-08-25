package com.example.Catalogo_Cursos.application.port.courseLocation;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CourseLocationReadRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  Mono<CourseLocationDto> findById(
      UUID id);

  // =========================================================
  // COURSE RELATION
  // =========================================================

  Mono<PageResult<CourseLocationDto>> findByCourseId(
      UUID courseId,
      int page,
      int size);

  Flux<CourseLocationDto> findAllByCourseId(
      UUID courseId);

  // =========================================================
  // LOCATION FILTERS
  // =========================================================

  Mono<PageResult<CourseLocationDto>> findByCity(
      String city,
      int page,
      int size);

  Mono<PageResult<CourseLocationDto>> findByName(
      String name,
      int page,
      int size);

  // =========================================================
  // SEARCH
  // =========================================================

  Mono<PageResult<CourseLocationDto>> search(
      String text,
      int page,
      int size);

  // =========================================================
  // COURSE + CITY
  // =========================================================

  Mono<PageResult<CourseLocationDto>> findByCourseAndCity(
      UUID courseId,
      String city,
      int page,
      int size);

  // =========================================================
  // ORDERING
  // =========================================================

  Mono<PageResult<CourseLocationDto>> findOrderedByCity(
      boolean ascending,
      int page,
      int size);

  // =========================================================
  // ADMIN
  // =========================================================

  Flux<CourseLocationDto> findAll();

  // =========================================================
  // STATISTICS
  // =========================================================

  Mono<Long> count();

  Mono<Long> countByCourseId(
      UUID courseId);
}
