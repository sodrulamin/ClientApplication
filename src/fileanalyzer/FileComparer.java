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
public class FileComparer {
    private static ArrayList<String> lines,lines2;
    private static String fileName1 = "test.txt",fileName2 = "Working Header.txt";
    private static HashMap<String,String> result;
    public static void test(){
        try {
            readLines();
            //processWorkingHeader();
            //removeUnwantedHeader();
            findUncommonLines();
            printLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public static void readLines() throws Exception{
        String line = null;
        lines = new ArrayList<>();
        lines2 = new ArrayList<>();
        
        FileReader fileReader = new FileReader(fileName1);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while((line = bufferedReader.readLine()) != null) {
            if(line.length()<1)continue;
            line = line.toUpperCase();
            lines.add(line);
        }

        // Always close files.
        bufferedReader.close();
        
        fileReader = new FileReader(fileName2);
        bufferedReader = new BufferedReader(fileReader);
        while((line = bufferedReader.readLine()) != null) {
            if(line.length()<1)continue;
            line = line.toUpperCase();
            lines2.add(line);
        }

        // Always close files.
        bufferedReader.close();
    }
    public static void processWorkingHeader(){
        result = new HashMap<>();
        for(String str2: lines2){
            boolean found = false;
            for(String str: lines){
                if(str2.contains(str)){
                    found = true;
                    break;
                }
            }
            if(!found){
                result.put(str2, str2);
            }
        }
        
    }
    public static void findUncommonLines(){
        result = new HashMap<>();
        for(String str2: lines2){
            boolean found = false;
            for(String str: lines){
                if(str2.equals(str)){
                    found = true;
                    break;
                }
            }
            if(!found){
                result.put(str2, str2);
            }
        }
    }
    public static void removeUnwantedHeader(){
        result = new HashMap<>();
        for(String str: lines){
            if(!str.contains("95f44324341353137"))
                result.put(str, str);
        }
        
    }
    public static void printLine() throws Exception{
        File file  = new File(fileName2);
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
}
