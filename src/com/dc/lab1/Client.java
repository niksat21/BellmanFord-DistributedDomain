package com.dc.lab1;

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
    private Boolean isClientDone = false;
    private Boolean isLeader = false;
    private Integer dist;

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
            while(i<2){

                Message msg = takingQueue.take();
//                System.out.println("Client : "+this.nodeId+" \t"+msg.getRoundNumber());
                Thread.sleep(100);
                //TODO add bellman ford login
                clientWorker(msg);

                puttingQueue.put(new Message(this.nodeId, Message.MessageType.ROUNDEND, msg.getRoundNumber()));
                System.out.println("sent RoundEND from : " + this.nodeId + "round no : " + msg.getRoundNumber());
                i++;

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

            try {
                Message rcvdMsg = temp.take();
                System.out.println("Node : "+this.nodeId+ " : " +
                        "rcvd : "+" from " + rcvdMsg.getNodeId()+" dist is : "+rcvdMsg.getDist());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }



        //relaxation


    }

}

