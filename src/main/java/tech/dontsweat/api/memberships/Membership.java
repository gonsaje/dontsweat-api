package tech.dontsweat.api.memberships;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import tech.dontsweat.api.orgs.OrgRole;
import tech.dontsweat.api.orgs.Organization;
import tech.dontsweat.api.users.User;

@Entity
@Table(
    name = "memberships",
    uniqueConstraints = @UniqueConstraint(columnNames = {"org_id", "user_id", "role"}))
public class Membership {
  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "org_id", nullable = false)
  private Organization org;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrgRole role;

  @Column(nullable = false)
  private Instant createdAt;

  protected Membership() {}

  public Membership(User user, Organization org, OrgRole role) {
    this.id = UUID.randomUUID();
    this.user = Objects.requireNonNull(user);
    this.org = Objects.requireNonNull(org);
    this.role = Objects.requireNonNull(role);
    this.createdAt = Instant.now();
  }
}
