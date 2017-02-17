package com.dc.lab1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * Created by niksat21 on 2/12/2017.
 */
public class Master {

    private static Integer nodeId;
    private static Logger logger = LogManager.getLogger(Master.class);
    public static void main(String[] args){

        try{

            ConfigParser parser = new ConfigParser();
            Config config = parser.getConfig();
//
//            nodeId=Integer.valueOf(System.getProperty("nodeId"));
//            String hostname = InetAddress.getLocalHost().getHostName();
//
//            logger.info("NodeID:{} Hostname:{}",nodeId,hostname);
//
//            Node node = config.getNodes().get(nodeId);
//            NodeLocation nodeLoc =config.getNodeLocs().get(nodeId);


//            TODO
// create server and client threads here and run them please implement this

//            Server server = new Server(nodeId,nodeLoc.getPort(),config);
//            Client client = new Client(hostname,config,nodeId);

//            Thread clientThread = new Thread(client, "client-thread");
//            Thread serverThread = new Thread(server, "server-thread");


            List<BlockingQueue<Message>> blockingQueueList = new ArrayList<>();
            BlockingQueue<Message> takingQueue = new ArrayBlockingQueue<>(config.getNoOfNodes());
            Integer round =0;
            System.out.println("MAster : starting new round : "+round);
            for(int i=0;i<config.getNoOfNodes();i++){

                blockingQueueList.add(new ArrayBlockingQueue<Message>(1));
                Client client  = new Client(config.getNodes().get(i).getNodeID(),
                        config.getLeader(),config.getNodes().get(i).getEdgesToNbrs(),
                        blockingQueueList.get(i),takingQueue);
                blockingQueueList.get(i).put(new Message("Master",Message.MessageType.ROUNDSTART,round));
                System.out.println("starting thread : "+config.getNodes().get(i).getNodeID());
                new Thread(client).start();


            }


            int i=0;
            while(i<2){

                if(takingQueue.size()==config.getNoOfNodes()){
                    while(!takingQueue.isEmpty()){
                        Message msg = takingQueue.take();
                        System.out.println("Master got : End from : "+msg.getNodeId());

                    }
                    System.out.println("MAster : round : "+round + "finished");
                    System.out.println("MAster : starting new round : "+round);
                    round++;
                    for(BlockingQueue q : blockingQueueList){

                        q.put(new Message("Master", Message.MessageType.ROUNDSTART,round));
                    }
                    i++;

                }

            }

        }catch (Exception e){
            logger.error("Exception in master : ",e);
        }
    }
}
