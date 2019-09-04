/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileanalyzer;

import clientapplication.Functions;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class CallPacketAnalyzer {
    static ArrayList<String> lines = new ArrayList<>();
    private static String fileName = "shaontestdialer_dump.txt";
    
    public static void test(){
        try {
            readHeaders();
            process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private static void readHeaders() throws Exception{
        
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
    private static void process(){
        for(String line: lines){
            try{
                byte [] data = Functions.hexStringToByteArray(line);
                
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
