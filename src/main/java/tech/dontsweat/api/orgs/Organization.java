package tech.dontsweat.api.orgs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class Organization {
  @Id private UUID id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  protected Organization() {}

  public Organization(String name) {
    this.id = UUID.randomUUID();
    this.name = normalizeName(name);
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  public void rename(String name) {
    this.name = normalizeName(name);
    touch();
  }

  private static String normalizeName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Organization name is required");
    }

    return name.trim();
  }

  private void touch() {
    this.updatedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
