package liva.SpringSearchAI.controller;

import liva.SpringSearchAI.dto.ChatbotRequestDto;
import liva.SpringSearchAI.dto.ProductResponseDto;
import liva.SpringSearchAI.dto.ProductSearchParameters;
import liva.SpringSearchAI.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class SearchController {

    private final SearchService chatService;
    public SearchController(SearchService chatService) {
        this.chatService = chatService;
    }


    //1
    /**
     * via ethernet
     * @param chatbotRequestDto
     * @return
     */
    @GetMapping("/productListByPropertiesPrompt")
    public List<ProductSearchParameters> productListByPropertiesPrompt(@RequestBody ChatbotRequestDto chatbotRequestDto){
        return chatService.getProductByParametrePrompt(chatbotRequestDto.prompt());
    }



    //3
    @GetMapping("/searchProductsByVector")
    public List<ProductResponseDto> searchProductsByVector(@RequestBody ChatbotRequestDto chatbotRequestDto){
        return chatService.searchProductsByVector(chatbotRequestDto.prompt());
    }

    //2
    @PostMapping("/ingestProductInfo")
    public void ingestProductInfo(){
        chatService.ingestProductInfo();
    }


    //4 chatbot RAG
    @GetMapping("/chatProductsByVector")
    public String chatProductsByVector(@RequestBody ChatbotRequestDto chatbotRequestDto){
        return chatService.chatProductsByVector(chatbotRequestDto.prompt());
    }

    //5
    @GetMapping("/searchProductsByVectorHyDE")
    public List<ProductResponseDto> searchProductsByVectorHyDE(@RequestBody ChatbotRequestDto chatbotRequestDto){
        return chatService.searchProductsByVectorHyDE(chatbotRequestDto.prompt());
    }
}
