package liva.SpringSearchAI.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class SSSIngestionService {

    private static final Logger log = LoggerFactory.getLogger(SSSIngestionService.class);
    private final VectorStore vectorStore;

    @Value("classpath:/siparisSSS.pdf")
    private Resource interviewDb;

    public SSSIngestionService(VectorStore vectorStore) {

        this.vectorStore = vectorStore;

    }

    public void addPdf() throws Exception {
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(interviewDb);
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(250, 30, 5, 10000, true);
        vectorStore.accept(tokenTextSplitter.apply(pagePdfDocumentReader.get()));
        log.info("başarılı");

    }
}
