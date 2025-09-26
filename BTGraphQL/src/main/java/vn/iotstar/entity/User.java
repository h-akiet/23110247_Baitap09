package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullname;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;

    // optional: users favorite categories (many-to-many)
    @ManyToMany
    @JoinTable(name = "user_category",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> favoriteCategories;

    // getters/setters (omitted for brevity)
}