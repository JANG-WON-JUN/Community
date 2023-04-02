package com.jwj.community.config.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jwj.community.config.props.SshProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class SshTunnelingInitializer {

    private final SshProperties sshProperties;
    private Session session;

    public Integer buildSshConnection(){
        Integer forwardedPort = null;

        try {
            JSch jSch = new JSch();

            session = jSch.getSession(sshProperties.getUser(), sshProperties.getRemoteJumpHost(), sshProperties.getPort());
            session.setPassword(sshProperties.getPassword());
            session.setConfig(sshConfig());
            session.connect();

            forwardedPort = session.setPortForwardingL(0, sshProperties.getDbHost(), sshProperties.getDbPort());
        } catch (JSchException e) {
            // todo DB 접속 실패에러 던저야 함
            e.printStackTrace();
        }

        return forwardedPort;
    }

    private Properties sshConfig(){
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        return config;
    }
}
