package com.example.Catalogo_Cursos.domain.model.courseCategories.repository;

import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategory;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseCategoryRepository {

  Mono<CourseCategory> findById(CourseCategoryId id);

  Mono<CourseCategory> save(CourseCategory category);

  Mono<Void> delete(CourseCategoryId id);

  Flux<CourseCategory> findAll();
}
