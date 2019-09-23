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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class OpcodeGrouping {
    private static ArrayList<String> lines;
    //private static String fileName = "headers_08281_dump.txt";
    private static String fileName = "Working Header.txt";
    public static void restore(){
        String [] opcodes = {
            "S0010",
            "S0011",
            "S0012",
            "S0016",
            "S0021",
            "S0018"
        };
        HashMap<String,String> result = new HashMap<String,String>();
        HashMap<String,String> groupMap = new HashMap<String,String>();
        String line = null;
        for(String opcode: opcodes)
        {
            try {
                FileReader fileReader = new FileReader(opcode + ".txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while((line = bufferedReader.readLine()) != null) {
                    if(line.length()<1)continue;
                    line = line.trim();
                    String setting = result.get(line);
                    if(setting == null)
                    {
                        setting = opcode;
                    }
                    else
                    {
                        setting = setting + "," + opcode;
                    }
                    result.put(line, setting);
                }

                // Always close files.
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Set<String> opcodeList = result.keySet();
        for(String str: opcodeList)
        {
            String mapString = result.get(str);
            String alreadyMapped = groupMap.get(mapString);
            if(alreadyMapped == null)
            {
                alreadyMapped = str;
            }
            else
            {
                alreadyMapped = alreadyMapped + "," + str;
            }
            groupMap.put(mapString, alreadyMapped);
        }
        
        try {
            File file  = new File("resultMap.txt");
            file.delete();
            FileOutputStream fos = new FileOutputStream(file);
            
            Set<String> mapStringSet = groupMap.keySet();
            for(String str: mapStringSet){
                line = str + " = " + groupMap.get(str) + "\n";
                fos.write(line.getBytes());
            }
        } catch (Exception e) {
        }
    }
}
