package com.dc.lab1;

public class Message {
	
	private String nodeId;
	private int roundNumber;
	
	public Message(String nodeId,int roundNumber){
		this.nodeId = nodeId;
		this.roundNumber = roundNumber;
	}
	
	public int getRound(){
		return roundNumber;
	}
	
	public String getNodeId(){
		return nodeId;
	}

}
