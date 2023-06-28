package com.comradegenrr.moviehubback.standerio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StanderUserInfo {

    private Object credentials;
    private Object authorities;
    private Object details;
    private Object principal;
}
