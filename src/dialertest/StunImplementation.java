/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialertest;

import clientapplication.Functions;
import java.io.InputStream;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class StunImplementation {
    private static int typeUserName = 6,typePriority = 0x24,typeIceControlling = 0x802a,typeMsImplementationVersion = 0x8070,typeMessageIntegrity = 0x08,typeFingerPrint = 0x8028;
    private byte [] userName;// = Functions.getRandomWord(9).getBytes();
    private static byte [] magicCookie = Functions.hexStringToByteArray("2112a442");
    byte [] temparray,receivedData;
//    static{
//        userName[4] = 0x3a;
//    }

    public StunImplementation(){
        temparray = new byte[2048];
        receivedData = new byte[2048];
        userName  = Functions.getRandomWord(9).getBytes();
        userName[4] = 0x3a;
    }
    
    
    public static void test(){
        byte [] data = new byte[2048];
        int len = Functions.getRandomData(data, 0, 201);
        String dataString = Functions.bytesToHex(data, 0, len);
//        System.out.println("Base len: "+len+" Data: "+dataString);
        StunImplementation stun = new StunImplementation();
//        len = stun.createStunFromData(data, 0, len);
        byte [] dialerData = Functions.hexStringToByteArray("000101342112a4425a4d0018c025ec6bb26d8a5e000600094f5761753a4477535300000000240054042760118eedac40aeccae944473b42d76b72005ceac7e5fc6e054e56692cc619fde8e91366120f39d0bf8303c30ec86d9cd5e3478fac8835b638609067470eb1ef6c026d8a592866012f33f1e4fe58611b078f8802a0054694e7170c91cf8727892a9c3efee8ad01638f87274532c1846a95fdc72bd683e521005e388c26086cc789849825d361cf8e0569fd316cebb8d72f82bfc6c939fface39707893be42fc2f08d1c63078701fc778c980700004000000030008005ff105db7260c85efa426e982d62f3f833f576d9832f3b177f78daa5c71b30c7aeba9c5224646f4c4f4bb2e47c78eafc70a69ecc706dcc7857f6ad871e4e0fa14460931e7b015f058e150613d9d47976d48d1df108a6a754bea3a3361a5263090080280004e93a2d67");
        System.arraycopy(dialerData, 0, data, 0, dialerData.length);
        len = dialerData.length;
        
        dataString = Functions.bytesToHex(data, 0, len);
        System.out.println("Stun encoded len: "+len+" data: "+dataString);
        len = stun.decodeStunPacket(data, 0, len);
        dataString = Functions.bytesToHex(data, 0, len);
        System.out.println("After decoding len: "+len+" data: "+dataString);
    }
    
    
    public int createStunFromData(byte [] data,int offset,int len){
    	if(len < 28) len = 28;
        System.arraycopy(data,offset,temparray,0,len);
        int index = 0,dataAddedIndex = 0,a,trxData;
        data[index++] = 0x00;
        data[index++] = 0x01;
        index+=2;

        /*data[index++] = 0x21;
        data[index++] = 0x12;
        data[index++] = (byte) 0xa4;
        data[index++] = 0x42;*/
        for(int i=0;i<magicCookie.length;i++){
            data[index++] = magicCookie[i];
        }

        trxData = 12;
        System.arraycopy(temparray,dataAddedIndex,data,index,trxData);
        index+=trxData;
        dataAddedIndex+=trxData;
        index = Functions.appendAttribute(data,index,typeUserName,userName.length,userName);
        if(userName.length%4 != 0) {
            for (int i = 0; i < (4 - (userName.length % 4)); i++)
                data[index++] = 0x00;
        }
        a = (len - dataAddedIndex - 4)/3;
        a-=a%4;

        data[index++] = (byte)(typePriority>>8 & 0xff);
        data[index++] = (byte)(typePriority & 0xff);
        data[index++] = (byte)(a>>8 & 0xff);
        data[index++] = (byte)(a & 0xff);
        for(int i=0;i<a;i++)
            data[index++] = temparray[dataAddedIndex++];

        data[index++] = (byte)(typeIceControlling>>8 & 0xff);
        data[index++] = (byte)(typeIceControlling & 0xff);
        data[index++] = (byte)(a>>8 & 0xff);
        data[index++] = (byte)(a & 0xff);
        for(int i=0;i<a;i++)
            data[index++] = temparray[dataAddedIndex++];

        data[index++] = (byte)(typeMsImplementationVersion>>8 & 0xff);
        data[index++] = (byte)(typeMsImplementationVersion & 0xff);
        data[index++] = (byte)(4>>8 & 0xff);
        data[index++] = (byte)(4 & 0xff);
        data[index++] = 0x00;
        data[index++] = 0x00;
        data[index++] = 0x00;
        data[index++] = 0x03;

        a = len - dataAddedIndex - 4;
        data[index++] = (byte)(typeMessageIntegrity>>8 & 0xff);
        data[index++] = (byte)(typeMessageIntegrity & 0xff);
        data[index++] = (byte)(a>>8 & 0xff);
        data[index++] = (byte)(a & 0xff);
        for(int i=0;i<a;i++)
            data[index++] = temparray[dataAddedIndex++];
        if(a%4!=0){
        	for(int i=0;i<4-a%4;i++){
        		data[index++] = 0x00;
        	}
        }

        //a = len - dataAddedIndex;
        a = 4;
        data[index++] = (byte)(typeFingerPrint>>8 & 0xff);
        data[index++] = (byte)(typeFingerPrint & 0xff);
        data[index++] = (byte)(a>>8 & 0xff);
        data[index++] = (byte)(a & 0xff);
        for(int i=0;i<a;i++)
            data[index++] = temparray[dataAddedIndex++];
        if(a%4!=0){
        	for(int i=0;i<4-a%4;i++){
        		data[index++] = 0x00;
        	}
        }

        index = index-20;
        data[2] = (byte)(index>>8 & 0xff);
        data[3] = (byte)(index & 0xff);
        return index+20;
    }
    public int createDummyStun(byte [] data){
        int index = 0,a;
        data[index++] = 0x00;
        data[index++] = 0x01;
        index+=2;

        /*data[index++] = 0x21;
        data[index++] = 0x12;
        data[index++] = (byte) 0xa4;
        data[index++] = 0x42;*/
        for(int i=0;i<magicCookie.length;i++){
            data[index++] = magicCookie[i];
        }

        index = Functions.getRandomData(data,index,12);
        index = Functions.appendAttribute(data,index,typeUserName,userName.length,userName);
        if(userName.length%4 != 0) {
            for (int i = 0; i < (4 - (userName.length % 4)); i++)
                data[index++] = 0x00;
        }

        a = 4;
        data[index++] = (byte)(typePriority>>8 & 0xff);
        data[index++] = (byte)(typePriority & 0xff);
        data[index++] = (byte)(a>>8 & 0xff);
        data[index++] = (byte)(a & 0xff);
        index = Functions.getRandomData(data,index,a);

        a = 8;
        data[index++] = (byte)(typeIceControlling>>8 & 0xff);
        data[index++] = (byte)(typeIceControlling & 0xff);
        data[index++] = (byte)(a>>8 & 0xff);
        data[index++] = (byte)(a & 0xff);
        index = Functions.getRandomData(data,index,a);

        data[index++] = (byte)(typeMsImplementationVersion>>8 & 0xff);
        data[index++] = (byte)(typeMsImplementationVersion & 0xff);
        data[index++] = (byte)(4>>8 & 0xff);
        data[index++] = (byte)(4 & 0xff);
        data[index++] = 0x00;
        data[index++] = 0x00;
        data[index++] = 0x00;
        data[index++] = 0x03;


        a = 20;
        data[index++] = (byte)(typeMessageIntegrity>>8 & 0xff);
        data[index++] = (byte)(typeMessageIntegrity & 0xff);
        data[index++] = (byte)(a>>8 & 0xff);
        data[index++] = (byte)(a & 0xff);
        index = Functions.getRandomData(data,index,a);

        //a = len - dataAddedIndex;
        a = 4;
        data[index++] = (byte)(typeFingerPrint>>8 & 0xff);
        data[index++] = (byte)(typeFingerPrint & 0xff);
        data[index++] = (byte)(a>>8 & 0xff);
        data[index++] = (byte)(a & 0xff);
        index = Functions.getRandomData(data,index,a);

        a = index-20;
        data[2] = (byte)(a>>8 & 0xff);
        data[3] = (byte)(a & 0xff);

        return index;
    }
    public static boolean isStun(byte [] data,int offset,int len){
    	if(len <= 20)return false;
        for(int i=0;i<4;i++){
            if(data[i+offset+4] != magicCookie[i]){
                return false;
            }
        }
        return true;
    }
    public int decodeStunPacket(byte [] data,int offset,int len){
        if(len <= 20)return len;
        for(int i=0;i<4;i++){
            if(data[i+offset+4] != magicCookie[i]){
                return len;
            }
        }
        int packetLen,packetType;
        int index = offset+8,receivedDataLen = 0;
        for(int i=0;i<12;i++){
            receivedData[receivedDataLen++] = data[index++];
        }
        index+=16;
        /*packetLen = Functions.byteArrayToint(data,index);
        index+=2;
        if(packetLen <= 4 || packetLen > 500)return len;
        for(int i=0;i<packetLen;i++){
            receivedData[receivedDataLen++] = data[index++];
        }*/

        while(index < len) {
        	packetType = Functions.byteArrayToint(data, index);
            index += 2;
            packetLen = Functions.byteArrayToint(data, index);
            index += 2;
            if( (packetType != typeUserName && packetType != typeMsImplementationVersion) && packetLen <= 500) {
                for (int i = 0; i < packetLen; i++) {
                    receivedData[receivedDataLen++] = data[index++];
                }
            }else
                index+=packetLen;
            if(packetLen % 4 != 0){
            	int a = 4 - (packetLen % 4);
            	index += a;
            }
        }
        System.arraycopy(receivedData,0,data,0,receivedDataLen);
        return receivedDataLen;
    }
//    public static int receiveStreamData(InputStream is,byte [] data,int offset) throws Exception{
//        int totalRead = offset;
//        boolean isStun = true;
//        totalRead = Functions.readByte(is, data, totalRead, 8);
//        for(int i=0;i<magicCookie.length;i++){
//            if(data[totalRead - magicCookie.length + i] != magicCookie[i]){
//                isStun = false;
//                break;
//            }
//        }
//        if(isStun){
//            totalRead = Functions.readByte(is, data, totalRead, 12);
//            while(true){
//                totalRead = Functions.readByte(is, data, totalRead, 4);
//                int type = Functions.byteArrayToint(data, totalRead - 4);
//                int len = Functions.byteArrayToint(data, totalRead - 2);
//                if(len % 4 != 0){
//                    int a = len % 4;
//                    len += (4 - a);
//                }
//                totalRead = Functions.readByte(is,data,totalRead,len);
//                if(type == typeFingerPrint)break;
//            }
//        }else{
//            int remainingHeaderLen = Configurations.upStreamHeaderLen + 2 - (totalRead - offset);
//            if(remainingHeaderLen > 0){
//                totalRead = Functions.readByte(is, data, totalRead, remainingHeaderLen);
//            }
//            int dataLen = Functions.byteArrayToint(data, Configurations.upStreamHeaderLen);
//            if(remainingHeaderLen < 0){
//                dataLen += remainingHeaderLen;
//            }
//            totalRead = Functions.readByte(is, data, totalRead, dataLen);
//        }
//        
//        return totalRead;
//    }
}

