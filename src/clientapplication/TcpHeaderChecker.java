/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class TcpHeaderChecker extends Thread{
    int sleep;
    InetAddress address;
    int port,totalPacket;
    int sentCount,receivedCount;
    byte [] firstHeader = Functions.hexStringToByteArray("040b0000820500000e830817246be4030c0000007c0800000500000090abfb804f77f081e6ea1f61ab5669437da3e2c5ea77b4d12ab3989b171059b60be248acf74b1c6b78ba6c495651b06911a6f608045d634eb44f1ff62ba778412d2c6d7a9a7b70d230daf7793586f48a2d484bcbeae3d75e3add55b30da6397900ce4de9f8376453d93270e5b27d03fc4e1ec003c819c2c4ced392242a34c22b27323aafdf38aafd4b64629c82dcbe8e15468cc6698822baa332496dc36f6c42520d4540c4d9e82062d0e837e17eba888c1698a2aa0db99e17067c189e68c247bddbde6af79797ebe9bc03b5a8c435ffc6e8d249353f56d0284d83f4a478d72cb72c25ac8e5c4adc010518893f46e780110ddf732256790ad10dfcf0b031603032cfb242852768bb5d72d85f09ccba456ef2131d3c8c45cca865ba9d50926aa6b0acc1745373e14541c585ea7fd64ac743fb597e3791ce3ba8032aac45d9f9dc309c15a2979cc0ceb59c64e4dad0110822f53f7fa49bfe9ed962fe42494a8760b822209f06fef4890fb2437c8248bed5becaed2ecd3002ceae5970a186bc65ce3e69a025d973feab8d41a83fe45f9e937b2a402d51d9cbd803f1925f0cb08f98f4c6e095cc94d7a7f7f37d79a6253e3ff0846eaaa50ccf985889908520266b25e34a0d1ec0630ada12fd94e6334ea2581173807a22ed477d3405cfc790f57a28926180d2970c6aa080c48f285f60f61b4b0915709b9e552c60ecfc38ae0cd7f27e066415a7445f16fa7bb67ab8b560a5d048c979e8b64659f1f2771c8e018d719f88a305581bf5a663ab39477343e778129e927e67d3b3a14f61b9b711a2485e314de8f5a438621bf0d1d3d6ed1fbbe44101632dd66cc1811f9b5957ca15adeb4b755ad7ef7ec5110083d6a425ff6e36eae17cb33175b44426b29f1ad4047a2c7ecbbb710aa9392a4339a6f3a888512510eb24aa3e02bedf4bf6265b11c79f9e148d28f90ccac328f410ccdec3e5f63a0d8c450d25f09412bebeaafdd79b7750b56d3156b678b6579338195ea6477dbf5f07abe4bbdcb5080a270d8426eb492f6b7d276f86dcc037645148c69bb5a0bf5143efabe841dff09a94fc4aa6edaa8179764116042d6d1ba7a3b73db3ac4538f95085652417e805d1f3dbb6e412937049a6172a3c674b03a7cddd752658c18695aa3e153ee2c5d0a5485f39834ec72870904274af69aeb7db42a99018de26e0c35583ea69edae1d49295b08a90a8e53faf9d6e94a2ea2510b957bccb49f4d5537106610faef668fea107711f7d1b8a4c3d3a874099ce7fb06e2ac4a4604124966534939d5288d34765ea10931a5f0b7b2aa0d22f5db2049acdc98d8b41811af53a552424840bed071a95328956f0744f8218629863086184c7c0dca6ae5385d4461c306c23f1939");
    byte [] header = Functions.hexStringToByteArray("ffffffffffffffffffffffffffffffff002d0104feb000b40a0000021002060104000100010202800002020200");
    TcpHeaderChecker(){
        sleep=200;
        sentCount=0;
        receivedCount=0;
        totalPacket=100;
    }
    
    @Override
    public void run(){
        try {
            loadConfiguration();
            int i=0;
            //while(true) 
            {
                Socket socket = new Socket(address,port);
                new TCPSenderClass(socket).start();
                new TCPReceiverClass(socket).start();
                i++;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(TcpHeaderChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private class TCPSenderClass extends Thread{
        Socket socket;

        public TCPSenderClass(Socket s) {
            socket = s;
        }
        
        @Override
        public void run(){
            try {
                OutputStream os = socket.getOutputStream();
                for(int i=0;i<totalPacket;i++)
                {
                    os.write(createTcpPacket(sleep));
                    sentCount++;
                    System.out.println("Packet sent len "+sleep+" count ----- "+sentCount);
                    Thread.sleep(sleep);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    private class TCPReceiverClass extends Thread{
        Socket socket;

        public TCPReceiverClass(Socket s) {
            socket = s;
        }
        
        @Override
        public void run(){
            try{
                socket.setSoTimeout(2000);
                InputStream is = socket.getInputStream();
                
                while (true) {
                    int n = readByte(is);
                    if(n>0)
                    {
                        receivedCount++;
                        System.out.println("Packet received len "+n+" count "+receivedCount);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
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
            totalPacket=Integer.parseInt(line);
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" +fileName + "'");
            
        }
        catch(IOException ex) {
            System.out.println("Error reading file '"+ fileName + "'");
        }
    }
    
    private byte [] createTcpPacket(int len){
        byte [] data = Functions.getRandomData(len+2);
        data[0] = (byte)((len) >> 8 & 0xff);
        data[1] = (byte)((len) & 0xff);
        
        data = Functions.concatenateByteArrays(header, data);
        
        return data;
    }
    // ======================================================================== //
    public int readByte(InputStream is){

        int minLen = 2;

        int rl, crl;
        int mlen = minLen;

        byte[] chunkHeader = new byte[minLen];
        crl = rl = 0;
        try {
            while (crl < minLen) {
                rl = is.read(chunkHeader, crl, minLen - crl);
                if (rl < 0) {
                    break;
                }
                crl += rl;
            }
        } catch (IOException ex) {}
        minLen = chunkHeader[mlen - 2] & 0xff;
        minLen = (minLen << 8) | (chunkHeader[mlen - 1] & 0xff);
        byte[] b = new byte[minLen];
        crl = 0;

        while (crl < minLen) {
            try {
                rl = is.read(b, crl, minLen - crl);
                if (rl < 0) // socket close case
                {
                    break;
                }
                crl += rl;
            } catch (IOException ex) {
                
            }
        }
        //System.out.println("length:: " + minLen);
        return crl;
    }
    
}
