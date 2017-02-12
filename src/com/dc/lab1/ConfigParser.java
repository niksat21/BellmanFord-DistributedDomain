package com.dc.lab1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by niksat21 on 2/12/2017.
 */

public class ConfigParser {

    private static Logger logger = LogManager.getLogger(ConfigParser.class);
    private String fileLocation = System.getProperty("config", "conf/config.txt");
    private Config config;

    public ConfigParser() throws IOException {
        parseConfig();
    }

    private void parseConfig() throws IOException {

        List<String> fileContent = Files.readAllLines(Paths.get(fileLocation));
        Iterator<String> iterator = fileContent.iterator();
        Integer noOfNodes;
        List<String> nodeID = new ArrayList<>();
        List<String> nbrs;
        List<Node> nodes = new LinkedList<>();
        List<NodeLocation> nodeLocations = new LinkedList<>();
        List<Integer> edgeToNbrs;
        Map<String, List<Integer>> weightMatrix = new HashMap<>();
        String leader = "";

        String line = getNextLine(iterator);

        // Ignore comments
        while (line.startsWith("#") || line.isEmpty()) {
            line = getNextLine(iterator);
        }

        noOfNodes = Integer.valueOf(line);

        line = getNextLine(iterator);
//        line.replaceAll("\\[","");line.replaceAll("\\]","");
        String[] temp = line.split("\\,");
        for (int i = 0; i < noOfNodes; i++) {
            nodeID.add(temp[i]);
        }


        line = getNextLine(iterator);

        //
        for (int i = 0; i < noOfNodes && iterator.hasNext(); line = getNextLine(iterator)) {

            nbrs = new ArrayList<>();
            edgeToNbrs = new ArrayList<>();
            // Ignore comments
            if (line.startsWith("#") || line.isEmpty())
                continue;

            String[] split = line.split("\\,");
            for (int j = 0; j < noOfNodes; j++) {
                if (Integer.valueOf(split[j]) != -1) {
                    nbrs.add(nodeID.get(j));
                    edgeToNbrs.add(Integer.valueOf(split[j]));
                } else {
                    edgeToNbrs.add(Integer.valueOf(split[j]));
                }
            }

            Node node = new Node(nodeID.get(i), nbrs, nbrs.size(), edgeToNbrs);

            nodes.add(node);

            weightMatrix.put(nodeID.get(i), edgeToNbrs);


            i++;
        }


        leader = line.trim();

        line= getNextLine(iterator);
        for (int i = 0; i < noOfNodes && iterator.hasNext(); line = getNextLine(iterator)) {
            // Ignore comments

            if (line.startsWith("#") || line.isEmpty())
                continue;

            String[] split = line.split("\\s+");

            NodeLocation nodeLoc = new NodeLocation(split[0], split[1], Integer.valueOf(split[2]));

            nodeLocations.add(nodeLoc);
            i++;
        }


        config = new Config(noOfNodes, nodeID, nodes, weightMatrix, leader,nodeLocations);


    }

    private String getNextLine(Iterator<String> iterator) {
        String line = null;
        while (iterator.hasNext()) {
            line = iterator.next();
            line = line.trim();
            if (line.startsWith("#") || line.isEmpty())
                continue;
            else
                break;
        }
        return line;
    }


    public Config getConfig() {
        return config;
    }


    public static void main(String[] args) {
        try {
            ConfigParser parser = new ConfigParser();
            Config config2 = parser.getConfig();

            System.out.println("number of nodes : " + config2.getNoOfNodes());
            System.out.println("leader : " + config2.getLeader());
            List<Node> n = config2.getNodes();
            System.out.println("node id :" + n.get(0).getNodeID());
            List<Integer> ed = n.get(0).getEdgesToNbrs();
            System.out.println("size of nbr: " + ed.size());
            for (int z = 0; z < ed.size(); z++)
                System.out.println(ed.get(z));

            System.out.println(config2.getNodeLocs().get(0).getHostName());
            System.out.println(config2.getNodeLocs().get(4).getPort());
            System.out.println("Done");
        } catch (IOException e) {
            logger.error(e);
        }
    }


}
