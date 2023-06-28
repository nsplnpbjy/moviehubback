package com.comradegenrr.moviehubback.standerio;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserForLogin {
    
    private String username;
    private String password;

}
