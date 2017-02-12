package com.dc.lab1;

/**
 * Created by niksat21 on 2/12/2017.
 */
public class NodeLocation {

    private String nodeID ;
    private String hostName;
    private Integer port;

    public NodeLocation(String nodeID, String hostName, Integer port) {
        this.nodeID = nodeID;
        this.hostName = hostName;
        this.port = port;
    }

    public String getNodeID() {
        return nodeID;
    }

    public String getHostName() {
        return hostName;
    }

    public Integer getPort() {
        return port;
    }
}
