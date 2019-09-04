/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class FileHeaderChecker extends Thread{
    
    ArrayList<String> header=new ArrayList<>();
    String logFile="test.log";
    int checkedYet;
    InetAddress address;
    int port;
    String fileName;
    private int maxHeaderLen;
    
    @Override
    public void run(){
        checkedYet=0;
        File file=new File(logFile);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                //Logger.getLogger(FileHeaderChecker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        loadConfiguration();
        readHeaders();
        
        
        log("Checking .......... "+fileName);
        for (String headerStr : header) {
            checkHeader(headerStr);
            checkedYet++;
            log(checkedYet+":" + headerStr);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            DatagramSocket socket=new DatagramSocket(2434+checkedYet);
            for (int i=0;i<10;i++){
                socket.send(createPacket(address,port,200,"0000000000000000000000000000000000000000000000000000000000000000"));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        log("CONGRATULATION");
        log("FINISHED");
            
        
    }
    private void loadConfiguration(){
        String fileName="config.cfg";
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            try {
                address=InetAddress.getByName(line);
            } catch (UnknownHostException ex) {
            }
            line = bufferedReader.readLine();
            port=Integer.parseInt(line);
            line = bufferedReader.readLine();
            this.fileName=line;
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            //System.out.println("Unable to open file '" +fileName + "'");
            log("Unable to open file '"+fileName+"'"+ex);
        }
        catch(IOException ex) {
            //System.out.println("Error reading file '"+ fileName + "'");
            log("Error reading file '"+fileName+"'"+ex);
        }
    }
    
    private void readHeaders(){
        
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            maxHeaderLen = 100;
            while((line = bufferedReader.readLine()) != null) {
                if(line.length()<1)continue;
                if(line.length()>maxHeaderLen)line = line.substring(0,maxHeaderLen);
                header.add(line);
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            //System.out.println("Unable to open file '" +fileName + "'");
            log("Unable to open file '"+fileName+"'"+ex);
        }
        catch(IOException ex) {
            //System.out.println("Error reading file '"+ fileName + "'");
            log("Error reading file '"+fileName+"'"+ex);
        }
    }
    
    public void log(String message){
        String newMessage=message+"\r\n";
        try {
            Files.write(Paths.get(logFile), newMessage.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }
    private void checkHeader(String header){
        try {
            DatagramSocket socket=new DatagramSocket(2434+checkedYet);
            new Receiver(socket,header).start();
            int i=0;
            while (true){
                Thread.sleep(200);
                
                socket.send(createPacket(address,port,200,header));
                //System.out.println("Packet sent len 200 count ------------ "+ i);
                i++;
                if(i>=100)break;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private class Receiver extends Thread{
        DatagramSocket socket;
        String header;
        public Receiver(DatagramSocket s,String header) {
            socket = s;
            this.header = header;
        }
        
        @Override
        public void run(){
            try {
                socket.setSoTimeout(1000);
                int i=0;
                DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
                for(;i<100;i++){
                    socket.receive(packet);
                }
                if(i>90){
                    header="\n\n["+i+"] = "+header;
                    try {
                        Files.write(Paths.get("Working Header.txt"), header.getBytes(), StandardOpenOption.APPEND);
                    }catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }
                }
            } catch (Exception e) {
            }
        }
        
    }
    public static DatagramPacket createPacket(InetAddress address, int port, int len,String h){
        byte [] data,header;
        header=Functions.hexStringToByteArray(h);
        data=Functions.getRandomData(len);
        data=Functions.concatenateByteArrays(header,data);
        return new DatagramPacket(data,data.length,address,port);
    }
}
