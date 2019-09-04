package clientapplication;

import clientapplication.Functions;
import dialertest.RegistrationMessage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class ClientSSLSocket {

    private static String host = "181.41.196.236";

    private static int port = 995;
    public static int count = 10;
    public static int size = 500;
    public static int delay = 450;

    public static int sent = 0;
    public static int receive = 0;
    byte[] receivedData = new byte[2048];

    // Create the and initialize the SSLContext
    public SSLContext createSSLContext() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("keystore.jks"), "changeit".toCharArray());

            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "changeit".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();

            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();

            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(km, tm, null);

            return sslContext;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // Start to startServer the server
    public void sendSignal() {
        SSLContext sslContext = createSSLContext();

        try {
            // Create socket factory
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Create socket
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
            System.out.println("SSL client socket  started");
            clientThread(sslSocket, count, size, delay);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Thread handling the socket to server
    public void clientThread(SSLSocket sslSocket, int count, int size, int delay) {

        System.out.println("client sending thread started");

        try {

            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());

            try {

                sslSocket.startHandshake();
                SSLSession sslSession = sslSocket.getSession();

                //receiverThread(sslSocket, count, size, delay);
                senderThread(sslSocket, count, size, delay);
                // System.out.println("Client has closed ssl connection");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // System.out.println(e);

        }

    }

    public void senderThread(Socket sslSocket, int count, int size, int delay) {

        
        System.out.println("client sending thread started");
        try {

            DataOutputStream outToServer = new DataOutputStream(sslSocket.getOutputStream());
            int i = 0;
            System.out.println("handshake complete now going to send data");
//            String msg = "REG2U=47648\r\n"
//                    + "C=47648\r\n"
//                    + "P=4\r\n"
//                    + "O=S47648\r\n"
//                    + "ANDROID\r\n"
//                    + "V=7.2.4\r\n"
//                    + "S=1.0.6\r\n"
//                    + "\r\n"
//                    + "OP=S47648";

            OutputStream os = sslSocket.getOutputStream();
//            byte[] data = Functions.hexStringToByteArray("545203005e545203574b4e3a5e3e31333a3535393d384234390f0d474236383f3a313438333d383e1412593e37323c373d1311574654353939383d131149574553514c4812105d45402f333037110f59443937312f38100e5456445b3d3837363b0e4a");
//                    byte [] data = new byte[msg.length()+2];
//                    data[0] = (byte)((msg.length() >> 8) & 0xff);
//                    data[1] = (byte)(msg.length() & 0xff);
//                    System.arraycopy(msg.getBytes(), 0, data, 2, msg.length());
            RegistrationMessage registrationMessage = new RegistrationMessage();
            byte[] data = registrationMessage.getRegistrationMessage();
            //data = "Hi_abdullah/r/n".getBytes();
            //DataOutputStream osw = new DataOutputStream(os);
            //osw.writeBytes("Hi_Abdullah/n");
            
            os.write(data);
            sent++;
            //new dummyReceiver(sslSocket.getInputStream()).start();
            new receiver(registrationMessage,sslSocket.getInputStream()).start();

//            os.write(data);
//            len = readByte(is, receivedData);
//            msg = new String(receivedData, 0, len);
//            System.out.println("Got reply:\n" + msg);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    private class dummyReceiver extends Thread{
        BufferedReader br;
        public dummyReceiver(InputStream is){
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
        }
        @Override
        public void run(){
            try {
                br.readLine();
                receive++;
                String message = "\r\nsent: "+sent + " receive: "+receive;
                Functions.debug("Shaon.txt", message);
            } catch (IOException ex) {
                Logger.getLogger(ClientSSLSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private class receiver extends Thread{
        RegistrationMessage registrationMessage;
        InputStream is;

        public receiver(RegistrationMessage rg,InputStream inputStream) {
            is = inputStream;
            registrationMessage = rg;
        }
        @Override
        public void run(){
            try{
                //while(){
                    String msg = registrationMessage.receiveMessage(is, receivedData);
                    if(msg != null)receive++;
                    String message = "\r\nsent: "+sent + " receive: "+receive;
                    Functions.debug("Shaon.txt", message);
                //}
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }

    public void updateMsg() {
        System.out.println("sent: " + sent + " receive: " + receive);
    }

    public int readByte(InputStream is, byte[] data) {

        int minLen = 106;

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
        System.arraycopy(chunkHeader, 0, data, 0, mlen);
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
        //data=new byte[crl];
        System.arraycopy(b, 0, data, mlen, b.length);
        return minLen;
    }
	// }
}
