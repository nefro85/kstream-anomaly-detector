package io.s7i.temp.api.data;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface CrudRepository extends MongoRepository<Anomaly, String> {
}
