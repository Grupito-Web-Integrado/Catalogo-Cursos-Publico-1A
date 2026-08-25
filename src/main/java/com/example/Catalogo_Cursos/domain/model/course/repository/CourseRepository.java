package com.example.Catalogo_Cursos.domain.model.course.repository;

import com.example.Catalogo_Cursos.domain.model.course.Course;
import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseRepository {

  Mono<Course> findById(CourseId id);

  Mono<Course> save(Course course);

  Mono<Void> delete(CourseId id);

  Flux<Course> findAll();
}
