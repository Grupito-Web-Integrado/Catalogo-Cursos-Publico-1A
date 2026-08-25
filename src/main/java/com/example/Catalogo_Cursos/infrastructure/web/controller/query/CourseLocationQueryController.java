package com.example.Catalogo_Cursos.infrastructure.web.controller.query;

import com.example.Catalogo_Cursos.application.dto.courseLocation.CourseLocationDto;
import com.example.Catalogo_Cursos.application.query.courseLocation.FindCourseLocationsByCityQuery;
import com.example.Catalogo_Cursos.application.query.courseLocation.GetCourseLocationByCourseQuery;
import com.example.Catalogo_Cursos.application.query.courseLocation.GetCourseLocationByIdQuery;
import com.example.Catalogo_Cursos.application.query.courseLocation.SearchCourseLocationsQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryBus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-locations")
public class CourseLocationQueryController {

  private final QueryBus queryBus;

  public CourseLocationQueryController(
      QueryBus queryBus) {

    this.queryBus = queryBus;
  }

  // =========================================================
  // GET BY ID
  // =========================================================

  @GetMapping("/{id}")
  public Mono<CourseLocationDto> findById(
      @PathVariable UUID id) {

    return queryBus.dispatch(
        new GetCourseLocationByIdQuery(id));
  }

  // =========================================================
  // SEARCH GENERAL
  // =========================================================

  @GetMapping
  public Mono<PageResult<CourseLocationDto>> search(
      @RequestParam String text,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new SearchCourseLocationsQuery(
            text,
            page,
            size));
  }

  // =========================================================
  // BY COURSE
  // =========================================================

  @GetMapping("/course/{courseId}")
  public Mono<PageResult<CourseLocationDto>> findByCourse(
      @PathVariable UUID courseId,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new GetCourseLocationByCourseQuery(
            courseId,
            page,
            size));
  }

  // =========================================================
  // BY CITY
  // =========================================================

  @GetMapping("/city/{city}")
  public Mono<PageResult<CourseLocationDto>> findByCity(
      @PathVariable String city,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new FindCourseLocationsByCityQuery(
            city,
            page,
            size));
  }
}
