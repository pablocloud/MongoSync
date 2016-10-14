package model;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ssh {

    private final JSch jSch;
    private Session session;
    private Channel channel;
    private ChannelSftp sftp;

    public Ssh(){
        jSch = new JSch();
    }

    public ChannelSftp Connect(String host, String username, String password) {
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        try {
            session = jSch.getSession(username, host);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (JSchException ex) {
            Logger.getLogger(Ssh.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sftp;
    }

}
