package model;

import jakarta.persistence.*;

@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Survey survey;

    private String answerText; // For numeric: store as string and parse as needed

    // Getters, setters omitted
}

