package com.dc.lab1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public static Boolean isDone=false;
    private HashSet<Integer> deadNodes;
    private Boolean killMaster = false;
    public static void main(String[] args) {

        Master master = new Master();
        master.masterWorker();
    }

    public void masterWorker(){

        try{

            ConfigParser parser = new ConfigParser();
            Config config = parser.getConfig();
            deadNodes = new HashSet<Integer>();
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
                Client client  = new Client(config,config.getNodes().get(i).getNodeID(),
                        config.getLeader(),config.getNodes().get(i).getEdgesToNbrs(),
                        blockingQueueList.get(i),takingQueue);
                blockingQueueList.get(i).put(new Message("Master",Message.MessageType.ROUNDSTART,round));
                System.out.println("starting thread : "+config.getNodes().get(i).getNodeID());
                new Thread(client).start();
            }


            int i=0;
            while(!this.killMaster){
//            while(!Master.getDone()){

                //if(takingQueue.size()>0){
                    while(!takingQueue.isEmpty()){
                        Message msg = takingQueue.take();
                        if(msg.getMsgType().toString().equals("KILL")){
                        	deadNodes.add(Integer.parseInt(msg.getNodeId())-1);
                        	System.out.println("DeAD: "+msg.getNodeId());
                        	killMaster =Boolean.TRUE;
                        }
                    }
                    System.out.println("MAster : round : "+round + "finished");
                    System.out.println("MAster : starting new round : "+(round+1));
                    round++;
                    if(!killMaster){
                    	for(BlockingQueue q : blockingQueueList){		
                            q.put(new Message("Master", Message.MessageType.ROUNDSTART,round));
                        }
                    }
                    else{
                    	for(int j=0;j<blockingQueueList.size();j++){
                        	if(!deadNodes.contains(j)){
                        		blockingQueueList.get(j).put(new Message("Master", Message.MessageType.KILL,round));
                        	}
                        }
                    	System.out.println("Master killed");
                    }
                    
                    i++;
                //}
            }

        }catch (Exception e){
            //logger.error("Exception in master : ",e);
        	System.out.println("Exception in master : "+e.getMessage());
        }
    }

    public static  Boolean getDone() {
        return isDone;
    }

    public static void setDone(Boolean done) {
        isDone = done;
    }
}
