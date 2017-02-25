package com.dc.lab1;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class Client implements Runnable {


//    Client client  = new Client(config.getNodes().get(i),
//            config.getLeader(),config.getNodes().get(i).getEdgesToNbrs()
//            ,new ArrayBlockingQueue<MessageType>(1).put(MessageType.ROUNDSTART),
//            takingQueue);
    private Boolean loopStopFlag = Boolean.FALSE;
    private String nodeId;
    private List<Integer> edgeList;
    private BlockingQueue<Message> takingQueue;
    private BlockingQueue<Message> puttingQueue;
    private Boolean isLeader = false;
    private Integer dist;
    private Boolean readyToterminate=Boolean.FALSE;
    private Boolean killFlag = Boolean.FALSE;
    private Config config;
    private Node myNode;
    private List<String> myNbrs;
    private boolean isLeaf;
    private HashSet<String> waitingOnNodes;
    private int rejectCounter, ignoreCounter, doneCounter;
    private HashSet<String> terminationQueueFromMaster;

    Client(Config config, String nodeId, String leader, List<Integer> edgeList,
           BlockingQueue<Message> takingQueue,
           BlockingQueue<Message> puttingQueue, HashSet<String> terminationQueue) {
        this.nodeId = nodeId;
        this.edgeList = edgeList;
        this.takingQueue = takingQueue;
        this.puttingQueue = puttingQueue;
        if (nodeId.equals(leader)) {
            isLeader = true;
            dist = 0;
        } else {
            dist = Integer.MAX_VALUE;
        }

        this.rejectCounter =0;
        this.ignoreCounter =0;
        this.doneCounter =0;
        this.config = config;
        this.myNode = config.getNodes().get(Integer.parseInt(this.nodeId));
        this.myNbrs=config.getNodes().get(Integer.parseInt(this.nodeId)).getNbrs();
        this.isLeaf = false;
        this.terminationQueueFromMaster = terminationQueue;
        waitingOnNodes = new HashSet<>();
        for (String myNbr : myNbrs) waitingOnNodes.add(myNbr);
    }


    @Override
    public void run() {

        try {
            int i = 0;
            while (!loopStopFlag) {
//            while(!config.getNodes().get(Integer.parseInt(this.nodeId)).doneFlag){
	            Message msg = takingQueue.take();
                if(msg.getMsgType().toString().equals("KILL")){

                    this.loopStopFlag=Boolean.TRUE;
                    System.out.println("ending node in next round : "+this.nodeId);
//                    config.getNodes().get(Integer.parseInt(this.nodeId)).getRcvQueue().clear();
//                    config.getNodes().get(Integer.parseInt(this.nodeId)).getRoundStatus().clear();
//                    config.getNodes().get(Integer.parseInt(this.nodeId)).getTerminationDetectionQueue().clear();


                }


//                Thread.sleep(100);
	            clientWorker(msg);
               checkRoundStatus(msg);
               if(readyToterminate && this.isLeader && !this.killFlag){
            	   System.out.println("FINAL ADJACENCY LIST:");
            	   for(String key: myNode.getMyKnowledge().keySet()){
            		   adjacencyListObject adjObj = myNode.getMyKnowledge().get(key);
            		   System.out.println("Node: "+key+" Pred: "+adjObj.getpred()+" Dist: "+adjObj.getDist());
            		   //Master.isDone=Boolean.TRUE;
            	   }
                   this.killFlag =Boolean.TRUE;
                   System.out.println("killing leader : ");
                   terminationQueueFromMaster.add(this.nodeId);
               }
               synchronized (this){

                   if(!Master.getDone())
                    puttingQueue.put(new Message(this.nodeId, Message.MessageType.ROUNDEND, msg.getRoundNumber()));
               }

                i++;
                rejectCounter =0;
                doneCounter=0;
                ignoreCounter=0;

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.nodeId+ " exiting");
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
//        	if(Objects.equals(count, myNode.getNumberOfNbrs()) && myNode.getMyChildSet().size() == 0 && !isLeaf && (myNode.getNumberOfNbrs()==this.rejectCounter)
//                    && doneCounter ==0 && ignoreCounter ==0){
            if(Objects.equals(count, myNode.getNumberOfNbrs()) && myNode.getMyChildSet().size() == 0 && !isLeaf &&
                    doneCounter ==0 && ignoreCounter ==0){
                System.out.println(this.nodeId + " : getting rejects count : "+count);
                System.out.println(this.nodeId + " : sent no of rejects : "+this.rejectCounter);
                isLeaf = true;
            	readyToterminate =true;
            	adjacencyListObject adjObj = new adjacencyListObject(this.nodeId, myNode.getPred(),this.dist);
            	config.getNodes().get(Integer.parseInt(myNode.getPred())).getMyKnowledge().put(this.nodeId, adjObj);
            	System.out.println("Leaf node detected for node: "+this.nodeId +" at round: "+msg.getRoundNumber()+ " Pred: "+myNode.getPred());

            }
        	else if(waitingOnNodes.size() == 0 && !readyToterminate && !this.isLeader && doneCounter==0 && ignoreCounter==0){
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



    private void clientWorker(Message msg) throws InterruptedException {

        //send dist to ur nbrs
        for (String nbr : myNbrs) {


//            System.out.println("putting msg in : from Node : "+this.nodeId+" : in to nbr quueue : "+nbr+ " :dist: "+dist);

            if(myNode.getMyChildSet().contains(nbr) && this.killFlag){
                System.out.println(this.nodeId+ " sending kill to child : "+nbr);
                Message msgExplore =
                        new Message(this.nodeId, Message.MessageType.KILL, dist, msg.getRoundNumber());
                config.getNodes().get(Integer.valueOf(nbr)).getRcvQueue().put(msgExplore);
            }else{
                Message msgExplore =
                        new Message(this.nodeId, Message.MessageType.EXPLORE, dist, msg.getRoundNumber());
                config.getNodes().get(Integer.valueOf(nbr)).getRcvQueue().put(msgExplore);
            }

        }

        BlockingQueue<Message> myReceiveQueue = myNode.getRcvQueue();

        for (int i = 0; i < myNode.getNumberOfNbrs(); i++)
            try {
                Message rcvdMsg = myReceiveQueue.take();

                if(rcvdMsg.getMsgType().toString().equals("KILL")){
                    System.out.println("killed : "+this.nodeId);
                    this.killFlag=Boolean.TRUE;

                    terminationQueueFromMaster.add(this.nodeId);
//                    this.loopStopFlag=Boolean.TRUE;
                }
                Node rcvdNode = config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId()));
//
//                distance[u] + w < distance[v]:
//                distance[v] := distance[u] + w
//                predecessor[v] := u

                String myPred = myNode.getPred();
                if (rcvdMsg.getDist() != Integer.MAX_VALUE &&
                        rcvdMsg.getDist() + edgeList.get(Integer.valueOf(rcvdMsg.getNodeId())) < this.dist) {


                    this.dist = rcvdMsg.getDist() + edgeList.get(Integer.valueOf(rcvdMsg.getNodeId()));
                    myNode.pred = rcvdMsg.getNodeId();
                    rcvdNode.addToChildSet(this.nodeId);
                    if (!myPred.equals(rcvdNode.getNodeID()) && !myPred.equals("unknown"))
                        config.getNodes().get(Integer.parseInt(myPred)).removeFromChildSet(this.nodeId);
                    System.out.println("Updated dist : for : " +
                            this.nodeId + "\t" + this.dist + " : pred:  " + config.getNodes().get(Integer.valueOf(this.nodeId)).getPred() + " in round : " + msg.getRoundNumber());

                    Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.DONE, msg.getRoundNumber());
                    rcvdNode.getRoundStatus().put(roundStatusMsg);
                    System.out.println(this.nodeId+" sending done to : "+rcvdNode.getNodeID());
                    doneCounter++;
                } else if (rcvdMsg.getDist() != Integer.MAX_VALUE) {
                    if (this.getReadyToterminate() && rcvdMsg.getNodeId().equals(myPred) && !myPred.equals("unknown")) {

                        // System.out.println("sending terminate : from node : "+this.nodeId + " to node : "+rcvdMsg.getNodeId()+ " in round  : "+msg.getRoundNumber());
                        Message terminationMsg = new Message(this.nodeId, Message.MessageType.TERMINATE, msg.getRoundNumber());
                        rcvdNode.getRoundStatus().put(terminationMsg);
                    } else if (rcvdNode.getNodeID().equals(myPred)) {

                        Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.REJECTPARENT, msg.getRoundNumber());
                        rcvdNode.getRoundStatus().put(roundStatusMsg);
                        rejectCounter++;
                    } else {
                        Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.REJECT, msg.getRoundNumber());
                        rcvdNode.getRoundStatus().put(roundStatusMsg);
                        rejectCounter++;
                    }
                } else {
                    Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.IGNORE, msg.getRoundNumber());
                    rcvdNode.getRoundStatus().put(roundStatusMsg);
                    ignoreCounter++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private Boolean getReadyToterminate() {
        return readyToterminate;
    }

    public void setReadyToterminate(Boolean readyToterminate) {
        this.readyToterminate = readyToterminate;
    }
}

