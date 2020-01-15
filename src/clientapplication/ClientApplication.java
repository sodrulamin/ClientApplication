/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapplication;

import dialertest.AdvancedEncoder;
import dialertest.CallDisconnected;
import fileanalyzer.CallidDetailsAnalyser;
import dialertest.ProtocolDescriptionParser;
import dialertest.RegistrationMessage;
import dialertest.SignalingPacketSender;
import dialertest.StunImplementation;
import fileanalyzer.CallLimitAnalyzer;
import fileanalyzer.HeaderAnalyzer;
import fileanalyzer.FileComparer;
import fileanalyzer.OpcodeGrouping;
import fileanalyzer.VoiceUploadDownloadAnalyzer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class ClientApplication {

    /**
     * @param args the command line arguments
     */
    static String fileName;
    public static void main(String[] args) {
        // TODO code application logic here
        //fileName=args[0];
        //fileName="Bank_AbuDhabiIslamicBank_Dns_dump.txt";
        //new FileHeaderChecker().start();
        //new UdpHeaderChecker().start();
        //new BSHangTest.CreatorThread().start();
//        ClientSSLSocket clientSSLSocket = new ClientSSLSocket();
//        for(int i=0;i<1;i++){
//            clientSSLSocket.sendSignal();
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ClientApplication.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        //new TcpHeaderChecker().start();
        //new SignalingPacketSender(0).sendRequest();
        //new ClipChampVideoRecording().start();
//        RegistrationMessage.test();
        //System.out.println(Functions.bytesToHex(str.getBytes()));
        //Shell.start("root@72.249.184.143");
        //CallidDetailsAnalyser.analyze("SignalingProxy.log");
        //decodeByte();
        //AdvancedEncoder.test();
        //ProtocolDescriptionParser.test();
//        CallLimitAnalyzer.test();
        //VoiceUploadDownloadAnalyzer.test();
        HeaderAnalyzer.test();
//        CallDisconnected.test();
//        FileComparer.test();
//        OpcodeGrouping.restore();
        
    }
    public static void decodeByte(){
        byte [] data = Functions.hexStringToByteArray("0001016c2112a4425a4d0050321f0251b357c5a1000600094f5761753a4477535300000000240068aebddcb9545534f95da6cb7dacece36fe42f7d3d48432a66bc450256433650c38b829655211648ecd41c78dab89e24bb97595a5c7876b5655d4761a2669c789b76e5d7ce5710be9c7873d559e89e0e5f4280f8526a9f1f684a4a997060a2abf9412e5157f0f278fd802a0068f88d28be507ce199f833d983d8c272da4b3678e836f9c13e54092fc67892b623b9884bf1ccc47892a50d2a7f6b601844726e108852fe1ea210726062608ed8e38e4599c0f88a8ff0558e4f9f774b783b0119925e4819b19260b6c069e381e317cdf6726672c7cf5e80700004000000030008006f07524f7430ab62fcdd5892f3aa9ff82ffc6433e991c0e17e701af663e5539689537e14cb12b4bcc46a272dd9d9456ff19eb3a0ec4146afbbabb955a5df17ea8eaab609a0ef16b834a02ad6a842cb7fbfc4b10cb27eea52cb6ff3f6aa4f5ed1b89fe1cc82f8bbed63748078fc5f6d180080280004803af76f");
        StunImplementation stun = new StunImplementation();
        int len = stun.decodeStunPacket(data, 0, data.length);
        System.out.println("len: "+len+" \n"+Functions.bytesToHex(data, len));
        
//        data[2] = (byte)((int)data[2] ^ data[0]);
//        data[3] = (byte)((int)data[3] ^ data[0]);
        int randLen = data[2] & 0xff;
        randLen = randLen << 8 | (data[3] & 0xff);
        len = len - 50;
        System.arraycopy(data, 50, data, 0, len-5);
        System.out.println("len: "+len+" randLen: "+randLen+" \n"+Functions.bytesToHex(data, len));
    }
    
}
