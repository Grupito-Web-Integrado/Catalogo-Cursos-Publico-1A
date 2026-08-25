package com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository;

import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryDto;
import com.example.Catalogo_Cursos.application.dto.courseCategory.CourseCategoryStatisticsDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CourseCategoryElasticsearchRepository {

  // =========================================================
  // SINGLE LOOKUPS
  // =========================================================

  /**
   * Busca una categoría por su courseCategoryId
   * dentro del read model de Elasticsearch.
   */
  Mono<CourseCategoryDto> findById(
      UUID courseCategoryId);

  /**
   * Busca una categoría por nombre.
   */
  Mono<CourseCategoryDto> findByName(
      String name);

  // =========================================================
  // SEARCH
  // =========================================================

  /**
   * Búsqueda general por texto.
   */
  Mono<PageResult<CourseCategoryDto>> search(
      String text,
      int page,
      int size);

  /**
   * Búsqueda utilizando criterios específicos.
   */
  Mono<PageResult<CourseCategoryDto>> searchByCriteria(
      String name,
      String status,
      int page,
      int size);

  // =========================================================
  // STATUS
  // =========================================================

  /**
   * Obtiene categorías filtradas por estado.
   */
  Mono<PageResult<CourseCategoryDto>> findByStatus(
      String status,
      int page,
      int size);

  // =========================================================
  // COURSE RELATION
  // =========================================================

  /**
   * Obtiene las categorías asociadas a un curso.
   */
  Mono<PageResult<CourseCategoryDto>> findByCourseId(
      UUID courseId,
      int page,
      int size);

  /**
   * Obtiene todas las categorías asociadas a un curso.
   */
  Flux<CourseCategoryDto> findAllByCourseId(
      UUID courseId);

  // =========================================================
  // CATEGORIES WITH COURSES
  // =========================================================

  /**
   * Cuenta los cursos asociados a una categoría
   * utilizando el read model de Elasticsearch.
   */
  Mono<Long> countCoursesByCategoryId(
      UUID courseCategoryId);

  // =========================================================
  // ORDERING
  // =========================================================

  /**
   * Obtiene categorías ordenadas por nombre.
   */
  Mono<PageResult<CourseCategoryDto>> findOrderedByName(
      boolean ascending,
      int page,
      int size);

  // =========================================================
  // AUTOCOMPLETE
  // =========================================================

  /**
   * Autocompletado de categorías por nombre.
   */
  Mono<List<CourseCategoryDto>> autocomplete(
      String text,
      int limit);

  // =========================================================
  // STATISTICS
  // =========================================================

  /**
   * Cuenta todas las categorías existentes
   * en el read model.
   */
  Mono<Long> count();

  /**
   * Cuenta las categorías activas.
   */
  Mono<Long> countActive();

  /**
   * Obtiene estadísticas de categorías.
   */
  Mono<CourseCategoryStatisticsDto> statistics();

  // =========================================================
  // ADMIN / READ MODEL
  // =========================================================

  /**
   * Obtiene todas las categorías paginadas.
   */
  Mono<PageResult<CourseCategoryDto>> findAll(
      int page,
      int size);
}
