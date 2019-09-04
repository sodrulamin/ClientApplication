/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialertest;

import clientapplication.Functions;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class RegistrationMessage {

    public static String message = "REG2U=47648\r\n"
            + "C=47648\r\n"
            + "P=4\r\n"
            + "O=24950\r\n"
            + "ANDROID\r\n"
            + "V=3.9.1\r\n"
            + "S=1.0.7\r\n"
            + "NET=5\r\n"
            + "EXP=0\r\n"
            //+ "CS=1315808362\r\n"
            + "SOCIAL\r\n\r\n"
            + "OP=S47648\r\n";

    public static byte[] header = clientapplication.Functions.hexStringToByteArray("12");

    public byte[] getRegistrationMessage() {
        byte[] data = message.getBytes();
        for (int i = 0; i < data.length - 1; i++) {
            data[i] = (byte) (((int) (data[i] & 0xff)) ^ ((int) (header[i % header.length] & 0xff)));
        }
        data = clientapplication.Functions.concatenateByteArrays(header, data);
        byte[] returnData = new byte[data.length + 2 + header.length];
        System.arraycopy(header, 0, returnData, 0, header.length);
        returnData[header.length] = (byte) ((data.length >> 8) & 0xff);
        returnData[header.length + 1] = (byte) (data.length & 0xff);
        System.arraycopy(data, 0, returnData, header.length + 2, data.length);
        return returnData;
    }
    public String parseMessage(byte[] packetData, int packetLength) {
        if(packetLength < header.length) return null;
        int xorStartingIndex = header.length;
        byte[] returnData = new byte[packetLength - header.length];
        byte [] header1 = new byte[this.header.length];
        System.arraycopy(packetData, 0, header1, 0, header1.length);
        for (int i = 0; i < returnData.length; i++) {
            returnData[i] = (byte) (((int) (packetData[i + xorStartingIndex] & 0xff)) ^ ((int) (header1[i % header1.length] & 0xff)));
        }
        //System.out.println(Functions.bytesToHex(returnData));
        return new String(returnData);
    }
    public byte [] getUdpRegistrationMessage(){
        byte[] data = message.getBytes();
        for (int i = 0; i < data.length - 1; i++) {
            data[i] = (byte) (((int) (data[i] & 0xff)) ^ ((int) (header[i % header.length] & 0xff)));
        }
        data = clientapplication.Functions.concatenateByteArrays(header, data);
        return data;
    }
    public String receiveMessage(InputStream is, byte[] receiveBuffer) {
        int minLen = header.length+2;

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
        } catch (IOException ex) {

        }
        //System.arraycopy(chunkHeader, 0, data, 0, mlen);
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
        System.arraycopy(b, 0, receiveBuffer, 0, b.length);
        //return minLen;
        String msg = parseMessage(receiveBuffer, b.length);
        return msg;
    }
    
    public static void test(){
        RegistrationMessage  registrationMessage = new RegistrationMessage();
        byte [] data = Functions.hexStringToByteArray("1042555722452d282820212921232025272121261d1a402d2229212621231d1a5f2d27232626231d1a5940585f5e551d1a462d233e283e281d1a4059542d56442b202b595f432b267421282572762828742526237674297373292728752524242626277623222574732676722525202523232923757529287621272525222872247122757229711d1a595d55592d757675727474287422257423252675247327292172757629242422752527232273747674712828231d1a5c2d2b2b2b22233e2823222928222b29203e2425262921211d1ad5f3d9e45bfa6cc351e220ae0ce106986d61ff34a11e19fd3650e9b7818fc33a1e0fc02c44557ac8");
        String str = registrationMessage.parseMessage(data, data.length);
        
        System.out.println(str);
    }
}
