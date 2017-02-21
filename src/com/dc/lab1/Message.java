package com.dc.lab1;

/**
 * Created by niksat21 on 2/17/2017.
 */
public class Message {


    public enum MessageType {

        EXPLORE, DONE, ROUNDSTART, ROUNDEND, REJECT, IGNORE, TERMINATE, REJECTPARENT
    }

    private String nodeId ;
    private MessageType msgType;
    private Integer roundNumber;
    private Integer dist;
     public  Message(String nodeId,MessageType msgType,Integer roundNumber) {
        this.msgType=msgType;
        this.nodeId = nodeId;
        this.roundNumber=roundNumber;
    }
    //Message msg = new Message(this.nodeId,Message.MessageType.EXPLORE,dist);
     public Message(String nodeId,MessageType msgType,Integer dist,Integer roundNumber){

         this.nodeId=nodeId;
         this.msgType=msgType;
         this.dist=dist;
         this.roundNumber=roundNumber;
    }


    public Integer getRoundNumber() {
        return roundNumber;
    };

    public String getNodeId() {
        return nodeId;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public Integer getDist() {
        return dist;
    }
}
