package liva.SpringSearchAI.controller;

import liva.SpringSearchAI.dto.ChatbotRequestDto;
import liva.SpringSearchAI.service.ChatbotService;
import liva.SpringSearchAI.service.SSSIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final SSSIngestionService ingestionService;

    private final ChatbotService chatbotService;

    public ChatController(SSSIngestionService ingestionService, ChatbotService chatbotService) {

        this.ingestionService = ingestionService;
        this.chatbotService = chatbotService;


    }

    @GetMapping("/ask")
    public String chat(@RequestBody ChatbotRequestDto chatbotRequestDto) {
        return chatbotService.chat(chatbotRequestDto.prompt());

    }

    @PostMapping("/addSSSPdf")
    public void add() throws Exception {
        ingestionService.addPdf();
    }
}