package org.geworkbench.builtin.projects.remoteresources;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
/**
 *A simple wrapper class for resources
 */
public class RemoteResource {
    private String username;
    private String password;
    private String connectProtocol;
    private String DEFAULTPROTOCAL = "http";
    private String shortname;
    private String uri;
    private int portnumber = 80;
    private boolean editable = true;
    public RemoteResource() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public RemoteResource(String url, String protocal, String user,
                          String passwd) {
        this("Default", url, protocal, user, passwd);
    }

    public RemoteResource(String shortname, String url, String protocal,
                          String user, String passwd) {
        uri = url;
        connectProtocol = protocal;
        username = user;
        password = passwd;
        this.shortname = shortname;
    }

    public RemoteResource(String shortname, String url, String port,
                          String protocal,
                          String user, String passwd) {
        uri = url.trim();
        connectProtocol = protocal.trim();
        username = user.trim();
        password = passwd.trim();
        this.shortname = shortname.trim();
        try {
            if (new Integer(port.trim()).intValue() != 0) {
                portnumber = new Integer(port.trim()).intValue();
            }
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            portnumber = 80;
        }
        this.editable = true;
        ;
    }


    public static RemoteResource createNewInstance(String[] columns) {
        if (columns.length == 7) {
            return new RemoteResource(columns[0], columns[1], columns[2],
                                      columns[3], columns[4], columns[5], columns[6]);
        } else if (columns.length == 6) {
            return new RemoteResource(columns[0], columns[1], columns[2],
                                      columns[3], columns[4], columns[5]);
        } else if (columns.length == 4) {
                    return new RemoteResource(columns[0], columns[2], columns[3],
                                      columns[1], "", "", "false");
        }
        return null;

    }

    /**
     * RemoteResource
     */
    public RemoteResource(String shortname, String url, String port,
                          String protocal,
                          String user, String passwd, String editableStr
) {
        this(shortname, url, port, protocal, user, passwd);

        this.editable = new Boolean(editableStr.trim()).booleanValue();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectProtocal(String connectProtocol) {
        this.connectProtocol = connectProtocol;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setPortnumber(int portnumber) {
        this.portnumber = portnumber;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConnectProtocal() {
        return connectProtocol;
    }

    public String getShortname() {
        return shortname;
    }

    public String getUri() {
        return uri;
    }

    public int getPortnumber() {
        return portnumber;
    }

    public boolean isEditable() {
        return editable;
    }

    /**
     * update
     *
     * @param rResource RemoteResource
     */
    public void update(RemoteResource rResource) {
        shortname = rResource.shortname;
        uri = rResource.uri;
        username = rResource.username;
        password = rResource.password;
        connectProtocol = rResource.connectProtocol;
        editable =rResource.editable;
    }

    /**
     * Use shortname as the Key for every object.
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj) {
        if (obj instanceof RemoteResource) {
            return shortname.equals(((RemoteResource) obj).shortname);
        } else {
            return false;
        }
    }

    private void jbInit() throws Exception {
    }
}
