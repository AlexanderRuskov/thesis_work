package market_projekt.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who gives the rating
    @ManyToOne
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater;

    // The user who receives the rating
    @ManyToOne
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser;

    private int score; // e.g., 1–5 stars

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Rating() {}

    public Rating(User rater, User ratedUser, int score, String comment) {
        this.rater = rater;
        this.ratedUser = ratedUser;
        this.score = score;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRater() {
        return rater;
    }

    public void setRater(User rater) {
        this.rater = rater;
    }

    public User getRatedUser() {
        return ratedUser;
    }

    public void setRatedUser(User ratedUser) {
        this.ratedUser = ratedUser;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
