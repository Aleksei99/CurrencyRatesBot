package com.smuraha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@TestConfiguration
@PropertySources({@PropertySource("classpath:test.properties")})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestConfig {

    @Bean
    @Primary
//    @ConfigurationProperties(prefix = "test.datasource")
    public DataSource  dataSource(){
        return DataSourceBuilder.create()
                .url("jdbc:h2://mem:db;DB_CLOSE_DELAY=-1;INIT=RUN SCRIPT FROM 'classpath:init.sql'")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .build();
    }
}
