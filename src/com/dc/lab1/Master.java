package com.dc.lab1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * Created by niksat21 on 2/12/2017.
 */
public class Master {

    private static Integer nodeId;
    //private static Logger logger = LogManager.getLogger(Master.class);
    private static Boolean isDone = false;
    private HashSet<String> terminationQueue;

    public static void main(String[] args) {

        Master master = new Master();
        master.masterWorker();
    }

    public void masterWorker() {

        try {

            ConfigParser parser = new ConfigParser();
            Config config = parser.getConfig();
            this.terminationQueue = new HashSet<String>();
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

            Integer round = 0;
            System.out.println("Master : starting new round : " + round);
            for (int i = 0; i < config.getNoOfNodes(); i++) {


                blockingQueueList.add(new ArrayBlockingQueue<Message>(1));
                Client client = new Client(config, config.getNodes().get(i).getNodeID(),
                        config.getLeader(), config.getNodes().get(i).getEdgesToNbrs(),
                        blockingQueueList.get(i), takingQueue, terminationQueue);
                blockingQueueList.get(i).put(new Message("Master", Message.MessageType.ROUNDSTART, round));
//                System.out.println("starting thread : "+config.getNodes().get(i).getNodeID());
                new Thread(client).start();
            }


            int i = 0;
            while(!isDone){
//            while (!Master.getDone()) {
                if(terminationQueue.size()== config.getNoOfNodes()){
                    System.out.println("KILL by all in round: "+round);
                    isDone= Boolean.TRUE;
                }
                if (takingQueue.size() == config.getNoOfNodes()) {
                    System.out.println("1..............."+round);
                    while (!takingQueue.isEmpty()) {
                        Message msg = takingQueue.take();
                    }
                    System.out.println("MAster : round : " + round + "finished");
                    round++;
                    System.out.println("MAster : starting new round : " + round);
                    if(isDone){
                        System.out.println("came inside isdone in master");
                        for (BlockingQueue q : blockingQueueList) {

                                q.put(new Message("Master", Message.MessageType.KILL, round));

                        }
                    }else{
                        for (BlockingQueue q : blockingQueueList) {
                            q.put(new Message("Master", Message.MessageType.ROUNDSTART, round));
                        }
                    }

                    i++;

                }


        }


    } catch (Exception e) {
            //logger.error("Exception in master : ",e);
            e.printStackTrace();
            System.out.println("Exception in master : " + e.getMessage());
        }
        System.out.println("master exiting");
    }

    public static Boolean getDone() {
        return isDone;
    }

    public static void setDone(Boolean done) {
        isDone = done;
    }

    public void addToHashSet(String nodeId){
        synchronized (this){
            if(!terminationQueue.contains(nodeId))
                terminationQueue.add(nodeId);
        }
    }
}
