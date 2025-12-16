package liva.SpringSearchAI.service;

import liva.SpringSearchAI.dto.ProductResponseDto;
import liva.SpringSearchAI.dto.ProductSearchParameters;
import liva.SpringSearchAI.model.ProductProperty;
import liva.SpringSearchAI.model.ProductReadModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final ChatModel chatModel;
    private final ProductService productService;

    private final VectorService vectorService;

    private final VectorStore productVectorStore;


    public SearchService(ChatModel chatModel, VectorStore productVectorStore, ProductService productService, VectorService vectorService) {
        this.chatModel = chatModel;
        this.productService = productService;
        this.vectorService = vectorService;
        this.productVectorStore = productVectorStore;
    }

    public List<ProductSearchParameters> getProductByParametrePrompt(String userPrompt) {

        BeanOutputConverter<List<ProductSearchParameters>> beanOutputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<ProductSearchParameters>>() {
        });
        String format = beanOutputConverter.getFormat();

        String systemInstruction = """
        Sen bir e-ticaret sisteminden sana verilen prompt içerisindeki parametrelere bakarak
         kullanıcıya ürün öneren bir Yapay Zeka asistansın.
        Görevin, kullanıcı sorgusunda sana verilen parametrelere göre ürünleri hepsiburada.com sitesinde 
        bulup çıkarmak.
        örnek link: https://www.hepsiburada.com/
        Çıkaramadığın tüm değerleri 'null' olarak ayarla.
        """;

        String prompt = """
            Aşağıdaki kullanıcı sorgusunu analiz et ve ürün listesini getir.
            Çıkaramadığın veya olmayan değerleri 'null' olarak ayarla.
            Sorgu: "{userPrompt}"
             
            Format:
            {format}      
                 """;

        PromptTemplate promptTemplate = PromptTemplate.builder().template(systemInstruction + prompt).build();
        Prompt prompt1 = promptTemplate.create(Map.of("userPrompt", userPrompt, "format", format));

        Generation generation = chatModel.call(prompt1).getResult();
        List<ProductSearchParameters> convert = beanOutputConverter.convert(generation.getOutput().getText());

        return convert;
    }


    public void ingestProductInfo(){
        List<ProductReadModel> allProducts = productService.getAllProducts();
        List<Document> documentList = allProducts.stream().map(productReadModel -> {
            String name = productReadModel.getName();
            String category = productReadModel.getCategory();
            String id = productReadModel.getId();
            List<ProductProperty> properties = productReadModel.getProperties();

            String propertiesTxt = properties.stream().map(p -> p.getKey() + ": " + p.getValue())
                    .collect(Collectors.joining(", "));

            String content = String.format("ürün adı: %s Category: %s. ÖZellikler %s ",
                    name, category, propertiesTxt);


            Map<String, Object> metadata= new HashMap<>();
            metadata.put("category", category);
            metadata.put("productId", id);
            return new Document(content, metadata);

        }).toList();

        vectorService.addVectordb(documentList);

    }

    public List<ProductResponseDto> searchProductsByVector(String prompt) {

        List<ProductResponseDto> productResponseDtos = getProductResponseDtos(prompt);

        return productResponseDtos;

    }

    private List<ProductResponseDto> getProductResponseDtos(String prompt) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(prompt)
                .filterExpression(new FilterExpressionBuilder().eq("category", "Ayakkabı").build())
                .topK(30)
                .build();

        List<Document> documents = productVectorStore.similaritySearch(searchRequest);

        List<ProductResponseDto> productResponseDtos = documents.stream().map(document -> {
            ProductResponseDto productResponseDto = new ProductResponseDto();
            Map<String, Object> metadata = document.getMetadata();
            String productId = (String) metadata.get("productId");
            ProductReadModel productReadModel = productService.getById(productId);
            String name = productReadModel.getName();

            productResponseDto.setCategory(productReadModel.getCategory());
            productResponseDto.setId(productReadModel.getId());
            productResponseDto.setProductProperties(productReadModel.getProperties());
            productResponseDto.setName(name);
            return productResponseDto;

        }).toList();
        return productResponseDtos;
    }

    public String chatProductsByVector(String prompt) {
        List<ProductResponseDto> productResponseDtos = getProductResponseDtos(prompt);

        String context = productResponseDtos.stream()
                .map(p -> String.format(
                        "ID: %s, İsim: %s, Özellikler: %s",
                        p.getId(), p.getName(), p.getProductProperties()
                ))
                .collect(Collectors.joining("\n- "));


        String systemPrompt = """
            Sen bir e-ticaret botusun. Görevin, sağlanan ürün listesi (BAĞLAM) içinden, 
            kullanıcının sorusuna uygun ve kibar bir şekilde en alakalı ürünleri listeleyerek 
            cevap vermektir. Eğer bağlamda uygun ürün yoksa, bunu kibarca belirt.
            kullanıcı sorgusunu al ve kullanıyca cevap dönerken giriş cümlesi olarak: "size 
            önerebileceğim ayakkabı istesi bunlar" diyerek başla
            """;

        // B. Kullanıcı/Prompt Mesajı: Bağlamı ve asıl sorguyu içerir
        String userPrompt = """
             {prompt}
            
            ve SAĞLANAN ÜRÜN BAĞLAMI (Sadece bu listedeki ürünleri kullan):
            ---
            {context}
            ---
            
            Lütfen en alakalı 3 ürünü listele ve kısa bir açıklama yap.
            """;


        PromptTemplate promptTemplate = PromptTemplate.builder().template(systemPrompt + userPrompt).build();
        Prompt prompt1 = promptTemplate.create(Map.of("prompt", prompt, "context", context));

        Generation generation = chatModel.call(prompt1).getResult();
        String text = generation.getOutput().getText();

        return text;


    }

    public List<ProductResponseDto> searchProductsByVectorHyDE(String prompt) {
        String hydePromptTemplate = """
            Sana verilen ürün sorgusuna (prompt) cevap olabilecek, detaylı, 
            kapsamlı ve teknik özellikler içeren tek bir HAYALİ ÜRÜN TANIMI oluştur. 
            Cevapta sadece ürün tanımı yer alsın, ek açıklama yapma.
            
            Örnek: '36 numara kadın Sneakers'
            Çıktı Örneği: '36 numara kadın spor ayakkabısı. Beyaz renkte, hafif file malzemeden yapılmış, 
            ortopedik tabanlı ve günlük kullanıma uygun rahat bir sneaker modeli.'
            
            PROMPT: {prompt}
            """;

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(hydePromptTemplate)
                .build();

        Prompt promptStr = promptTemplate.create(Map.of("prompt", prompt));
        Generation generation = chatModel.call(promptStr).getResult();
        String hypotheticalDocument = generation.getOutput().getText();
        String newQuery = hypotheticalDocument;


        SearchRequest searchRequest = SearchRequest
                .builder()
                .query(newQuery)
                .filterExpression(new FilterExpressionBuilder().eq("category","Ayakkabı").build())
                .topK(10)
                .build();


        List<Document> documents = productVectorStore.similaritySearch(searchRequest);
        List<ProductResponseDto> list = documents.stream().map(document -> {
            String id = (String)document.getMetadata().get("productId");
            ProductReadModel productReadModel = productService.getById(id);

            ProductResponseDto productResponseDto = new ProductResponseDto();
            productResponseDto.setCategory(productReadModel.getCategory());
            productResponseDto.setProductProperties(productReadModel.getProperties());
            productResponseDto.setName(productReadModel.getName());
            productResponseDto.setId(productReadModel.getId());
            return productResponseDto;

        }).toList();
        return list;

    }
}