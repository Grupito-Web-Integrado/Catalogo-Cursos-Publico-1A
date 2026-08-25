package com.example.Catalogo_Cursos.domain.model.shared.valueobject;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record Slug(String value) {

  private static final Pattern NON_ALPHANUM = Pattern.compile("[^a-z0-9]+");
  private static final Pattern TRIM_DASHES = Pattern.compile("^-+|-+$");

  public Slug {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Slug cannot be empty");
    }

    value = normalize(value);
  }

  public static Slug from(String text) {
    return new Slug(generate(text));
  }

  public static Slug fromTitle(String title) {
    return from(title);
  }

  private static String generate(String input) {

    if (input == null || input.isBlank()) {
      throw new IllegalArgumentException("Slug source cannot be empty");
    }

    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
        .toLowerCase(Locale.ROOT)
        .trim();

    String slug = NON_ALPHANUM.matcher(normalized).replaceAll("-");
    slug = TRIM_DASHES.matcher(slug).replaceAll("");

    return slug;
  }

  private static String normalize(String value) {
    return generate(value);
  }

  public Slug withSuffix(long version) {
    return new Slug(this.value + "-" + version);
  }

  @Override
  public String toString() {
    return value;
  }
}
