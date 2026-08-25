package com.example.Catalogo_Cursos.application.shared.port;

import java.time.Instant;

public interface ClockProvider {

  Instant now();
}
