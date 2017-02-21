package com.dc.lab1;

public class adjacencyListObject {
	private String myNodeId;
	private String myPred;
	private int myDist;
	
	public adjacencyListObject(String nodeId, String pred, int dist){
		this.myNodeId = nodeId;
		this.myPred = pred;
		this.myDist = dist;
	}
	
	public String getNodeId(){
		return this.myNodeId;
	}
	
	public String getpred(){
		return this.myPred;
	}
	
	public int getDist(){
		return this.myDist;
	}

}
