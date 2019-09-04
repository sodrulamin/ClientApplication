/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BSHangTest;


/**
 *
 * @author Sodrul Amin Shaon
 */
public class ProcessorThread extends Thread{
    CreatorThread creatorThread;
    private static int ThreadCount = 0;
    private int myId;
    public ProcessorThread(CreatorThread t) {
        creatorThread = t;
        myId = ThreadCount++;
    }
    
    
    @Override
    public void run(){
        
        System.out.println("Processor thread ["+myId+"] started successfully..");
        while(true){
            creatorThread.print(myId);
        }
 
    }
    
    
    
    
}
