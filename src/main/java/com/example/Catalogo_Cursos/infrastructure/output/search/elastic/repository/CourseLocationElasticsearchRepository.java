package com.example.Catalogo_Cursos.infrastructure.output.search.elastic.repository;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationStatisticsDto;
import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationSuggestionDto;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CourseLocationElasticsearchRepository {

  Mono<CourseLocationDto> findById(
      UUID locationId);

  Mono<CourseLocationDto> findByCourseId(
      UUID courseId);

  Mono<PageResult<CourseLocationDto>> findAll(
      int page,
      int size);

  Mono<PageResult<CourseLocationDto>> search(
      String text,
      int page,
      int size);

  Mono<PageResult<CourseLocationDto>> searchByCriteria(
      String name,
      String city,
      String address,
      UUID courseId,
      int page,
      int size);

  Mono<PageResult<CourseLocationDto>> findByCity(
      String city,
      int page,
      int size);

  Mono<Long> count();

  Mono<CourseLocationStatisticsDto> statistics();

  Mono<List<CourseLocationSuggestionDto>> autocomplete(
      String text,
      int limit);

  Mono<Void> index(
      CourseLocationDto location);

  Mono<Void> bulkIndex(
      List<CourseLocationDto> locations);

  Mono<Void> deleteFromIndex(
      UUID locationId);
}
