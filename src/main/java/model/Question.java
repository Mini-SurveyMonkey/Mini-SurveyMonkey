package model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String questionText;
    private String type;

    @ElementCollection
    private List<String> options;

    // Add Getters and Setters
}
