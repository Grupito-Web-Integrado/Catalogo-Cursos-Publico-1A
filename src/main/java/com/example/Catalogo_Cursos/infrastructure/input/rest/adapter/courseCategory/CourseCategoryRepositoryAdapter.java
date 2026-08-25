package com.example.Catalogo_Cursos.infrastructure.input.rest.adapter.courseCategory;

import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategory;
import com.example.Catalogo_Cursos.domain.model.courseCategories.CourseCategoryId;
import com.example.Catalogo_Cursos.domain.model.courseCategories.repository.CourseCategoryRepository;
import com.example.Catalogo_Cursos.infrastructure.input.rest.mapper.CourseCategoryPersistenceMapper;
import com.example.Catalogo_Cursos.infrastructure.input.rest.repository.SpringDataCourseCategoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CourseCategoryRepositoryAdapter
    implements CourseCategoryRepository {

  private final SpringDataCourseCategoryRepository repository;
  private final CourseCategoryPersistenceMapper mapper;

  public CourseCategoryRepositoryAdapter(
      SpringDataCourseCategoryRepository repository,
      CourseCategoryPersistenceMapper mapper) {

    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Mono<CourseCategory> findById(CourseCategoryId id) {
    return repository
        .findById(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public Mono<CourseCategory> save(CourseCategory category) {
    return repository
        .save(mapper.toEntity(category))
        .map(mapper::toDomain);
  }

  @Override
  public Mono<Void> delete(CourseCategoryId id) {
    return repository.deleteById(id.value());
  }

  @Override
  public Flux<CourseCategory> findAll() {
    return repository
        .findAll()
        .map(mapper::toDomain);
  }
}
