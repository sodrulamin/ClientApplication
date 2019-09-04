package clientapplication;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Sodrul Amin Shaon on 9/18/2017.
 */

public class ClipChampVideoRecording extends Thread {
    int count1,count2;
    long timeStamp;
    byte [] sentTimeStamp,timeStamp2;
    int sentCount,receivedCount;
    Socket socket;
    public ClipChampVideoRecording(InetAddress address, int port){
        try {
            socket=new Socket(address,port);
        } catch (IOException ex) {
            Logger.getLogger(ClipChampVideoRecording.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public ClipChampVideoRecording(){
        try {
            InetAddress address = InetAddress.getByName("181.41.196.244");
            int port = 1940;
            socket=new Socket(address,port);
        } catch (IOException ex) {
            Logger.getLogger(ClipChampVideoRecording.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        sentCount = 0;
        receivedCount = 0;
        new Thread(new Sender(socket)).start();
        count1=0;
        count2=128;
    }

    private class Sender implements Runnable{
        Socket socket;
        Sender(Socket s){
            socket=s;
        }
        @Override
        public void run() {
            try {
                socket.setTcpNoDelay(true);
                OutputStream os=socket.getOutputStream();
                long startTime=System.currentTimeMillis();
                System.out.println("Socket connection completed");
                RtmpHandshake(socket);
                os.write(Functions.hexStringToByteArray("4300645f00001a110002000c63726561746553747265616d00400000000000000005"));
                os.write(Functions.hexStringToByteArray("4200000000000a040003000000000000ea60"));
                System.out.println("First two packets sent");
                sentCount+=2;
                int n=readByte(socket.getInputStream(),41);

                if(n<=0){
                    //Log.i("Reading error","socket read exception");
                    return;
                }
                receivedCount++;
                os.write(Functions.hexStringToByteArray("080064620000311101000000000200077075626c69736800000000000000000005020010743430304a39316164536e7465536b490200067265636f7264c20003000000010000ea60"));
                sentCount++;
                System.out.println("Packet sent count ------- "+sentCount);
                //os.write(Functions.hexStringToByteArray("0700653500002d090100000017000000000142001fffe100196742801f96540501ed80a840000003004000000a2768489a8001000468ce3520"));
                timeStamp=System.currentTimeMillis();
                //activity.sentCount++;
                //os.write(getInitialVideoPacket());
                //activity.sentCount++;
                os.flush();
                new Thread(new Receiver(socket)).start();
                while (true) {
                    Thread.sleep(40);
                    //if(System.currentTimeMillis()>(startTime+(activity.packetPerSocket*200)))break;
                    //if(!activity.running)continue;
                    os.write(createVideoPacket(42));
                    sentCount++;
                    System.out.println("Packet sent count ------- "+sentCount);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void RtmpHandshake(Socket socket){
        try {
            OutputStream os=socket.getOutputStream();
            InputStream is=socket.getInputStream();
            os.write(0x03);                  ////////// c0 sent
            Thread.sleep(200);
            os.write(c1());                  ////////// c1 sent
            is.read();                       ////////// s0 read
            byte [] data=readByte(is,1536,true); ////// s1 read
            System.arraycopy(sentTimeStamp,0,data,4,4);
            os.write(data);                  ////////// c2 sent
            readByte(is,1536);               ////////// s2 read
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private class Receiver implements Runnable{
        Socket socket;
        Receiver(Socket s){
            socket=s;
        }
        @Override
        public void run() {
            try{
                InputStream is=socket.getInputStream();
                //socket.getOutputStream().write(createVideoPacket(200));
                while (true){
                    int n=readByte(is);
                    if(n>1){
                        receivedCount++;
                        System.out.println("Packet received count ------------- "+receivedCount);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    byte [] getInitialVideoPacket() {
        return Functions.concatenateByteArrays(Functions.hexStringToByteArray("470000000010000917010000000000000209100000000f060001c0010709080080000003008000000fde65888040056c"),Functions.getRandomData(4056));
    }
    private byte [] c1(){
        byte [] data=new byte[4];
        sentTimeStamp=Functions.getRandomData(4);
        data=Functions.concatenateByteArrays(sentTimeStamp,data);
        data=Functions.concatenateByteArrays(data,Functions.getRandomData(1528));
        return data;
    }
    public byte[] createVideoPacket(int len){
        byte[] data,header;
        header=Functions.hexStringToByteArray("07000000");
        long currentTime=System.currentTimeMillis();
        header[header.length-1]= (byte) (currentTime-timeStamp);
        timeStamp=currentTime;
        header=Functions.concatenateByteArrays(header,Functions.hexStringToByteArray("000000"));
        int n=len+3;
        header[header.length-3]=(byte)(n>>16 & 0xff);
        header[header.length-2]=(byte)(n>>8 & 0xff);
        header[header.length-1]=(byte)(n & 0xff);
        header=Functions.concatenateByteArrays(header,Functions.hexStringToByteArray("090100000017"));
        data=Functions.getRandomData(len+2);
        data[0]=(byte)(len>>8 & 0xff);
        data[1]=(byte)(len & 0xff);
        data=Functions.concatenateByteArrays(header,data);
        return data;
    }
    // ======================================================================== //
    public int readByte(InputStream is){

        int minLen = 2;

        int rl, crl;
        int mlen = minLen;

        byte[] chunkHeader = new byte[minLen];
        crl = rl = 0;
        while (crl < minLen) {
            try {
                rl = is.read(chunkHeader, crl, minLen - crl);
                if (rl < 0) {

                    break;

                }
                crl += rl;
            } catch (IOException ex) {

            }
        }
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
        return minLen;
    }
    // ======================================================================== //
    public int readByte(InputStream is,int len){
        int rl, crl,minLen;
        minLen=len;
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
        return minLen;
    }
    // ======================================================================== //
    public byte[] readByte(InputStream is,int len,boolean a){
        int rl, crl,minLen;
        minLen=len;
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
        byte[] returnByte=new byte[crl];
        System.arraycopy(b,0,returnByte,0,crl);
        return returnByte;
    }


}
