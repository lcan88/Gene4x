package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: The event thrown when a session connection is requested</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author $AUTHOR$
 * @version 1.0
 */
public class SessionConnectEvent extends Event {
    private String userName;
    private String password;
    private String host;
    private int port;
    private String serviceName;
    private int sessionId;
    private String sessionName;

    /**
     * @param source      EventSource
     * @param userName    name of the user on the server
     * @param password    the password
     * @param host        host name
     * @param port        port on url
     * @param serviceName identifies the service. This allows listener to monitor
     *                    the event and decide if the request was ment for them.
     * @param sessionId   the id of the session.
     */
    public SessionConnectEvent(String userName, char[] password, String host, int port, String serviceName, int sessionId, String sessionName) {
        super(null);
        this.userName = userName;
        this.password = new String(password);
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getSessionName() {
        return sessionName;
    }
}
