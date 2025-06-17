package ru.practicum.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Setter @Getter @ToString
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    private String name;
    @Column(name = "registration_date")
    private Instant registrationDate = Instant.now();
    @Enumerated
    private UserState state;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
