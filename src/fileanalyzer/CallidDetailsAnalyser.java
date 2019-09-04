/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileanalyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class CallidDetailsAnalyser {
    static ArrayList<String> lines;
    static ArrayList<CallidDetailsDTO> cidDetailsList;
    public static void analyze(String fileName){
        try{
            CallidDetailsAnalyser analyser = new CallidDetailsAnalyser();
            analyser.readHeaders(fileName);
            analyser.decriptLines();
            analyser.showResult();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void showResult(){
        HashMap<String,DetailsResult> resultMap = new HashMap<>();
        for(CallidDetailsDTO cidDetail: cidDetailsList){
            if(cidDetail.status == 3){
                DetailsResult result = resultMap.get(cidDetail.mediaIP);
                if(result == null)
                    result = new DetailsResult();
                result.mediaIp = cidDetail.mediaIP;
                if(cidDetail.clientAudioSent > 0 && cidDetail.mediaAudioReceived > 0){
                    result.totalClientSent += cidDetail.clientAudioSent;
                    result.totalClientReceived += cidDetail.clientAudioReceived;
                    result.totalMediaSent += cidDetail.mediaAudioSent;
                    result.totalMediaReceived += cidDetail.mediaAudioReceived;
                    result.uploadSuccessRate = ((double)result.totalMediaReceived / result.totalClientSent)*100.00;
                    result.downloadSuccesRate = ((double)result.totalClientReceived / result.totalMediaSent)*100.00;
                }
                resultMap.put(cidDetail.mediaIP, result);
            }
        }
        System.out.println("Total media ip count: "+resultMap.size());
        Set<String> ipSet = resultMap.keySet();
        for(String str: ipSet){
            DetailsResult result = resultMap.get(str);
            System.out.println("mediaIp: "+result.mediaIp+" uploadSuccessRate: "+result.uploadSuccessRate+" downloadSuccesRate: "+result.downloadSuccesRate);
        }
    }
    private void readHeaders(String fileName) throws Exception{
        
        String line = null;
        lines = new ArrayList<>();
        
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while((line = bufferedReader.readLine()) != null) {
            if(line.length()<1)continue;
            lines.add(line);
        }

        // Always close files.
        bufferedReader.close();
    }
    private void decriptLines(){
        cidDetailsList = new ArrayList<>();
        CallidDetailsDTO cidDetails = null;
        for(String str: lines){
            String [] array = str.split(" ");
            cidDetails = new CallidDetailsDTO();
            for(int i = 0;i<array.length;i++){
                if(array[i].equals("callID:")){
                    cidDetails.callID = array[i+1];
                    i++;
                }
                else if(array[i].equals("clientAudioSent:")){
                    cidDetails.clientAudioSent = Integer.parseInt(array[i+1].trim());
                    i++;
                }
                else if(array[i].equals("clientAudioReceived:")){
                    cidDetails.clientAudioReceived = Integer.parseInt(array[i+1].trim());
                    i++;
                }
                else if(array[i].equals("mediaAudioSent:")){
                    cidDetails.mediaAudioSent = Integer.parseInt(array[i+1].trim());
                    i++;
                }
                else if(array[i].equals("mediaAudioReceived:")){
                    cidDetails.mediaAudioReceived = Integer.parseInt(array[i+1].trim());
                    i++;
                }
                else if(array[i].equals("mediaIP:")){
                    cidDetails.mediaIP = array[i+1].trim();
                    i++;
                }
                else if(array[i].equals("status:")){
                    cidDetails.status = Integer.parseInt(array[i+1].trim());
                    i++;
                }
            }
            cidDetailsList.add(cidDetails);
//            System.out.println("callID: "+cidDetails.callID+" clientAudioSent: "+cidDetails.clientAudioSent+" clientAudioReceived: "+cidDetails.clientAudioReceived+" mediaAudioSent: "
//                    +cidDetails.mediaAudioSent+" mediaAudioReceived: "+cidDetails.mediaAudioReceived+" mediaIP: "+cidDetails.mediaIP+" status: "+cidDetails.status);
        }
    }
    
    private class CallidDetailsDTO {
	public String callID;
	public long disconnectTime;
	public long addedTime;
	public long clientAudioSent;
	public long clientAudioReceived;
	public long mediaAudioSent;
	public long mediaAudioReceived;
	public String mediaIP;
	public int status;
    }
    public class DetailsResult{
        String mediaIp;
        long totalClientSent;
        long totalClientReceived;
        long totalMediaSent;
        long totalMediaReceived;
        double uploadSuccessRate;
        double downloadSuccesRate;
        
        public DetailsResult(){
            totalClientSent = 0;
            totalClientReceived = 0;
            totalMediaSent = 0;
            totalMediaReceived = 0;
            uploadSuccessRate = 0;
            downloadSuccesRate = 0;
        }
    }

}
