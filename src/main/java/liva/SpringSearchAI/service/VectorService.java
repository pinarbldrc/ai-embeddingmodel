package liva.SpringSearchAI.service;
import liva.SpringSearchAI.model.ProductProperty;
import liva.SpringSearchAI.model.ProductReadModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VectorService {

    private final VectorStore productVectorStore;

    public VectorService(VectorStore productVectorStore) {
        this.productVectorStore = productVectorStore;
    }

    public Document createVector(ProductReadModel productReadModel){
        String category = productReadModel.getCategory();
        List<ProductProperty> properties = productReadModel.getProperties();
        String idProduct = productReadModel.getId();
        String name = productReadModel.getName();

        String propertyText = properties.stream()
                .map(p -> p.getKey() + ": " + p.getValue())
                .collect(Collectors.joining(", "));

        String content = String.format("Ürün Adı: %s. Kategori: %s. Özellikler: %s",
                name,
                category,
                propertyText);

        return null;
    }

    public void addVectordb(List<Document> documents){
        productVectorStore.add(documents);

    }
}
