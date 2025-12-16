package liva.SpringSearchAI.service;

import liva.SpringSearchAI.model.ProductReadModel;
import liva.SpringSearchAI.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    // Tüm ürünleri listeleme (API endpoint'i için kullanılacak)
    public List<ProductReadModel> getAllProducts() {
        return repository.findAll();
    }


    public ProductReadModel getById(String id) {
        return repository.findById(id).get();
    }

}
