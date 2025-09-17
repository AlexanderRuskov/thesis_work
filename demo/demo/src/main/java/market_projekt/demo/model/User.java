package market_projekt.demo.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String phoneNumber;

    // One user can receive many ratings
    @OneToMany(mappedBy = "ratedUser", cascade = CascadeType.ALL)
    private List<Rating> ratingsReceived;

    // One user can give many ratings
    @OneToMany(mappedBy = "rater", cascade = CascadeType.ALL)
    private List<Rating> ratingsGiven;


    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() {return phoneNumber;}

    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
}
