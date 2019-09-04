/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialertest;

import clientapplication.Functions;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class CallDisconnected {

    public static final int CALL_CLEAN_UP_PACKET_TYPE = 0x0204;
    public static final int CALL_ID = 0x000D;
    static byte[] callDetails = new byte[2048];

    public static void test() {
        ConcurrentHashMap<String, String> cidMap = new ConcurrentHashMap<>();
        cidMap.put("aaaa1", "aaaa1");
        cidMap.put("aaaa2", "aaaa2");
        cidMap.put("aaaa3", "aaaa3");
        cidMap.put("aaaa4", "aaaa4");
        int len = sendCallDisconnectedCleanupMessage(cidMap);
        processCallCleanupPacket(callDetails, len);
    }

    public static int sendCallDisconnectedCleanupMessage(ConcurrentHashMap<String, String> callIDMap) {

        int index = 0, cidCount = 0;
        callDetails[index++] = (byte) ((CALL_CLEAN_UP_PACKET_TYPE >> 8) & 0xff);
        callDetails[index++] = (byte) (CALL_CLEAN_UP_PACKET_TYPE & 0xff);
        index += 2;

        Set<String> callIDSet = callIDMap.keySet();
        for (String callId : callIDSet) {
            cidCount++;
            int attrCount = 0, attrCountIndex = index;
            index += 2;
            index = Functions.appendAttribute(callDetails, index, CALL_ID, callId);
            attrCount++;

            callDetails[attrCountIndex] = (byte) ((attrCount >> 8) & 0xff);
            callDetails[attrCountIndex + 1] = (byte) (attrCount & 0xff);

            if (index > 1024) {
                callDetails[2] = (byte) ((cidCount >> 8) & 0xff);
                callDetails[3] = (byte) (cidCount & 0xff);

                index = Functions.encodeBytes(callDetails, index);
                System.out.println(Functions.bytesToHex(callDetails, index));

                callDetails[0] = (byte) ((CALL_CLEAN_UP_PACKET_TYPE >> 8) & 0xff);
                callDetails[1] = (byte) (CALL_CLEAN_UP_PACKET_TYPE & 0xff);
                index = 4;
                cidCount = 0;
            }
        }

        if (cidCount == 0) {
            return 0;
        }

        callDetails[2] = (byte) ((cidCount >> 8) & 0xff);
        callDetails[3] = (byte) (cidCount & 0xff);
        System.out.println(Functions.bytesToHex(callDetails, index));
        index = Functions.encodeBytes(callDetails, index);

        System.out.println(Functions.bytesToHex(callDetails, index));

        return index;
    }

    public static void processCallCleanupPacket(byte[] message, int packetLength) {
        System.out.println(Functions.bytesToHex(message, packetLength));
        packetLength = Functions.decodeBytes(message, packetLength);
        System.out.println(Functions.bytesToHex(message, packetLength));
        
        int callIDCount = 4;

        int index = 4;
        for (int i = 0; i < callIDCount; i++) {
            int attributeCount = ((message[index++] & 0x00FF) << 8) | (message[index++] & 0x00FF);
            int attributeType = ((message[index++] & 0x00FF) << 8) | (message[index++] & 0x00FF);
            int attributeLength = ((message[index++] & 0x00FF) << 8) | (message[index++] & 0x00FF);
            System.out.println("count: "+attributeCount+" type: "+attributeType+" len: "+attributeLength+" index: "+index);

            if (attributeLength < 0 || attributeLength > message.length
                    || attributeType != CALL_ID) {

                index += attributeLength;
                
                index = skipAttributes(message, index, attributeCount - 1);
                continue;
            }
            String str = new String(message, index, attributeLength);
            index += attributeLength;

            index = skipAttributes(message, index, attributeCount - 1);

            //index += attributeLength;

        }

    }

    private static int skipAttributes(byte[] message, int index, int attrCount) {
        int attributeType, attributeLength;
        for (int i = 0; i < attrCount; i++) {
            ////attr type
            attributeType = message[index++] & 0x00ff;
            attributeType = (attributeType << 8) | (message[index++] & 0x00ff);

            ////attr len
            attributeLength = message[index++] & 0x00ff;
            attributeLength = (attributeLength << 8) | (message[index++] & 0x00ff);
            index += attributeLength;
        }
        return index;
    }
}
