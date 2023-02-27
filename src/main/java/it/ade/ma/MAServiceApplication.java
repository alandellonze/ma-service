package it.ade.ma;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class MAServiceApplication {

    public static void main(String[] args) {
        run(MAServiceApplication.class, args);
    }

}
