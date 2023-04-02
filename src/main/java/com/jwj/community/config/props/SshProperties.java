package com.jwj.community.config.props;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("ssh")
public class SshProperties {

    private String user;
    private String password;
    private String remoteJumpHost;
    private int port;
    private String dbHost;
    private int dbPort;
}
