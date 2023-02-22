package com.comradegenrr.moviehubback;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
public class MoviehubbackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviehubbackApplication.class, args);
    }

}
