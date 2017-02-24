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
    private String nodeID;
    private List<String> nbrs;
    private Integer numberOfNbrs;
    private List<Integer> edgesToNbrs;
    private BlockingQueue<Message> rcvQueue;
    private BlockingQueue<Message> terminationDetectionQueue;
    private BlockingQueue<Message> roundStatus;
    String pred;
    private HashSet<String> myChildSet;
    private HashMap<String,adjacencyListObject> myKnowledge;
    Boolean doneFlag=Boolean.FALSE;

    Node(String nodeID, List<String> nbrs, Integer numberOfNbrs, List<Integer> edgesToNbrs) {
        this.nodeID = nodeID;
        this.nbrs = nbrs;
        this.numberOfNbrs = numberOfNbrs;
        this.edgesToNbrs = edgesToNbrs;
        rcvQueue = new ArrayBlockingQueue<>(this.numberOfNbrs);
        this.pred = "unknown";
        terminationDetectionQueue = new ArrayBlockingQueue<>(this.numberOfNbrs);
        roundStatus = new ArrayBlockingQueue<>(this.numberOfNbrs);
        this.myChildSet = new HashSet<>();
        this.myKnowledge = new HashMap<>();
    }

    String getNodeID() {
        return nodeID;
    }

    List<String> getNbrs() {
        return nbrs;
    }

    Integer getNumberOfNbrs() {
        return numberOfNbrs;
    }

    List<Integer> getEdgesToNbrs() {
        return edgesToNbrs;
    }

    BlockingQueue<Message> getRcvQueue(){
        return rcvQueue;
    }

    String getPred(){
        return this.pred;
    }

    public BlockingQueue<Message> getTerminationDetectionQueue() {
        return terminationDetectionQueue;
    }

    BlockingQueue<Message> getRoundStatus() {
        return roundStatus;
    }

    HashSet<String> getMyChildSet(){
        synchronized(this){
            return myChildSet;
        }

    }

    void addToChildSet(String nodeId){
        synchronized (this){
            this.myChildSet.add(nodeId);
        }
    }

    void removeFromChildSet(String nodeId){
        synchronized (this){
            this.myChildSet.remove(nodeId);
        }
    }
    
    HashMap<String,adjacencyListObject> getMyKnowledge(){
        synchronized(this){
            return this.myKnowledge;
        }
    }
}
