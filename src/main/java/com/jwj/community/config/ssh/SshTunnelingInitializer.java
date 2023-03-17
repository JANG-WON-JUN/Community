package com.jwj.community.config.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Profile("dev")
@Component
public class SshTunnelingInitializer {

    @Value("${ssh.user}")
    private String user;

    @Value("${ssh.password}")
    private String password;

    @Value("${ssh.remote_jump_host}")
    private String remoteJumpHost;

    @Value("${ssh.ssh_port}")
    private int port;

    @Value("${ssh.database_host}")
    private String dbHost;

    @Value("${ssh.database_port}")
    private int dbPort;

    private Session session;

    public Integer buildSshConnection(){
        Integer forwardedPort = null;

        try {
            JSch jSch = new JSch();

            session = jSch.getSession(user, remoteJumpHost, port);
            session.setPassword(password);
            session.setConfig(sshConfig());
            session.connect();

            forwardedPort = session.setPortForwardingL(0, dbHost, dbPort);
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
