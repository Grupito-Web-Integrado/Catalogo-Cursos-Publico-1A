package com.example.Catalogo_Cursos.infrastructure.input.rest.adapter.course;

import com.example.Catalogo_Cursos.domain.model.course.Course;
import com.example.Catalogo_Cursos.domain.model.course.CourseId;
import com.example.Catalogo_Cursos.domain.model.course.repository.CourseRepository;
import com.example.Catalogo_Cursos.infrastructure.input.rest.mapper.CoursePersistenceMapper;
import com.example.Catalogo_Cursos.infrastructure.input.rest.repository.SpringDataCourseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CourseRepositoryAdapter
    implements CourseRepository {

  private final SpringDataCourseRepository repository;

  private final CoursePersistenceMapper mapper;

  public CourseRepositoryAdapter(
      SpringDataCourseRepository repository,
      CoursePersistenceMapper mapper) {

    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Mono<Course> findById(
      CourseId id) {

    return repository
        .findById(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public Mono<Course> save(
      Course course) {

    return repository
        .save(mapper.toEntity(course))
        .map(mapper::toDomain);
  }

  @Override
  public Mono<Void> delete(
      CourseId id) {

    return repository.deleteById(
        id.value());
  }

  @Override
  public Flux<Course> findAll() {

    return repository
        .findAll()
        .map(mapper::toDomain);
  }
}
