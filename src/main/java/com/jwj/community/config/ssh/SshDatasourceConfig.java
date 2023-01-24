package com.jwj.community.config.ssh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Slf4j
@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class SshDatasourceConfig {

    private final SshTunnelingInitializer initializer;

    /* SpringBoot에서 port forwarding하기
     * 1. SshTunnelingInitializer를 빈으로 등록한다.
     *      (SshTunnelingInitializer는 db서버가 위치한 서버에 접속 후 포워딩할 포트를 반환한다.)
     * 2. 포워딩 할 포트번호를 얻어서, DataSource를 만들어서 반환한다.
     * 3. 즉 포워딩 전에는 application.yml에서 직접 dataSource를 얻었다면, 포워딩 시에는 직접 Bean을 등록하여 port를 설정해주면 된다.
     */
    @Bean("dataSource")
    public DataSource dataSource(DataSourceProperties properties) {
        Integer forwardedPort = initializer.buildSshConnection();
        String url = properties.getUrl().replace("[forwardedPort]", Integer.toString(forwardedPort));
        return DataSourceBuilder.create()
                .url(url)
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }
}
