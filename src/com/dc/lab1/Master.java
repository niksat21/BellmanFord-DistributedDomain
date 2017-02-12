package com.dc.lab1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;


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

            nodeId=Integer.valueOf(System.getProperty("nodeId"));
            String hostname = InetAddress.getLocalHost().getHostName();

            logger.info("NodeID:{} Hostname:{}",nodeId,hostname);

            Node node = config.getNodes().get(nodeId);
            NodeLocation nodeLoc =config.getNodeLocs().get(nodeId);


//            TODO
// create server and client threads here and run them please implement this

//            Server server = new Server(nodeId,nodeLoc.getPort(),config);
//            Client client = new Client(hostname,config,nodeId);

//            Thread clientThread = new Thread(client, "client-thread");
//            Thread serverThread = new Thread(server, "server-thread");
//


        }catch (Exception e){
            logger.error("Exception in master : ",e);
        }
    }
}
