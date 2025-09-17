package market_projekt.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories") // 👈 Make sure this matches your DB table name exactly
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    //  Constructors
    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    //  Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //  Optional: nice toString for debugging
    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
