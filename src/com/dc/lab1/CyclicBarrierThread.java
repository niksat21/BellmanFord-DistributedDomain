package com.dc.lab1;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by niksat21 on 2/16/2017.
 */

class Master1 extends Thread{


    public void run(){
        System.out.println("in master");
    }

}

class Slave extends Thread{

    CyclicBarrier barrierPoint ;

    public Slave(CyclicBarrier barrierPoint, String name){

        this.setName(name);
        this.barrierPoint=barrierPoint;
        this.start();
    }

    public void run(){
        System.out.println("entered : "+getName());

        try {
            barrierPoint.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}


public class CyclicBarrierThread {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("start of main");
        CyclicBarrier cb = new CyclicBarrier(2,new Master1());

        for(int i=0;i<2;i++){
            Thread.sleep(100);
            new Slave(cb,String.valueOf(i));
        }

        System.out.println("end of main");


    }
}
