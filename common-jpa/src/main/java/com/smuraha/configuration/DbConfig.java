package com.smuraha.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "dbManagerFactory",
        transactionManagerRef = "dbTransactionManager",
        basePackages = {"com.smuraha.*"})
@EntityScan("com.smuraha.model")
@ComponentScan("com.smuraha.*")
@RequiredArgsConstructor
public class DbConfig {

    private final DbProperties dbProperties;


    @Bean(name = "dbDataSource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName(dbProperties.getDriverClassName())
                .password(dbProperties.getPassword())
                .username(dbProperties.getUsername())
                .url(dbProperties.getUrl())
                .build();
    }


    @Bean(name = "dbManagerFactory")
    public LocalContainerEntityManagerFactoryBean dbManagerFactory(EntityManagerFactoryBuilder builder,
                                                                   @Qualifier("dbDataSource") DataSource dataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return builder.dataSource(dataSource).properties(properties).packages("com.smuraha.model").persistenceUnit("db").build();
    }


    @Bean(name = "dbTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("dbManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
