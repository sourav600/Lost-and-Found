package com.moinul.LostAndFound.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.apachecommons.CommonsLog;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="missingPerson")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long createdUserId;
    @Column(name="Name", nullable = false)
    private String fullName;
    @Column(name="Age", nullable = false)
    private Integer age;
    @Column(name="Gender", nullable = false)
    private String gender;
    @Column(name="Height")
    private String height;
    @Column(name="Weight")
    private Integer weight;
    @Column(name="Hair color")
    private String hairColor;
    @Column(name="Eye color")
    private String eyeColor;
    @Column(name="Missing date")
    private String missingDate;
    @Column(name="Description")
    private String description;
    @Column(name="Photo")
    private String photo;
    @Column(name="Mobile No.", nullable = false)
    private Long contact;
    @Column(name="Address")
    private String address;
    private PersonStatus status;
}
