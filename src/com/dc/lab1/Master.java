package com.dc.lab1;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by niksat21 on 2/12/2017.
 */
public class Master {

    
    private static Logger logger = Logger.getLogger(Master.class.getName());
    //This will be the synchronizer class that will define the rounds for each of the threads
    //that act as nodes.
    
    private static int numberOfNodes;
    private static List<Node> nodeList;
    private static volatile boolean[] roundDonePermissions;
    private static int numberOfRounds;
    private static volatile boolean proceedToNextRound;
    private static volatile int currentRound;
    private static volatile boolean endOperation;
    private static clientThread[] threadPool;
    private static volatile boolean[] threadIntoNextRound;
    
    private static volatile List<ArrayBlockingQueue<Message>> messageQueues;
    
    public static void main(String[] args){

        try{

            ConfigParser parser = new ConfigParser();
            Config config = parser.getConfig();
            numberOfRounds=10;
            currentRound=0;
            endOperation=false;
            //nodeId=Integer.valueOf(System.getProperty("nodeId"));
            //String hostname = InetAddress.getLocalHost().getHostName();

            logger.info("Round Synchronizer started");
            numberOfNodes = config.getNoOfNodes();
            nodeList = new ArrayList<Node>();
            nodeList = config.getNodes();
            messageQueues = new ArrayList<ArrayBlockingQueue<Message>>();
            
            
            
            
            for(int i=0;i<numberOfNodes;i++){
            	
            	Node node = nodeList.get(i);
            	messageQueues.add(new ArrayBlockingQueue<Message>(1));
            	clientThread client = new clientThread(node.getNodeID(),node.nbrs,node.getEdgesToNbrs(),messageQueues.get(i));
            	Thread thread = new Thread(client);
            	thread.setName("thread"+node.getNodeID());
            	thread.start();
            	System.out.println(node.getNodeID()+" Thread Started");
            	Thread.currentThread().sleep(1000);
            }
            
            System.out.println("--------------");
            
            while(currentRound<numberOfRounds){
            	System.out.println(currentRound+" Round Started in Master");
            	
            	while(!checkQueues()){
            		Thread.currentThread().sleep(5);
            	}
            	
            	//System.out.println("Wait for next round");
            	//Thread.currentThread().sleep(2000);
            	clearQueues();
            	currentRound++;
            }
            endOperation=true;



//            TODO
// create server and client threads here and run them please implement this

//            Server server = new Server(nodeId,nodeLoc.getPort(),config);
//            Client client = new Client(hostname,config,nodeId);

//            Thread clientThread = new Thread(client, "client-thread");
//            Thread serverThread = new Thread(server, "server-thread");
//


        }catch (Exception e){
            logger.log(Level.SEVERE, "Exception in master : "+e);
        }
    }
    
    private static boolean checkQueues(){
    	for(int i=0;i<messageQueues.size();i++){
    		if(messageQueues.get(i).size()==0)
    			return false;
    	}
    	return true;
    }
    
    private static void clearQueues(){
    	for(int i=0;i<messageQueues.size();i++){
    		try {
				Message remove = messageQueues.get(i).take();
				System.out.println(remove.getNodeId()+" removed from queue at round "+remove.getRound());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public static synchronized int getCurrentGlobalRound(){
    	return currentRound;
    }

    public static synchronized boolean getEndOperation(){
    	return endOperation;
    }
}
