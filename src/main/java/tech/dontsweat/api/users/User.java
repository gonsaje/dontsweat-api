package tech.dontsweat.api.users;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  private String firstName;
  private String lastName;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  protected User() {}

  public User(String email, String firstName, String lastName) {
    this.id = UUID.randomUUID();
    this.email = normalizeRequiredEmail(email);
    this.firstName = normalizeOptional(firstName);
    this.lastName = normalizeOptional(lastName);
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  public void changeEmail(String email) {
    this.email = normalizeRequiredEmail(email);
    touch();
  }

  public void rename(String firstName, String lastName) {
    this.firstName = normalizeOptional(firstName);
    this.lastName = normalizeOptional(lastName);
    touch();
  }

  private static String normalizeRequiredEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email is required");
    }

    return email.trim().toLowerCase();
  }

  private static String normalizeOptional(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    return value.trim();
  }

  private void touch() {
    this.updatedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
