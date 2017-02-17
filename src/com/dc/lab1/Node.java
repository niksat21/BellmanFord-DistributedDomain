package com.dc.lab1;

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
    String pred;


    public Node(String nodeID, List<String> nbrs, Integer numberOfNbrs, List<Integer> edgesToNbrs) {
        this.nodeID = nodeID;
        this.nbrs = nbrs;
        this.numberOfNbrs = numberOfNbrs;
        this.edgesToNbrs = edgesToNbrs;
        rcvQueue = new ArrayBlockingQueue<>(this.numberOfNbrs);
        this.pred = null;

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

}
