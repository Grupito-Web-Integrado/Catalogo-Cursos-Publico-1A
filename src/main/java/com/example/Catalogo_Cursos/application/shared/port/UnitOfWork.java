package com.example.Catalogo_Cursos.application.shared.port;

public interface UnitOfWork {

  void commit();

  void rollback();
}
