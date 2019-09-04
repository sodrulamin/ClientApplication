/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class CallLimitAnalyzer {
    
    private static ArrayList<String> lines;
    private static String fileName = "test.txt";
    private static String writeFileName = "testWrite.txt";
    private static HashMap<String,String> result;
    private static HashMap<String,callInfo> limitCrossMap = new HashMap<>();
    
    public static void test(){
        try{
            readLines();
            //processLines();
            //findMaxCall();
            findLimitCrossTree();
            printLine();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void readLines() throws Exception{
        String line = null;
        lines = new ArrayList<>();
        
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while((line = bufferedReader.readLine()) != null) {
            if(line.length()<1)continue;
//            if(line.contains("Rejecting call request"))
                lines.add(line);
        }

        // Always close files.
        bufferedReader.close();
    }
    public static void processLines(){
        result = new HashMap<>();
        for(String str: lines){
            if(str.contains("LimitCross")){
                String[] arr = str.trim().split(" ");
                for(int i=0;i<arr.length;i++){
                    if(arr[i].equalsIgnoreCase("LimitCross:")){
                        result.put(arr[i+1], str);
                    }
                }
            }
        }
    }
    public static void findMaxCall(){
        result = new HashMap<>();
        for(String str: lines){
            if(str.contains("LimitCross")){
                String [] arr = str.split(" ");
                String opcode = "";
                int maxCall = 0;
                int callLimit = 0;
                for(int i = 0;i<arr.length;i++){
                    if(arr[i].contains("LimitCross:")){
                        opcode = arr[i+1].trim();
                    }else if(arr[i].contains("callLimit:")){
                        callLimit = Integer.parseInt(arr[i+1].trim());
                    }else if(arr[i].contains("totalCall:")){
                        maxCall = Integer.parseInt(arr[i+1].trim());
                    }
                }
                callInfo info = limitCrossMap.get(opcode);
                if(info == null){
                    info = new callInfo(opcode,callLimit,maxCall);
                    limitCrossMap.put(opcode, info);
                }
                if(info.maxCall < maxCall){
                    info.maxCall = maxCall;
                }
                
            }
        }
    }
    public static void printLine() throws Exception{
        File file  = new File(writeFileName);
        file.delete();
        FileOutputStream fos = new FileOutputStream(file);
        Collection<String> resultLines = result.values();
        for(String str: resultLines){
            String line = str + "\n";
            fos.write(line.getBytes());
        }
    }
    public static void findLimitCrossTree(){
        result = new HashMap<>();
        for(String str: lines){
            if(str.contains("LimitCross: S556677")){
                result.put(str, str);
            }
        }
    }
    public static class callInfo{
        public String opcode;
        int callLimit;
        int maxCall;
        public callInfo(String s,int limit,int maxCall){
            opcode = s;
            this.callLimit = limit;
            this.maxCall = maxCall;
        }
        public String toString(){
            return "Opcode: "+opcode+" callLimit: "+callLimit+" maxCall: "+maxCall;
        }
    }
}
