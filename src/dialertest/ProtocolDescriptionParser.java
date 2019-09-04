/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialertest;

import java.util.ArrayList;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class ProtocolDescriptionParser {
    public ArrayList<ProtocolDescription> protocolSequence;
    
    public ArrayList<ProtocolDescription> parseProtocolDescription(String description){
        if(description == null || description.length() == 0)
            return null;
        protocolSequence = new ArrayList<ProtocolDescription>();
        
        ///100-1.1.1.1:3453,3.3.3.3:989;101-3.2.4.5:443;102-56.56.2.4:763
        
        String [] protocols = description.split(";");
        for(String protocol: protocols){
            String [] arr = protocol.split("-");
            if(arr.length > 1){
                ProtocolDescription protocolDescription = new ProtocolDescription();
                protocolDescription.protocol = Integer.parseInt(arr[0].trim());
                protocolDescription.ipPortSequence = new ArrayList<IpportDTo>();
                String [] ipPorts = arr[1].split(",");
                for(String str: ipPorts){
                    String [] ipPortArray = str.split(":");
                    if(ipPortArray.length > 1){
                        IpportDTo dto = new IpportDTo();
                        dto.ip = ipPortArray[0].trim();
                        dto.port = Integer.parseInt(ipPortArray[1].trim());
                        protocolDescription.ipPortSequence.add(dto);
                    }
                }
                protocolSequence.add(protocolDescription);
            }
        }
        
        return protocolSequence;
    }
    
    public class ProtocolDescription{
        int protocol;
        public ArrayList<IpportDTo> ipPortSequence;
    }
    public class IpportDTo{
        public String ip;
        public int port;
    }
    
    
    public static void test(){
        ProtocolDescriptionParser parser = new ProtocolDescriptionParser();
        String description = "100-1.1.1.1:3453,3.3.3.3:989;101-3.2.4.5:443;102-56.56.2.4:763";
        ArrayList<ProtocolDescription> sequence = parser.parseProtocolDescription(description);
        for(ProtocolDescription descriptor: sequence){
            System.out.println("Protocol: "+descriptor.protocol);
            for(IpportDTo dto: descriptor.ipPortSequence){
                System.out.println("ip: "+dto.ip+" port: "+dto.port);
            }
        }
    }
}

