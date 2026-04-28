package ru.gorilla.gim.backend.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.minio.MinioClient;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static ru.gorilla.gim.backend.util.CommonUnits.DATE_FORMAT;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Configuration
@ComponentScan("ru.gorilla.gim.backend")
@EnableJpaRepositories("ru.gorilla.gim.backend.repository")
@EntityScan("ru.gorilla.gim.backend.entity")
public class AppConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.url}")
    private String jdbcURL;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${minio.url}")
    private String minioURL;
    @Value("${minio.username}")
    private String minioUsername;
    @Value("${minio.password}")
    private String minioPassword;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(jdbcURL);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Gorilla GIM API").version("1.0"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public ObjectMapper getCommonObjectMapper() {
        var simpleModule = new SimpleModule();
        simpleModule.addSerializer(OffsetDateTime.class, new ValueSerializer<>() {
            @Override
            public void serialize(OffsetDateTime value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
                gen.writeString(DateTimeFormatter.ofPattern(DATE_FORMAT).format(value));
            }
        });

        var builder = JsonMapper.builder()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .addModule(simpleModule)
                .changeDefaultPropertyInclusion(v -> JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL));

        return builder.build();
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioURL)
                .credentials(minioUsername, minioPassword)
                .build();
    }
}
