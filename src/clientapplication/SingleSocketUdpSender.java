/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapplication;

import dialertest.RegistrationMessage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import sun.misc.VM;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class SingleSocketUdpSender extends Thread{
    DatagramSocket socket;
    String addressString = "65.99.254.5";
    long sleep = 200;
    int packetSize = 200;
    RegistrationMessage registrationMessage;

    public SingleSocketUdpSender() throws SocketException {
        socket = new DatagramSocket(54044);
    }
    
    @Override
    public void run(){
        try{
            registrationMessage = new RegistrationMessage();
            byte [] data = registrationMessage.getRegistrationMessage();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(addressString), 49);
            new SingleSocketUdpReceiver().start();
            while(true){
                socket.send(packet);
                Thread.sleep(sleep);
            }
        }catch(Exception e){
            
        }
    }
    
    private class SingleSocketUdpReceiver extends Thread{
        @Override
        public void run(){
            DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
            String reply;
            try{
                while(true){
                    socket.receive(packet);
                    reply = registrationMessage.parseMessage(packet.getData(), packet.getLength());
                    System.out.println("Reply found:\n"+reply);
                }
            }catch(Exception e){
                
            }
        }
    }
}
