package com.moinul.LostAndFound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private  Long id;

    private  String password;
    private String phoneNo;
    private String fullName;
}
