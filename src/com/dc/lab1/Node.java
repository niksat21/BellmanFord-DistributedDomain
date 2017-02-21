package com.dc.lab1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by niksat21 on 2/12/2017.
 */
public class Node {
    String nodeID;
    List<String> nbrs;
    Integer numberOfNbrs;
    List<Integer> edgesToNbrs;
    BlockingQueue<Message> rcvQueue;
    BlockingQueue<Message> terminationDetectionQueue;
    BlockingQueue<Message> roundStatus;
    String pred;
    HashSet<String> myChildSet;
    private HashMap<String,adjacencyListObject> myKnowledge;


    public Node(String nodeID, List<String> nbrs, Integer numberOfNbrs, List<Integer> edgesToNbrs) {
        this.nodeID = nodeID;
        this.nbrs = nbrs;
        this.numberOfNbrs = numberOfNbrs;
        this.edgesToNbrs = edgesToNbrs;
        rcvQueue = new ArrayBlockingQueue<>(this.numberOfNbrs);
        this.pred = "unknown";
        terminationDetectionQueue = new ArrayBlockingQueue<Message>(this.numberOfNbrs);
        roundStatus = new ArrayBlockingQueue<Message>(this.numberOfNbrs);
        this.myChildSet = new HashSet<String>();
        this.myKnowledge = new HashMap<String,adjacencyListObject>();
    }

    public String getNodeID() {
        return nodeID;
    }

    public List<String> getNbrs() {
        return nbrs;
    }

    public Integer getNumberOfNbrs() {
        return numberOfNbrs;
    }

    public List<Integer> getEdgesToNbrs() {
        return edgesToNbrs;
    }

    public BlockingQueue<Message> getRcvQueue(){
        return rcvQueue;
    }

    public String getPred(){
        return this.pred;
    }

    public BlockingQueue<Message> getTerminationDetectionQueue() {
        return terminationDetectionQueue;
    }

    public BlockingQueue<Message> getRoundStatus() {
        return roundStatus;
    }
    public HashSet<String> getMyChildSet(){
    	return myChildSet;
    }
    
    public synchronized HashMap<String,adjacencyListObject> getMyKnowledge(){
    	return this.myKnowledge;
    }
}
