package io.s7i.temp.api.data;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "anomaly")
public interface AnomalyRepository extends Repository<Anomaly, String> {


    List<Anomaly> findByRoomId(String roomId);

    List<Anomaly> findByThermometerId(String thermometerId);
}
