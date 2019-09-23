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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class HeaderAnalyzer {
    
    private static ArrayList<String> lines;
    //private static String fileName = "headers_08281_dump.txt";
    private static String fileName = "Working Header.txt";
    private static HashMap<String,String> result;
    public static void test(){
        result = new HashMap<>();
        try {
            readLines();
            //processWorkingHeader();
            processWorkingIp();
            //removeUnwantedHeader();
            //findNonDuplicateLines();
            printLine();
            //printLineWithColonSeperator();
        } catch (Exception e) {
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
            lines.add(line);
        }

        // Always close files.
        bufferedReader.close();
    }
    public static void processWorkingHeader(){
        result = new HashMap<>();
        for(String str: lines){
            String[] arr = str.trim().split(" ");
            for(int i=0;i<arr.length;i++){
                if(arr[i].equalsIgnoreCase("=") && !result.containsKey(arr[i+1])){
                    
                    //if(!arr[i+1].contains("95f44324341353137"))
                    {
                        result.put(arr[i+1], arr[i+1]);
                    }
                }
            }
        }
        
    }
    public static void processWorkingIp(){
        
        for(String str: lines){
            String[] arr = str.trim().split(" ");
            for(int i=0;i<arr.length;i++){
                if(arr[i].equalsIgnoreCase("IP") && !result.containsKey(arr[i+1])){
                    
                    //if(!arr[i+1].contains("95f44324341353137"))
                    {
                        result.put(arr[i+1], arr[i+1]);
                    }
                }
            }
        }
        
    }
    public static void removeUnwantedHeader(){
        ArrayList<String> validList = new ArrayList<>();
        //result = new HashMap<>();
        for(String str: lines){
            if(!str.contains("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000")){
                //result.put(str, str);
                validList.add(str);
            }
        }
        result.clear();
        for(String str: validList){
            result.put(str, str);
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
            String line = str.toUpperCase() + "\n";
            fos.write(line.getBytes());
        }
    }
    public static void findNonDuplicateLines(){
        result = new HashMap<>();
        for(String str: lines){
            if(result.containsKey(str)){
                result.remove(str);
            }else{
                result.put(str, str);
            }
        }
    }

    private static void printLineWithColonSeperator() throws Exception {
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
            String line = str.toUpperCase() + ";";
            fos.write(line.getBytes());
        }
    
    }
}
