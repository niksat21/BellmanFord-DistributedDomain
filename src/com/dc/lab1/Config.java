package com.dc.lab1;

import java.util.List;
import java.util.Map;

/**
 * Created by niksat21 on 2/12/2017.
 */
public class Config {

    Integer noOfNodes;
    List<String> nodeID;
    List<Node> nodes;
    Map<String,List<Integer>> weightMatrix;
    String leader;
    List<NodeLocation> nodeLocs;

    public Config(Integer noOfNodes, List<String> nodeID,
                  List<Node> nodes, Map<String,
            List<Integer>> weightMatrix, String leader,List<NodeLocation> nodeLocs) {
        this.noOfNodes = noOfNodes;
        this.nodeID = nodeID;
        this.nodes = nodes;
        this.weightMatrix = weightMatrix;
        this.leader = leader;
        this.nodeLocs=nodeLocs;
    }

    public Integer getNoOfNodes() {
        return noOfNodes;
    }

    public List<String> getNodeID() {
        return nodeID;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Map<String, List<Integer>> getWeightMatrix() {
        return weightMatrix;
    }

    public String getLeader() {
        return leader;
    }

    public List<NodeLocation> getNodeLocs() {
        return nodeLocs;
    }
}
