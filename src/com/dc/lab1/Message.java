package com.dc.lab1;

/**
 * Created by niksat21 on 2/17/2017.
 */
public class Message {

    public enum MessageType {

        EXPLORE, DONE, ROUNDSTART, ROUNDEND
    }

    private String nodeId ;
    private MessageType msgType;
    private Integer roundNumber;

    public Message(String nodeId,MessageType msgType,Integer roundNumber) {
        this.msgType=msgType;
        this.nodeId = nodeId;
        this.roundNumber=roundNumber;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public String getNodeId() {
        return nodeId;
    }

    public MessageType getMsgType() {
        return msgType;
    }
}
