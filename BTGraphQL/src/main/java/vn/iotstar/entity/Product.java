package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "product")
@Data
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Integer quantity;

    @Column(name = "desc", length = 255)
    private String desc;

    private Double price;

    // owner user (many products -> one user)
    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    // product belongs to one category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // getters/setters
}
