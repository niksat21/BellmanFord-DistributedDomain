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
    private Boolean readyToterminate=false;

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
            int i=0;

           // while (!isClientDone) {
            while(i<5){

                Message msg = takingQueue.take();
//                System.out.println("Client : "+this.nodeId+" \t"+msg.getRoundNumber());
                Thread.sleep(100);
                //TODO add bellman ford login
                clientWorker(msg);

                checkRoundStatus(msg);

                if(readyToterminate)
                    checkForTermination(msg);


                puttingQueue.put(new Message(this.nodeId, Message.MessageType.ROUNDEND, msg.getRoundNumber()));
                System.out.println("sent RoundEND from : " + this.nodeId + "round no : " + msg.getRoundNumber());
                i++;

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkForTermination(Message msg) throws InterruptedException {
        BlockingQueue<Message> terminationTemp = config.getNodes().get(Integer.parseInt(this.nodeId)-1).getTerminationDetectionQueue();
        Integer noOfNbrs =config.getNodes().get(Integer.valueOf(this.nodeId)-1).getNumberOfNbrs();
        if(terminationTemp.size()==2*noOfNbrs){
            List<String> nbrs = config.getNodes().get(Integer.parseInt(this.nodeId)-1).getNbrs();
            for(int i=0;i<config.getNodes().get(Integer.valueOf(this.nodeId)-1).getNumberOfNbrs();i++){
                Message terminationMsg = new Message(this.nodeId, Message.MessageType.TERMINATE,msg.getRoundNumber());
                config.getNodes().get(Integer.parseInt(nbrs.get(i))).getTerminationDetectionQueue().put(terminationMsg);
            }
        }


    }

    private void checkRoundStatus(Message msg) throws InterruptedException {

        Integer count=0;
        BlockingQueue<Message> roundStatusTemp = config.getNodes().get(Integer.parseInt(this.nodeId)-1).getRoundStatus();
        System.out.println("size of roundstatus queue : "+roundStatusTemp.size()+" for node : "+this.nodeId);
        for(int i=0;i<config.getNodes().get(Integer.valueOf(this.nodeId)-1).getNumberOfNbrs();i++){


            Message roundStatusForME = roundStatusTemp.take();
            if(roundStatusForME.getMsgType().equals("REJECT"))
               count++;

        }

        if(Objects.equals(count, config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getNumberOfNbrs())){

            readyToterminate=Boolean.TRUE;
            System.out.println("Termination detected by node : "+this.nodeId + " in round : "+msg.getRoundNumber());
            List<String> nbrs = config.getNodes().get(Integer.parseInt(this.nodeId)-1).getNbrs();
            for(int i=0;i<config.getNodes().get(Integer.valueOf(this.nodeId)-1).getNumberOfNbrs();i++){
                Message terminationMsg = new Message(this.nodeId, Message.MessageType.TERMINATE,msg.getRoundNumber());
                config.getNodes().get(Integer.parseInt(nbrs.get(i))).getTerminationDetectionQueue().put(terminationMsg);
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

        BlockingQueue<Message> temp = config.getNodes().get(Integer.valueOf(this.nodeId)-1).getRcvQueue();


        for(int i=0;i<config.getNodes().get(Integer.valueOf(this.nodeId)-1).getNumberOfNbrs();i++){


            List<Integer> edgeWt = config.getNodes().get(Integer.valueOf(this.nodeId)-1).getEdgesToNbrs();
            try {
                Message rcvdMsg = temp.take();
                System.out.println("Node : "+this.nodeId+ " : " +
                        "rcvd : "+" from " + rcvdMsg.getNodeId()+" dist is : "+rcvdMsg.getDist());
//
//                distance[u] + w < distance[v]:
//                distance[v] := distance[u] + w
//                predecessor[v] := u

                if(rcvdMsg.getDist()!=Integer.MAX_VALUE &&
                        rcvdMsg.getDist()+edgeWt.get(Integer.valueOf(rcvdMsg.getNodeId())-1)<this.dist) {


                    this.dist = rcvdMsg.getDist() + edgeWt.get(Integer.valueOf(rcvdMsg.getNodeId()) - 1);
                    config.getNodes().get(Integer.valueOf(this.nodeId) - 1).pred = rcvdMsg.getNodeId();
                    System.out.println("Updated dist : for : " +
                            this.nodeId + "\t" + this.dist + " : pred:  " + config.getNodes().get(Integer.valueOf(this.nodeId) - 1).getPred() + " in round : " + msg.getRoundNumber());

                    Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.DONE,msg.getRoundNumber());
                    config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId())-1).getRoundStatus().put(roundStatusMsg);
                }else if(rcvdMsg.getDist()!=Integer.MAX_VALUE &&
                        rcvdMsg.getDist()+edgeWt.get(Integer.valueOf(rcvdMsg.getNodeId())-1)>=this.dist){
                    Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.REJECT,msg.getRoundNumber());
                    config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId())-1).getRoundStatus().put(roundStatusMsg);
                }else if(rcvdMsg.getDist()==Integer.MAX_VALUE){

                        Message roundStatusMsg = new Message(this.nodeId, Message.MessageType.IGNORE,msg.getRoundNumber());
                        config.getNodes().get(Integer.parseInt(rcvdMsg.getNodeId())-1).getRoundStatus().put(roundStatusMsg);
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }



        //relaxation


    }

}

