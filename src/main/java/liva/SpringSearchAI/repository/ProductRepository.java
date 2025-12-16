package liva.SpringSearchAI.repository;


import liva.SpringSearchAI.model.ProductReadModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<ProductReadModel, String> {
}