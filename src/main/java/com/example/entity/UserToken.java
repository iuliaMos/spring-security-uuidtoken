package com.example.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq")
    @SequenceGenerator(name = "token_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    private String uuid;

    private Boolean active;
}
