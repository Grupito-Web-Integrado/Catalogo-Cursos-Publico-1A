package com.example.Catalogo_Cursos.application.port.courseCategory;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CourseCategoryReadRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  Mono<CourseCategoryDto> findById(
      UUID id);

  Mono<CourseCategoryDto> findByName(
      String name);

  // =========================================================
  // SEARCH
  // =========================================================

  Mono<PageResult<CourseCategoryDto>> search(
      String text,
      int page,
      int size);

  // =========================================================
  // STATUS
  // =========================================================

  Mono<PageResult<CourseCategoryDto>> findByStatus(
      String status,
      int page,
      int size);

  Mono<PageResult<CourseCategoryDto>> findActive(
      int page,
      int size);

  Mono<PageResult<CourseCategoryDto>> findInactive(
      int page,
      int size);

  // =========================================================
  // COURSE RELATION
  // =========================================================

  Mono<PageResult<CourseCategoryDto>> findByCourseId(
      UUID courseId,
      int page,
      int size);

  Flux<CourseCategoryDto> findAllByCourseId(
      UUID courseId);

  // =========================================================
  // CATEGORIES WITH COURSES
  // =========================================================

  Mono<Long> countCoursesByCategoryId(
      UUID categoryId);

  // =========================================================
  // ORDERING
  // =========================================================

  Mono<PageResult<CourseCategoryDto>> findOrderedByName(
      boolean ascending,
      int page,
      int size);

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  Mono<java.util.List<CourseCategoryDto>> autocomplete(
      String text,
      int limit);

  // =========================================================
  // STATISTICS
  // =========================================================

  Mono<Long> count();

  Mono<Long> countActive();

  // =========================================================
  // ADMIN
  // =========================================================

  Mono<PageResult<CourseCategoryDto>> findAll(
      int page,
      int size);
}
