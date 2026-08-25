package com.example.Catalogo_Cursos.infrastructure.input.rest.adapter.courseSchedule;

import org.springframework.stereotype.Service;

import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseSchedule;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.CourseScheduleId;
import com.example.Catalogo_Cursos.domain.model.courseSchedules.repository.CourseScheduleRepository;
import com.example.Catalogo_Cursos.infrastructure.input.rest.mapper.CourseSchedulePersistenceMapper;
import com.example.Catalogo_Cursos.infrastructure.input.rest.repository.SpringDataCourseScheduleRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CourseScheduleRepositoryAdapter
    implements CourseScheduleRepository {

  private final SpringDataCourseScheduleRepository repository;

  public CourseScheduleRepositoryAdapter(
      SpringDataCourseScheduleRepository repository) {

    this.repository = repository;
  }

  @Override
  public Mono<CourseSchedule> findById(
      CourseScheduleId id) {

    return repository
        .findById(id.value())
        .map(CourseSchedulePersistenceMapper::toDomain);
  }

  @Override
  public Mono<CourseSchedule> save(
      CourseSchedule schedule) {

    return repository
        .save(
            CourseSchedulePersistenceMapper.toEntity(
                schedule))
        .map(
            CourseSchedulePersistenceMapper::toDomain);
  }

  @Override
  public Mono<Void> delete(
      CourseScheduleId id) {

    return repository.deleteById(
        id.value());
  }

  @Override
  public Flux<CourseSchedule> findAll() {

    return repository
        .findAll()
        .map(
            CourseSchedulePersistenceMapper::toDomain);
  }
}
