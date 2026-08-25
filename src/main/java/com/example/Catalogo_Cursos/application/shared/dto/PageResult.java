package com.example.Catalogo_Cursos.application.shared.dto;

import java.util.List;

public record PageResult<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int page,
    int size) {

  public static <T> PageResult<T> of(
      List<T> content,
      long totalElements,
      int page,
      int size) {

    int totalPages = size == 0
        ? 0
        : (int) Math.ceil(
            (double) totalElements / size);

    return new PageResult<>(
        content,
        totalElements,
        totalPages,
        page,
        size);
  }
}
