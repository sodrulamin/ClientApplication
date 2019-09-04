/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialertest;

import clientapplication.Functions;
import java.util.Random;
import javax.print.DocFlavor;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class AdvancedEncoder {
    public static byte [] keys = Functions.hexStringToByteArray("0607080112433084");
    static Random random = new Random();
   public static int encodeBytes(byte [] data,int offset,int dataLen,byte [] keys,byte [] header,int headerLength){
        if(data == null || keys == null || header == null || headerLength > header.length 
                || offset < 0 || dataLen <= 0 || headerLength <= 0 
                || (offset + dataLen + headerLength) > data.length) 
            return -1;
        
        for(int i=dataLen - 1;i >= 0;i--){
            data[i + headerLength + offset] = (byte) ( (data[i + offset] + keys[i % keys.length]) & 0x00FF);
            data[i + headerLength + offset] = (byte) (  data[i + headerLength + offset] ^ header[i % headerLength]);
        }
        for(int i = 0;i<headerLength;i++){
            data[i + offset] = header[i];
        }
        return offset + headerLength + dataLen;
    }
    public static int encodeBytes(byte [] data,int offset,int dataLen,byte [] keys,int headerLength){
        byte [] header = Functions.getRandomData(headerLength);
        return encodeBytes(data, offset, dataLen, keys, header, headerLength);
    }
    public static int encodeBytes(byte [] data,int offset,int dataLen,byte [] keys){
        int headerLength = random.nextInt(250) + 5;
        
        int totalLen =  encodeBytes(data, offset, dataLen, keys, headerLength);
        if(totalLen <= 0)
            return -1;
        
        int firstIndex = ((random.nextInt(headerLength - 2) + 2) & 0xff);
        int secondIndex = firstIndex;
        int tryCount = 0;
        while(firstIndex == secondIndex && tryCount < 100){
            secondIndex = ((random.nextInt(headerLength - 2) + 2) & 0xff);
            tryCount++;
        }
        if(firstIndex == secondIndex)
        	return -1;
        //System.out.println("headerLength: "+headerLength+" firstIndex: "+firstIndex+" secondIndex: "+secondIndex);
        ////shifting those affected bytes
        data[totalLen + 0] = data[offset];
        data[totalLen + 1] = data[offset + 1];
        data[totalLen + 2] = data[offset + firstIndex];
        data[totalLen + 3] = data[offset + secondIndex];
        
        ////adding encoding info
        data[offset + 0] = (byte)firstIndex;
        data[offset + 1] = (byte)secondIndex;
        data[offset + firstIndex] = (byte)((headerLength >> 8) & 0xff);
        data[offset + secondIndex] = (byte)(headerLength & 0xff);
        
        ////ssh len encoding techonology
        data[offset + 0] = (byte)(data[offset + 2] ^ data[offset + 0]);
        data[offset + 1] = (byte)(data[offset + 3] ^ data[offset + 1]);
        
        return totalLen + 4;
    }
    public static int decodeBytes(byte [] data,int offset,int totalLen,byte [] keys){
        if(data == null || keys == null || offset < 0 || totalLen <= 0 
                || offset >= totalLen || totalLen > data.length)
            return -1;
        try{
	        /////ssh len decoding
	        data[offset + 0] = (byte)(data[offset + 2] ^ data[offset + 0]);
	        data[offset + 1] = (byte)(data[offset + 3] ^ data[offset + 1]);
	        
	        /////collecting encoding info
	        int firstIndex = (data[offset + 0] & 0xff);
	        int secondIndex = (data[offset + 1] & 0xff);
	        int headerLength = (int)(data[offset + firstIndex] & 0xff);
	        headerLength = ((headerLength << 8) | (int)(data[offset + secondIndex] & 0xff));
	        
	        //System.out.println("headerLength: "+headerLength+" firstIndex: "+firstIndex+" secondIndex: "+secondIndex);
	        
	        ////shifting back those affected bytes
	        data[offset + 0] = data[totalLen - 4];
	        data[offset + 1] = data[totalLen - 3] ;
	        data[offset + firstIndex] = data[totalLen - 2];
	        data[offset + secondIndex] = data[totalLen - 1];
	        
	        return decodeBytes(data, offset, totalLen - 4, headerLength, keys);
        }catch(Exception e){}
        return -1;
    }
    
    public static int decodeBytes(byte [] data,int offset,int totalLen,int headerLen,byte [] keys){
        if(data == null || keys == null || offset < 0 || totalLen <= 0 
                || headerLen <= 0 || offset >= totalLen || headerLen >= totalLen 
                || (offset + totalLen) > data.length)
            return -1;
        try{
	        int dataLen = totalLen - headerLen;
	        byte [] header = new byte[headerLen];
	        System.arraycopy(data, offset, header, 0, headerLen);
	        for(int i=0;i < dataLen;i++){
	            data[offset + i] = (byte)(data[offset + headerLen + i] ^ header[i % headerLen]);
	            data[offset + i] = (byte)(data[offset + i] - keys[i % keys.length]);
	        }
	        
        }catch(Exception e){}
        return -1;
    }
    
    public static void test(){
        byte [] data = new byte[2048];
        System.out.println("Keys: "+Functions.bytesToHex(AdvancedEncoder.keys));
        data = Functions.hexStringToByteArray("e03f961f266759cb93bd5fe07ba9233cae30c0d39e7da240559a3a10dcbd9ab994ad8401ab4c1b262a3df43ddbb47fd874a0dbb6a55bc10584c0a4fba0f8159fb2d99f9eb12d5aef28c368bb6749d5343b9b2932668897f2fed37abfcb1e8d33d61a72b1b300e7112747578f4759867f46af988cfff1009a54c89ff6f8075001bb1c71c291439953553ef597a7466c138bb44506b48cd92c41e73e77acfd1eee672f4e84930dfcea813022816ada7b1ec02ff76eb995be33e11c21e37e76ae6d8033425628e4e499e02aa394949ae1c9e08c4b26a1c8c9d8d7ad387e38893385243fb48d05ac69062efdaa7cab843feb8f6aef86e80becc0b281" );
        //int len = Functions.getRandomData(data, 0, 20);
        int len = data.length;
        System.out.println("Base Data: "+Functions.bytesToHex(data, len)+"\n\nNow starting encoding......");
        
        byte [] header = Functions.getRandomData(19);
        System.out.println("Header: "+Functions.bytesToHex(header));
        
//        len = AdvancedEncoder.encodeBytes(data, 0, len, AdvancedEncoder.keys,header,header.length);
//        System.out.println("End of Encoding .....\nEncoded len: "+len+" Data: "+Functions.bytesToHex(data, len)+"\n\nNow starting decoding.......");
        
        len = AdvancedEncoder.decodeBytes(data, 0, len, AdvancedEncoder.keys);
        System.out.println("End of Decoding .....\nDecoded Data: "+Functions.bytesToHex(data, len));
        System.out.println(new String(data,0,len));
    }
}
