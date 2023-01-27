package com.jwj.community.domain.entity.member.auth;

import com.jwj.community.domain.entity.BaseEntity;
import com.jwj.community.domain.entity.member.embedded.HttpMethod;
import com.jwj.community.domain.enums.ResourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RESOURCES_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resources extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourceName;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    private HttpMethod httpMethod;

    private Integer orderNum; // 자원의 우선순위

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    private ResourceType resourceType;

}
