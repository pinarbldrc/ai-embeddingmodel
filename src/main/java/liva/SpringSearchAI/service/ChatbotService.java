package liva.SpringSearchAI.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {


    private final ChatClient chatClient;

    public ChatbotService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }


    public String chat(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();

    }
}
