package com.dc.lab1;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class clientThread implements Runnable{
	private static String nodeId;
	private static Map<String,Integer> edgesToNeighbors;
	private static int roundNumber;
	private static ArrayBlockingQueue<Message> queueInMaster;
	
	public clientThread(String nodeId, List<String> nodeList, List<Integer> edgeList, ArrayBlockingQueue<Message> queue){
		this.nodeId = nodeId;
		this.edgesToNeighbors = new HashMap<String,Integer>();
		for(int i =0;i< nodeList.size();i++){
			edgesToNeighbors.put(nodeList.get(i), edgeList.get(i));
		}
		queueInMaster = queue;
		writeLog("Thread initialized");
		roundNumber =0;
	}

	@Override
	public void run() {
		writeLog("Thread starting");
		while(!Master.getEndOperation()){
			
			writeLog("Thread round:"+ roundNumber+" Global round:"+Master.getCurrentGlobalRound());
			//System.out.println(nodeId+" round:"+roundNumber+" Global round:"+Master.getCurrentGlobalRound());
			
			try {
				/////BELL MAN FORD PROCESS HERE
				
				////BLOCKING CALL will wait for master to clear queue
				
				while(queueInMaster.size() ==1){
					Thread.currentThread().sleep(100);
				}
				writeLog(queueInMaster.size()+" node: "+nodeId + " round: "+roundNumber);
				Message msg = new Message(nodeId,roundNumber);
				queueInMaster.put(msg);
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Fail 1");
				writeLog("Thread sleep failed at round:"+roundNumber);
			}
			roundNumber++;
		}
		writeLog("Program ended");
	}
	

	
	private void writeLog(String msg) {
		Writer writer;
		try {
			FileOutputStream FoutStream = new FileOutputStream(
					"outputFiles/threadFile" + nodeId + ".txt", true);
			try {
				writer = new BufferedWriter(
						new OutputStreamWriter(FoutStream, "UTF-8"));
				writer.append(msg);
				writer.append("\n");
				writer.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				FoutStream.close();
			}
		} catch (Exception e) {
		}
	}


}
