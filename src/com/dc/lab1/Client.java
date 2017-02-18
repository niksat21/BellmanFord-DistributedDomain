package com.dc.lab1;

import java.util.List;
import java.util.Objects;
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
    private Boolean isClientDone = false;
    private Boolean isLeader = false;
    private Integer dist;
    private Boolean readyToterminate=Boolean.FALSE;

    private Config config;

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

    }


    @Override
    public void run() {

        try {
            int i = 0;

            // while (!isClientDone) {
//            while(!Master.getDone()){
            while (i < 5) {

                Message msg = takingQueue.take();
//                System.out.println("Client : "+this.nodeId+" \t"+msg.getRoundNumber());
                Thread.sleep(100);
                //TODO add bellman ford login
                clientWorker(msg);
                if(!this.getReadyToterminate())
                    testTermination(msg);

//                checkRoundStatus(msg);

//                if(this.getReadyToterminate()){
//                    checkForTermination(msg);
//                    System.out.println("checking for termination for node : "+this.nodeId);
//                    if(this.nodeId.equals(this.Leader)){
//
//                        System.out.println("setting end of flag for master : true");
//                        Master.setDone(Boolean.TRUE);
//
//                    }
//                }


                puttingQueue.put(new Message(this.nodeId, Message.MessageType.ROUNDEND, msg.getRoundNumber()));
                System.out.println("sent RoundEND from : " + this.nodeId + "round no : " + msg.getRoundNumber());
                i++;

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void testTermination(Message msg) throws InterruptedException {

        List<String> nbrs = config.getNodes().get((Integer.parseInt(this.nodeId)) - 1).getNbrs();
        int count = 0;
        BlockingQueue<Message> statusQueue = config.getNodes().get((Integer.parseInt(this.nodeId)) - 1).getRoundStatus();

        for (int i = 0; i < nbrs.size(); i++) {
            Message statusMsg = statusQueue.take();
            if (statusMsg.getMsgType().toString().equals("REJECT") ||
                    statusMsg.getMsgType().toString().equals("TERMINATE")) {
                count++;
            }
        }
        if (count == nbrs.size()) {
            System.out.println("Termination detected : by node : "+this.nodeId + " in round : "+msg.getRoundNumber());
            this.setReadyToterminate(Boolean.TRUE);


        }


    }

    private void checkForTermination(Message msg) throws InterruptedException {

        System.out.println("Termination : node : " + this.nodeId + " in round  : " + msg.getRoundNumber());
        BlockingQueue<Message> terminationTemp = config.getNodes().get(Integer.parseInt(this.nodeId) - 1).getTerminationDetectionQueue();
        Integer noOfNbrs = config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getNumberOfNbrs();
        List<String> nbrs = null;
        if (terminationTemp.size() == noOfNbrs) {
            this.setReadyToterminate(Boolean.TRUE);
            System.out.println("Terminated : " + this.nodeId + " in round  : " + msg.getRoundNumber());
            nbrs = config.getNodes().get(Integer.parseInt(this.nodeId) - 1).getNbrs();
            for (int i = 0; i < config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getNumberOfNbrs(); i++) {
                Message terminationMsg = new Message(this.nodeId, Message.MessageType.TERMINATE, msg.getRoundNumber());
                config.getNodes().get(Integer.parseInt(nbrs.get(i))).getTerminationDetectionQueue().put(terminationMsg);
            }
        }

        config.getNodes().get(Integer.parseInt(this.nodeId) - 1).getTerminationDetectionQueue().clear();
        System.out.println("term que cleared for node : " + this.nodeId);

    }

    private void checkRoundStatus(Message msg) throws InterruptedException {

        System.out.println("in round check for : " + this.nodeId + " Round: " + msg.getRoundNumber());
        Integer count = 0;
        BlockingQueue<Message> roundStatusTemp = config.getNodes().get(Integer.parseInt(this.nodeId) - 1).getRoundStatus();
        System.out.println("size of roundstatus queue : " + roundStatusTemp.size() + " for node : " + this.nodeId);
        for (int i = 0; i < config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getNumberOfNbrs(); i++) {


            Message roundStatusForME = roundStatusTemp.take();
            System.out.println("msg from roundStatusForME from  : " +
                    roundStatusForME.getNodeId() +
                    "to nde : " + this.nodeId + " in round : " + roundStatusForME.getRoundNumber() + roundStatusForME.getMsgType());
            if (roundStatusForME.getMsgType().toString().equals("REJECT")) {
                count++;

//                Message.MessageType.REJECT
                System.out.println("rcvd reject from : " + roundStatusForME.getNodeId() + " by node : " + this.nodeId);
            }


        }
        System.out.println("count : " + count + "\t" + " for node : " + this.nodeId);

        if (Objects.equals(count, config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getNumberOfNbrs())) {

            this.setReadyToterminate(Boolean.TRUE);
            System.out.println("setting readyforTerm true for node : " + this.nodeId + " Round: " + msg.getRoundNumber());
            System.out.println("Termination detected by node : " + this.nodeId + " in round : " + msg.getRoundNumber());
            List<String> nbrs = config.getNodes().get(Integer.parseInt(this.nodeId) - 1).getNbrs();
            for (int i = 0; i < config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getNumberOfNbrs(); i++) {
                System.out.println("nbr : " + nbrs.get(i) + " for node : " + this.nodeId);
                Message terminationMsg = new Message(this.nodeId, Message.MessageType.TERMINATE, msg.getRoundNumber());
                config.getNodes().get((Integer.parseInt(nbrs.get(i))) - 1).getTerminationDetectionQueue().put(terminationMsg);
                System.out.println("TErm qu for node : " + config.getNodes().get(Integer.parseInt(nbrs.get(i))).getNodeID()
                        + " added by : " + this.nodeId + " in round : " + msg.getRoundNumber() + " size : " + config.getNodes().get(Integer.parseInt(nbrs.get(i))).getTerminationDetectionQueue().size());
            }

        }

        roundStatusTemp.clear();

    }

    public void clientWorker(Message msg) throws InterruptedException {

        //send dist to ur nbrs

        List<String> nbrList = config.getNodes()
                .get(Integer.valueOf(this.nodeId) - 1).getNbrs();
        for (String nbr : nbrList) {

//            System.out.println("putting msg in : from Node : "+this.nodeId+" : in to nbr quueue : "+nbr+ " :dist: "+dist);
            Message msgExplore =
                    new Message(this.nodeId, Message.MessageType.EXPLORE, dist, msg.getRoundNumber());
            config.getNodes().get(Integer.valueOf(nbr) - 1).getRcvQueue().put(msgExplore);

        }

        BlockingQueue<Message> temp = config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getRcvQueue();


        for (int i = 0; i < config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getNumberOfNbrs(); i++) {


            List<Integer> edgeWt = config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getEdgesToNbrs();
            try {
                Message rcvdMsg = temp.take();
                System.out.println("Node : " + this.nodeId + " : " +
                        "rcvd : " + " from " + rcvdMsg.getNodeId() + " dist is : " + rcvdMsg.getDist());
//
//                distance[u] + w < distance[v]:
//                distance[v] := distance[u] + w
//                predecessor[v] := u

                String myPred = config.getNodes().get(Integer.parseInt(this.nodeId) - 1).getPred().toString();
                if (rcvdMsg.getDist() != Integer.MAX_VALUE &&
                        rcvdMsg.getDist() + edgeWt.get(Integer.valueOf(rcvdMsg.getNodeId()) - 1) < this.dist) {


                    this.dist = rcvdMsg.getDist() + edgeWt.get(Integer.valueOf(rcvdMsg.getNodeId()) - 1);
                    config.getNodes().get(Integer.valueOf(this.nodeId) - 1).pred = rcvdMsg.getNodeId();
                    System.out.println("Updated dist : for : " +
                            this.nodeId + "\t" + this.dist + " : pred:  " + config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getPred() + " in round : " + msg.getRoundNumber());

                    Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.DONE, msg.getRoundNumber());
                    config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId()) - 1).getRoundStatus().put(roundStatusMsg);
                } else if (rcvdMsg.getDist() != Integer.MAX_VALUE) {
                    if (this.getReadyToterminate() && rcvdMsg.getNodeId().toString().equals(myPred) && !myPred.equals("unknown")) {

                        System.out.println("sending terminate : from node : "+this.nodeId + " to node : "+rcvdMsg.getNodeId()+ " in round  : "+msg.getRoundNumber());
                        Message terminationMsg = new Message(this.nodeId, Message.MessageType.TERMINATE, msg.getRoundNumber());
                        config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId()) - 1).getRoundStatus().put(terminationMsg);
                    } else {
                        Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.REJECT, msg.getRoundNumber());
                        config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId()) - 1).getRoundStatus().put(roundStatusMsg);
                    }

                }//else if(rcvdMsg.getDist()==Integer.MAX_VALUE){

                else {


                    if (this.getReadyToterminate() && rcvdMsg.getNodeId().toString().equals(myPred) && !myPred.equals("unknown")) {

                        System.out.println("sending terminate in ignore block : from node : "+this.nodeId + " to node : "+rcvdMsg.getNodeId()+ " in round  : "+msg.getRoundNumber());
                        Message terminationMsg = new Message(this.nodeId, Message.MessageType.TERMINATE, msg.getRoundNumber());
                        config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId()) - 1).getRoundStatus().put(terminationMsg);
                    } else {
                        Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.IGNORE, msg.getRoundNumber());
                        config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId()) - 1).getRoundStatus().put(roundStatusMsg);
                    }

                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        //relaxation


    }

    public Boolean getReadyToterminate() {
        return readyToterminate;
    }

    public void setReadyToterminate(Boolean readyToterminate) {
        this.readyToterminate = readyToterminate;
    }
}

