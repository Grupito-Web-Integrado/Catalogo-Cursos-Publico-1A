package com.example.Catalogo_Cursos.infrastructure.web.controller.query;

import com.example.Catalogo_Cursos.application.dto.courseSchedule.CourseScheduleDto;
import com.example.Catalogo_Cursos.application.query.courseSchedule.GetCourseScheduleByCourseAndDayQuery;
import com.example.Catalogo_Cursos.application.query.courseSchedule.GetCourseScheduleByIdQuery;
import com.example.Catalogo_Cursos.application.query.courseSchedule.GetCourseSchedulesByCourseQuery;
import com.example.Catalogo_Cursos.application.query.courseSchedule.GetCourseSchedulesByDayQuery;
import com.example.Catalogo_Cursos.application.shared.dto.PageResult;
import com.example.Catalogo_Cursos.application.shared.query.QueryBus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-schedules")
public class CourseScheduleQueryController {

  private final QueryBus queryBus;

  public CourseScheduleQueryController(
      QueryBus queryBus) {

    this.queryBus = queryBus;
  }

  // =========================================================
  // GET BY ID
  // =========================================================

  @GetMapping("/{id}")
  public Mono<CourseScheduleDto> findById(
      @PathVariable UUID id) {

    return queryBus.dispatch(
        new GetCourseScheduleByIdQuery(id));
  }

  // =========================================================
  // GET BY COURSE
  // =========================================================

  @GetMapping("/course/{courseId}")
  public Mono<PageResult<CourseScheduleDto>> findByCourse(
      @PathVariable UUID courseId,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new GetCourseSchedulesByCourseQuery(
            courseId,
            page,
            size));
  }

  // =========================================================
  // GET BY DAY
  // =========================================================
  // =========================================================
  // GET BY DAY
  // =========================================================

  @GetMapping("/day/{dayOfWeek}")
  public Mono<PageResult<CourseScheduleDto>> findByDay(
      @PathVariable DayOfWeek dayOfWeek,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new GetCourseSchedulesByDayQuery(
            dayOfWeek,
            page,
            size));
  }

  // =========================================================
  // GET BY COURSE + DAY
  // =========================================================
  // =========================================================
  // GET BY COURSE + DAY
  // =========================================================

  @GetMapping("/course/{courseId}/day/{dayOfWeek}")
  public Mono<PageResult<CourseScheduleDto>> findByCourseAndDay(
      @PathVariable UUID courseId,
      @PathVariable DayOfWeek dayOfWeek,
      @RequestParam Integer page,
      @RequestParam Integer size) {

    return queryBus.dispatch(
        new GetCourseScheduleByCourseAndDayQuery(
            courseId,
            dayOfWeek,
            page,
            size));
  }
}
