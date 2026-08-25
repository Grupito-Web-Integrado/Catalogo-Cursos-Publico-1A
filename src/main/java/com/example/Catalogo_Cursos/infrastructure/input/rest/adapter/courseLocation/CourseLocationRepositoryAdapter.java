package com.example.Catalogo_Cursos.infrastructure.input.rest.adapter.courseLocation;

import com.example.Catalogo_Cursos.domain.model.courseLocations.CourseLocation;
import com.example.Catalogo_Cursos.domain.model.courseLocations.CourseLocationId;
import com.example.Catalogo_Cursos.domain.model.courseLocations.repository.CourseLocationRepository;
import com.example.Catalogo_Cursos.infrastructure.input.rest.mapper.CourseLocationPersistenceMapper;
import com.example.Catalogo_Cursos.infrastructure.input.rest.repository.SpringDataCourseLocationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CourseLocationRepositoryAdapter
    implements CourseLocationRepository {

  private final SpringDataCourseLocationRepository repository;
  private final CourseLocationPersistenceMapper mapper;

  public CourseLocationRepositoryAdapter(
      SpringDataCourseLocationRepository repository,
      CourseLocationPersistenceMapper mapper) {

    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Mono<CourseLocation> findById(CourseLocationId id) {
    return repository
        .findById(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public Mono<CourseLocation> save(CourseLocation location) {
    return repository
        .save(mapper.toEntity(location))
        .map(mapper::toDomain);
  }

  @Override
  public Mono<Void> delete(CourseLocationId id) {
    return repository.deleteById(id.value());
  }

  @Override
  public Flux<CourseLocation> findAll() {
    return repository
        .findAll()
        .map(mapper::toDomain);
  }
}
