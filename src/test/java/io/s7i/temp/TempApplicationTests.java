package io.s7i.temp;

import io.s7i.temp.config.AnomalyConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = AnomalyConfig.class,
        properties = {
                "app.anomaly.meanSize=9",
                "app.anomaly.deviationThreshold=5"
        })
class TempApplicationTests {

    @Autowired
    AnomalyConfig anomalyConfig;

    @Test
    void anomalyConfigTest() {

        assertNotNull(anomalyConfig);
        //assertEquals(9, anomalyConfig.getMeanSize());
    }
}
