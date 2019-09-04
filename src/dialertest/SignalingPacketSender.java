/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialertest;

import clientapplication.Base64;
import clientapplication.Functions;
import com.sun.org.apache.xalan.internal.xsltc.trax.OutputSettings;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class SignalingPacketSender {
    //private static String host = "181.41.196.244";
    private static String host = "149.14.142.66";

    private static int port = 850;
    public static int count = 10;
    public static int size = 500;
    public static int delay = 450;

    public static int sent = 0;
    public static int receive = 0;
    byte[] receivedData = new byte[2048];
    DatagramSocket socket;
    Socket tSocket;
    InetAddress remoteAddress;
    InputStream is;
    OutputStream os;
    int switcher;
    
    public SignalingPacketSender(int n){
        switcher = n;
        switch(n){
            case 1:
                initiateTcpSocket();
                break;
            default:
                initiateDatagramSocket();
                break;
        }
        
    }
    private void initiateTcpSocket(){
        try {
            tSocket = new Socket(InetAddress.getByName(host),port);
            is = tSocket.getInputStream();
            os = tSocket.getOutputStream();
        } catch (Exception e) {
        }
    }
    
    private void initiateDatagramSocket(){
        try {
            socket = new DatagramSocket();
            remoteAddress = InetAddress.getByName(host);
        } catch (SocketException ex) {
            Logger.getLogger(SignalingPacketSender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(SignalingPacketSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendRequest(){
        try{
            RegistrationMessage registrationMessage = new RegistrationMessage();
            byte [] msg = registrationMessage.getUdpRegistrationMessage();
            byte [] recvData = new byte[2048];
            byte [] sendData = new byte[2048];
            int receiveLen = 0,sendDataLen = 0;
            String str ;
            StunImplementation stunImplementation = new StunImplementation();
            DatagramPacket packet;
            byte [] data;
            switch(switcher){
                case 0:
                    packet = new DatagramPacket(msg, msg.length, remoteAddress, port+14);
                    socket.send(packet);
                    packet.setData(recvData);
                    socket.receive(packet);
                    str = registrationMessage.parseMessage(packet.getData(), packet.getLength());
                    System.out.println("Received:\n"+str);
                    break;
                case 1:
                    msg = HttpProtocolClass.getRequest(msg, msg.length);
                    os.write(msg);
                    receiveLen = HttpProtocolClass.receiveClientData(is,receivedData);
                    receiveLen = Base64.base64Decode(receivedData, 0, receiveLen);
                    str = registrationMessage.parseMessage(receivedData, receiveLen);
                    System.out.println("Received:\n"+str);
                    break;
                case 2:
                    System.arraycopy(msg, 0, sendData, 0, msg.length);
                    sendDataLen = stunImplementation.createStunFromData(sendData, 0, msg.length);
                    packet = new DatagramPacket(sendData, 0, sendDataLen);
                    packet.setAddress(remoteAddress);
                    packet.setPort(port);
                    socket.send(packet);
                    packet.setData(receivedData);
                    socket.receive(packet);
                    receiveLen = stunImplementation.decodeStunPacket(receivedData, 0, packet.getLength());
                    str = registrationMessage.parseMessage(receivedData, receiveLen);
                    System.out.println("Received:\n"+str);
                    break;
                case 3:
                    sendDataLen = stunImplementation.createDummyStun(sendData);
                    packet = new DatagramPacket(sendData, sendDataLen);
                    packet.setAddress(remoteAddress);
                    packet.setPort(port);
                    socket.send(packet);
                    socket.send(packet);
                    socket.send(packet);
                    packet.setData(msg);
                    socket.send(packet);
                    packet.setData(receivedData);
                    socket.receive(packet);
                    str = registrationMessage.parseMessage(receivedData, packet.getLength());
                    System.out.println("Received:\n"+str);
                    break;
                case 4:
                    data = new byte[2048];
                    msg = RegistrationMessage.message.getBytes();
                    System.arraycopy(msg, 0, data, 0, msg.length);
                    int len = AdvancedEncoder.encodeBytes(data, 0, msg.length, AdvancedEncoder.keys);
                    //System.out.println("sending: "+Functions.bytesToHex(data,len));
                    packet = new DatagramPacket(data, len, remoteAddress, port+26);
                    socket.send(packet);
                    packet.setData(receivedData);
                    socket.receive(packet);
                    len = AdvancedEncoder.decodeBytes(receivedData, 0, packet.getLength(), AdvancedEncoder.keys);
                    str = new String(receivedData,0,len);
                    System.out.println(str);
                    break;
                case 5:
                    data = new byte[2048];
                    msg = RegistrationMessage.message.getBytes();
                    System.arraycopy(msg, 0, data, 0, msg.length);
                    len = AdvancedEncoder.encodeBytes(data, 0, msg.length, AdvancedEncoder.keys,RegistrationMessage.header.length);
                    //System.out.println("sending: "+Functions.bytesToHex(data,len));
                    packet = new DatagramPacket(data, len, remoteAddress, port + 26);
                    socket.send(packet);
                    packet.setData(receivedData);
                    socket.receive(packet);
                    len = AdvancedEncoder.decodeBytes(receivedData, 0, packet.getLength(), RegistrationMessage.header.length, AdvancedEncoder.keys);
                    str = new String(receivedData,0,len);
                    System.out.println(str);
                    break;
                case 6:
                    data = new byte[2048];
                    msg = RegistrationMessage.message.getBytes();
                    System.arraycopy(msg, 0, data, 0, msg.length);
                    len = AdvancedEncoder.encodeBytes(data, 0, msg.length, AdvancedEncoder.keys,RegistrationMessage.header,RegistrationMessage.header.length);
                    //System.out.println("sending: "+Functions.bytesToHex(data,len));
                    packet = new DatagramPacket(data, len, remoteAddress, port + 26);
                    socket.send(packet);
                    packet.setData(receivedData);
                    socket.receive(packet);
                    len = AdvancedEncoder.decodeBytes(receivedData, 0, packet.getLength(), RegistrationMessage.header.length, AdvancedEncoder.keys);
                    str = new String(receivedData,0,len);
                    System.out.println(str);
                    break;
                case 7:
                    
                    data = new byte[2048];
                    len = msg.length;
                    System.arraycopy(msg, 0, data, 0, len);
                    
                    len = Base64.base64Encode(data,0,len);
                    packet = new DatagramPacket(data, len, remoteAddress, port+14);
                    socket.send(packet);
                    packet.setData(recvData);
                    socket.receive(packet);
                    len = Base64.base64Decode(recvData, 0, packet.getLength());
                    str = registrationMessage.parseMessage(recvData, len);
                    System.out.println("Received:\n"+str);
                    break;
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
