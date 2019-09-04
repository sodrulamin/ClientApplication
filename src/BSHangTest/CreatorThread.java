/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BSHangTest;

import BSHangTest.ProcessorThread;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class CreatorThread extends Thread{
    public LinkedList<String> al=new LinkedList<String>();  
    Random random;
    private static byte [] characters="abcdefghijklmnopqrstuvwxyz".getBytes();
    private static int count = 0;
    private ProcessorThread [] processorThreads;
    private boolean locked;
    
    public CreatorThread() {
        random=new Random();
        processorThreads = new ProcessorThread[100];
        locked=false;
    }
    
    @Override
    public void run(){
        for(int i=0;i<processorThreads.length;i++){
            processorThreads[i]=new ProcessorThread(this);
            processorThreads[i].start();
        }
        while(true){
            try {
                Thread.sleep(200);
                String str=getRandomWord()+"--"+count++;
                
                synchronized(this){
                    al.add(str);
                    if(al.size()==1){
                        notify();
                        System.out.println("Calling notify()");
                    }
                    System.out.println("Queue size: "+al.size());
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(CreatorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    public void print(int threadCount){
        while(locked);
        synchronized(this){
            locked=true;
            if(al==null || al.isEmpty()){
                System.out.println("Going to wait ["+threadCount+"]");
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CreatorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Thread ["+threadCount+"] processed: "+al.removeFirst());
            locked=false;
        }
        
    }
    
    private String getRandomWord(){
        int len=5+random.nextInt(10);
        byte [] value=new byte[len];
        for(int i=0;i<len;i++){
            value[i]=characters[random.nextInt(characters.length)];
        }
        return new String(value);
    }
}
