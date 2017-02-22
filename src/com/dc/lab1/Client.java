package com.dc.lab1;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by niksat21 on 2/17/2017.
 */
public class Client implements Runnable {


//    Client client  = new Client(config.getNodes().get(i),
//            config.getLeader(),config.getNodes().get(i).getEdgesToNbrs()
//            ,new ArrayBlockingQueue<MessageType>(1).put(MessageType.ROUNDSTART),
//            takingQueue);

    private String nodeId;
    private String Leader;
    private List<Integer> edgeList;
    private BlockingQueue<Message> takingQueue;
    private BlockingQueue<Message> puttingQueue;
    private Boolean isLeader = false;
    private Integer dist;
    private Boolean readyToterminate=Boolean.FALSE;

    private Config config;
    private Node myNode;
    private List<String> myNbrs;
    private boolean isLeaf;
    private HashSet<String> waitingOnNodes;

    public Client(Config config, String nodeId, String leader, List<Integer> edgeList,
                  BlockingQueue<Message> takingQueue,
                  BlockingQueue<Message> puttingQueue) {
        this.nodeId = nodeId;
        Leader = leader;
        this.edgeList = edgeList;
        this.takingQueue = takingQueue;
        this.puttingQueue = puttingQueue;
        if (nodeId.equals(Leader)) {
            isLeader = true;
            dist = 0;
        } else {
            dist = Integer.MAX_VALUE;
        }


        this.config = config;
        this.myNode = config.getNodes().get(Integer.parseInt(this.nodeId));
        this.myNbrs=config.getNodes().get(Integer.parseInt(this.nodeId)).getNbrs();
        this.isLeaf = false;
        waitingOnNodes = new HashSet<String>();
        for(int i=0;i<myNbrs.size();i++)
        	waitingOnNodes.add(myNbrs.get(i));
    }


    @Override
    public void run() {

        try {
            int i = 0;
            while (i < 10) {
	            Message msg = takingQueue.take();
	            Thread.sleep(100);
	            clientWorker(msg);
               checkRoundStatus(msg);
               if(readyToterminate && this.isLeader){
            	   System.out.println("FINAL ADJACENCY LIST:");
            	   for(String key: myNode.getMyKnowledge().keySet()){
            		   adjacencyListObject adjObj = myNode.getMyKnowledge().get(key);
            		   System.out.println("Node: "+key+" Pred: "+adjObj.getpred()+" Dist: "+adjObj.getDist());
            	   }
               }
                puttingQueue.put(new Message(this.nodeId, Message.MessageType.ROUNDEND, msg.getRoundNumber()));
                i++;

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    private void checkRoundStatus(Message msg) throws InterruptedException {
    	
        Integer count = 0;
        BlockingQueue<Message> roundStatusTemp = myNode.getRoundStatus();
        if(!readyToterminate || !isLeaf){
        	for (int i = 0; i < myNode.getNumberOfNbrs(); i++) {
                Message roundStatusForME = roundStatusTemp.take();
                if (roundStatusForME.getMsgType().toString().equals("REJECT")) {
                    count++;
                    waitingOnNodes.remove(roundStatusForME.getNodeId());
                }
                else if(roundStatusForME.getMsgType().toString().equals("TERMINATE")){
                	waitingOnNodes.remove(roundStatusForME.getNodeId());
                }
                else if(roundStatusForME.getMsgType().toString().equals("DONE")){
                	if(!waitingOnNodes.contains(roundStatusForME.getNodeId()))
                		waitingOnNodes.add(roundStatusForME.getNodeId());
                }
            }
        	if(count==myNode.getNumberOfNbrs() && myNode.getMyChildSet().size() == 0 && !isLeaf){
            	isLeaf = true;
            	readyToterminate =true;
            	adjacencyListObject adjObj = new adjacencyListObject(this.nodeId, myNode.getPred(),this.dist);
            	config.getNodes().get(Integer.parseInt(myNode.getPred())).getMyKnowledge().put(this.nodeId, adjObj);
            	System.out.println("Leaf node detected for node: "+this.nodeId +" at round: "+msg.getRoundNumber()+ " Pred: "+myNode.getPred());
            }
        	else if(waitingOnNodes.size() == 0 && !readyToterminate && !this.isLeader){
        		readyToterminate=true;
        		Node myPred =config.getNodes().get(Integer.parseInt(myNode.getPred()));
        		for(String key : myNode.getMyKnowledge().keySet()){
        			myPred.getMyKnowledge().put(key, myNode.getMyKnowledge().get(key));
        		}
        		adjacencyListObject adjObj = new adjacencyListObject(this.nodeId, myNode.getPred(),this.dist);
        		myPred.getMyKnowledge().put(this.nodeId, adjObj);
        		System.out.println("Non Leaf node terminated for node: "+this.nodeId +" at round: "+msg.getRoundNumber());
        	}
        	else if(waitingOnNodes.size() == 0 && !readyToterminate && this.isLeader){
        		readyToterminate=true;
        		System.out.println("LEADER node terminate for node: "+this.nodeId +" at round: "+msg.getRoundNumber());
        	}
        }
        roundStatusTemp.clear();
    }



    public void clientWorker(Message msg) throws InterruptedException {

        //send dist to ur nbrs
        for (String nbr : myNbrs) {

//            System.out.println("putting msg in : from Node : "+this.nodeId+" : in to nbr quueue : "+nbr+ " :dist: "+dist);
            Message msgExplore =
                    new Message(this.nodeId, Message.MessageType.EXPLORE, dist, msg.getRoundNumber());
            config.getNodes().get(Integer.valueOf(nbr)).getRcvQueue().put(msgExplore);

        }

        BlockingQueue<Message> myReceiveQueue = myNode.getRcvQueue();

        for (int i = 0; i < myNode.getNumberOfNbrs(); i++) {
            try {
                Message rcvdMsg = myReceiveQueue.take();
                Node rcvdNode =config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId()));
//
//                distance[u] + w < distance[v]:
//                distance[v] := distance[u] + w
//                predecessor[v] := u

                String myPred = myNode.getPred().toString();
                if (rcvdMsg.getDist() != Integer.MAX_VALUE &&
                        rcvdMsg.getDist() + edgeList.get(Integer.valueOf(rcvdMsg.getNodeId())) < this.dist) {


                    this.dist = rcvdMsg.getDist() + edgeList.get(Integer.valueOf(rcvdMsg.getNodeId()));
                    myNode.pred = rcvdMsg.getNodeId();
                    rcvdNode.getMyChildSet().add(this.nodeId);
                    if(!myPred.equals(rcvdNode.getNodeID())&& !myPred.equals("unknown"))
                    	config.getNodes().get(Integer.parseInt(myPred)).getMyChildSet().remove(this.nodeId);
                   System.out.println("Updated dist : for : " +
                            this.nodeId + "\t" + this.dist + " : pred:  " + config.getNodes().get(Integer.valueOf(this.nodeId)).getPred() + " in round : " + msg.getRoundNumber());

                    Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.DONE, msg.getRoundNumber());
                    rcvdNode.getRoundStatus().put(roundStatusMsg);
                } else if (rcvdMsg.getDist() != Integer.MAX_VALUE) {
                    if (this.getReadyToterminate() && rcvdMsg.getNodeId().toString().equals(myPred) && !myPred.equals("unknown")) {

                       // System.out.println("sending terminate : from node : "+this.nodeId + " to node : "+rcvdMsg.getNodeId()+ " in round  : "+msg.getRoundNumber());
                        Message terminationMsg = new Message(this.nodeId, Message.MessageType.TERMINATE, msg.getRoundNumber());
                        rcvdNode.getRoundStatus().put(terminationMsg);
                    } else if(rcvdNode.getNodeID().equals(myPred)){
                    	
                        Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.REJECTPARENT, msg.getRoundNumber());
                        rcvdNode.getRoundStatus().put(roundStatusMsg);
                    }else{
                    	Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.REJECT, msg.getRoundNumber());
                        rcvdNode.getRoundStatus().put(roundStatusMsg);
                    }
                }
                else {
                        Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.IGNORE, msg.getRoundNumber());
                        rcvdNode.getRoundStatus().put(roundStatusMsg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean getReadyToterminate() {
        return readyToterminate;
    }

    public void setReadyToterminate(Boolean readyToterminate) {
        this.readyToterminate = readyToterminate;
    }
}

