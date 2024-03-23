package io.s7i.temp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TempApplication {

    public static final String ALG_CFG = "app.anomaly.algName";
    public static final String ALG_NAME_A = "alg1";
    public static final String ALG_NAME_B = "alg2";

    public static void main(String[] args) {
        SpringApplication.run(TempApplication.class, args);
    }

}
