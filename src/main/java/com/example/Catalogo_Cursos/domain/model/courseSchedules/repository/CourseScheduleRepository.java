package com.example.Catalogo_Cursos.domain.model.courseSchedules.repository;

import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseSchedule;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseScheduleId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseScheduleRepository {

  Mono<CourseSchedule> findById(CourseScheduleId id);

  Mono<CourseSchedule> save(CourseSchedule schedule);

  Mono<Void> delete(CourseScheduleId id);

  Flux<CourseSchedule> findAll();
}
