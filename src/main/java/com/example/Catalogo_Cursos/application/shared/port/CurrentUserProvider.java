package com.example.Catalogo_Cursos.application.shared.port;

import java.util.Optional;

public interface CurrentUserProvider {

  Optional<String> currentUser();
}
