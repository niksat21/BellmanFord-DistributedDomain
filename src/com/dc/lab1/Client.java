package com.dc.lab1;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by niksat21 on 2/17/2017.
 */
public class Client implements Runnable{


//    Client client  = new Client(config.getNodes().get(i),
//            config.getLeader(),config.getNodes().get(i).getEdgesToNbrs()
//            ,new ArrayBlockingQueue<MessageType>(1).put(MessageType.ROUNDSTART),
//            takingQueue);

    private String nodeId;
    private String Leader;
    private List<Integer> edgeList;
    private BlockingQueue<Message> takingQueue;
    private BlockingQueue<Message> puttingQueue;

    public Client(String nodeId, String leader, List<Integer> edgeList,
                  BlockingQueue<Message> takingQueue,
                  BlockingQueue<Message> puttingQueue) {
        this.nodeId = nodeId;
        Leader = leader;
        this.edgeList = edgeList;
        this.takingQueue = takingQueue;
        this.puttingQueue = puttingQueue;
    }


    @Override
    public void run() {

        try {
            int i=0;
            while(i<2){
                Message msg = takingQueue.take();
                System.out.println("Client : "+this.nodeId+" \t"+msg.getRoundNumber());
                Thread.sleep(100);


                puttingQueue.add(new Message(this.nodeId,Message.MessageType.ROUNDEND,msg.getRoundNumber()));
                System.out.println("sent RoundEND from : "+this.nodeId +"round no : "+msg.getRoundNumber());
                i++;

            }






        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

