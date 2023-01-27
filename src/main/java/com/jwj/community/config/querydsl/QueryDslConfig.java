package com.jwj.community.config.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {

    @PersistenceContext
    private EntityManager em;

     //QueryDsl을 사용하는 클래스에서 주입받아서 사용함
    @Bean
    public JPAQueryFactory queryFactory(){
        return new JPAQueryFactory(em);
    }

}
