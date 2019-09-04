/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class VoiceUploadDownloadAnalyzer {
    private static ArrayList<String> lines;
    private static String fileName = "test.txt";
    private static HashMap<String,String> result;
    public static void test(){
        try{
            readLines();
            processLines();
            printLine();
        }catch(Exception e){}
    }
    
    public static void readLines() throws Exception{
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
    public static void processLines(){
        result = new HashMap<>();
        for(String str: lines){
            String[] arr = str.trim().split(" ");
            for(int i=0;i<arr.length;i++){
                if(arr[i].equalsIgnoreCase("voiceUploadRatio:")){
                    short upload = Short.parseShort(arr[i+1]);
                    if(upload > 100)
                        result.put(str, str);
                }
                if(arr[i].equalsIgnoreCase("voiceDownloadRatio:")){
                    short download = Short.parseShort(arr[i+1]);
                    if(download > 100)
                        result.put(str, str);
                }
            }
        }
    }
    public static void printLine() throws Exception{
        File file  = new File(fileName);
        file.delete();
        FileOutputStream fos = new FileOutputStream(file);
//        for(int i=0;i<result.size();i++){
//            line = headerList.get(i);
//            line = line.toUpperCase();
//            line = "0x"+line+"\n";
//            fos.write(line.getBytes());
//        }
        Collection<String> resultLines = result.values();
        for(String str: resultLines){
            String line = str + "\n";
            fos.write(line.getBytes());
        }
    }
}
