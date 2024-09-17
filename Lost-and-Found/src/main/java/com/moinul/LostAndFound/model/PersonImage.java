package com.moinul.LostAndFound.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="PersonImage64")
public class PersonImage {
    @Id
    private Long id;
    @Column(name="Name", nullable = false)
    String fullName;
    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imageBase64;

}
