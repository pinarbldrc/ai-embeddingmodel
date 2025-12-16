package liva.SpringSearchAI.config;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ai.embedding.EmbeddingModel;


@Configuration
public class VectorStoreConfig {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;

    // JdbcTemplate ve EmbeddingModel'ı DI ile alın
    public VectorStoreConfig(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingModel = embeddingModel;
    }

    @Bean
    public VectorStore productVectorStore(){

        return PgVectorStore.builder(
                jdbcTemplate, embeddingModel
        ).vectorTableName("product_vector").build();
    }

    @Bean
    public VectorStore documentVectorStore(){

        return PgVectorStore.builder(
                jdbcTemplate, embeddingModel
        ).vectorTableName("vector_store").build();
    }

}
