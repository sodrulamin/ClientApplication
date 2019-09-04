/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialertest;

import clientapplication.Base64;
import clientapplication.Functions;
import static clientapplication.Functions.getRandomData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 *
 * @author Sodrul Amin Shaon
 */
public class HttpProtocolClass {
    private static String hexChars = "0123456789ABCDEF";
    private static DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss");
    private static byte [] characters="abcdefghijklmnopqrstuvwxyz".getBytes();
    
    public static String NEWLINE = "\r\n";
    private static Random random = new Random();
    private static String http = "HTTP/1.0 ";
    private static String OkString = "200 OK";
    private static String [] statStrings = {
        "302 Found",
        "304 NOT MODIFIED",
        "403 Forbidden",
        "204 No Content",
        "101 Switching Protocols",
        "301 Moved Permanently",
        "302 Moved Temporarily"
    };
    private static String cacheControl = "Cache-Control: private,max-age=0";
    private static String contentLength = "Content-Length: ";
    private static String content = "Content-Type: ";
    private static String [] contentType = {
        "application/javascript",
        "font/woff2"
    };
    private static String accessControl = "Access-Control-Allow-Origin: *";
    private static String connection="Connection: keep-alive";
    private static String expires = "Expires: ";
    private static String lastModified = "Last-Modified: ";
    private static String etag = "ETag: \"{";
    private static String resourceTag = "ResourceTag: rt:";
    private static String publicExtension = "Public-Extension: http://schemas.microsoft.com/repl-2";
    private static String sprequestGuid = "SPRequestGuid: ";
    private static String requestID = "request-id: ";
    private static String [] xFramOption = {
        "X-FRAME-OPTIONS: SAMEORIGIN"
    };
    private static String spRequestDuration = "SPRequestDuration: ";
    private static String spLatency = "SPIisLatency: ";
    private static String xContentType = "X-Content-Type-Options: ";
    private static String date = "Date: ";
    private static String setCookie = "Set-Cookie: ";
    private static String path = "Path=/";
    private static String userAgent="User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
            " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
    //private static String connection="Connection: keep-alive";
    private static String acceptEncoding="Accept-Encoding: gzip, deflate";
    private static String acceptLanguage="Accept-Language: en-US,en;q=0.9,bn;q=0.8,hi;q=0.7";
    private static String accept="Accept: */*";
    private static String[] referer={
            "espncricinfo",
            "google",
            "facebook",
            "twitter",
            "prothomalo",

    };
//    
//    private static String NEWLINE = "\r\n";
//    private static String contentLength = "Content-Length: ";
    private static String[] webDomainList = {
            ".com",
            ".net",
            ".org",
            ".int",
            ".edu",
            ".gov",
            ".mil"
    };
    private static String[] getHeaders={
            "ScriptResource.axd?d=",
            "WebResource.axd?d=",
            "_layouts/15/init.js?rev=",
            "_layouts/15/1033/initstrings.js?rev=",
            "_layouts/15/1033/strings.js?rev=",
            "_layouts/15/ie55up.js?rev=",
            "v.gif?aadib.ae&u=",
            "_layouts/15/sp.init.js?rev=",
            "_layouts/15/sp.core.js?rev=",
            "_layouts/15/1033/styles/Themable/corev15.css?rev=",
            "_layouts/15/blank.js?rev=",
            "PublishingImages/logo.png?rev=",
            "DependencyHandler.axd?s=",
            "loggerServices/widgetGlobalEvent?eT=",
            "combiner/i?img=",
            "/utils/get?url=",
            "transform/v3/eyJ?test=",
            "FastcastService/pubsub/profiles/12000?TrafficManager-Token=",
    };
    private static String [] folders = {
            "images","media","css","styles","browser","uploads"
    };
    public static int readByte(InputStream is,byte [] data,int len){
        int rl, crl,minLen;
        minLen=len;
        crl = 0;
        try {
            while (crl < minLen) {

                rl = is.read(data, crl, minLen - crl);
                if (rl < 0) // socket close case
                {
                    break;
                }
                crl += rl;

            }
        } catch (IOException ex) {
        }
        return crl;
    }
    
    public static byte [] getRequest(byte [] data,int len){
        //String GETHeader=new String(Base64.encode(Functions.getRandomData(len),0));
        
        //len  = Base64.base64Encode(data,0,len);
        data = Base64.encode(data);
        len = data.length;
        String GETHeader=new String(data,0,len);
        String randHeader=getHeaders[random.nextInt(getHeaders.length)];
        GETHeader="GET /"+randHeader+GETHeader+" HTTP/1.1 ";
        String host="Host: "+getRandomWord()+"."+getRandomWord()
                +webDomainList[random.nextInt(webDomainList.length)];
        String refererUrl = "Referer: http://www."+referer[random.nextInt(referer.length)]+".com";
        String cookie="Cookie: "+getRandomWord(2+random.nextInt(5)).toUpperCase()
                +"="+new String(Base64.encode(getRandomData(60)));
        String fullReqString=GETHeader+NEWLINE+host+NEWLINE+connection+NEWLINE+userAgent+NEWLINE
                +accept+NEWLINE+refererUrl+NEWLINE+acceptEncoding+NEWLINE+acceptLanguage+NEWLINE
                +cookie+NEWLINE+NEWLINE;
        //System.out.println(fullReqString);

        return fullReqString.getBytes();
    }
    public static int receiveClientData(InputStream is,byte [] receivedData) throws IOException {
        String temp = "Hi";

        String requestHeader = "";
        byte [] data = new byte[2048];
        while (temp!=null && !temp.equals("")) {
            temp = Functions.readLine(is,data);
            //System.out.println(temp);
            requestHeader += temp;
            //break;
        }
        int dataLength = 0,indexOfDataLength = 0, endOfDataLength = 0;
        indexOfDataLength = requestHeader.indexOf(contentLength);
        if(indexOfDataLength < 0){
            return 0;
        }
        indexOfDataLength += contentLength.length();
        endOfDataLength = requestHeader.indexOf(NEWLINE,indexOfDataLength);
        if(endOfDataLength <0)return 0;
        dataLength = Integer.parseInt(
                requestHeader.substring(indexOfDataLength,endOfDataLength).trim());

        dataLength = readByte(is,receivedData,dataLength);
        return dataLength;
    }
    public static String receiveData(InputStream is) throws IOException{
        String temp = "Hi";
        
        String requestHeader = "";
        
            while (temp!=null && !temp.equals("")) {
                temp = readLine(is);
                //System.out.println(temp);
                requestHeader += temp;
                //break;
            }
        
        String data=dataFromGet(requestHeader);
        //String data=dataFromDataField(requestHeader);
        return data;
    }
    private static String dataFromGet(String request){
        int dataStartIndex=request.indexOf("=");
        //System.out.println("dataStartIndex: "+dataStartIndex);
        if(dataStartIndex<0)return null;
        int dataEndEndex=request.indexOf(" HTTP/1.1",dataStartIndex);
        if(dataEndEndex<dataStartIndex+1)return null;
        //System.out.println("dataStartIndex: "+dataStartIndex+" dataEndEndex: "+dataEndEndex);
        String data=request.substring(dataStartIndex+1, dataEndEndex);
        
        return data;
    }
    private static String dataFromDataField(String request){
        //System.out.println("got request: "+requestHeader);
        int dataStartIndex=request.indexOf("Data:");
        //System.out.println("dataStartIndex: "+dataStartIndex);
        if(dataStartIndex<0)return null;
        int dataEndEndex=request.indexOf(NEWLINE,dataStartIndex);
        if(dataEndEndex<dataStartIndex+6)return null;
        //System.out.println("dataStartIndex: "+dataStartIndex+" dataEndEndex: "+dataEndEndex);
        String data=request.substring(dataStartIndex+6, dataEndEndex);
        
        return data;
    }
    
    private static String readLine(InputStream is) throws IOException {
        byte [] data=new byte[2048];
        StringBuilder sb=new StringBuilder();
        int index=0;
            
            while(true){
                int a=is.read(data,index,1);
                if(a==-1)break;
                if(data[index]==0x0a){
                    if(index>0 && data[index-1]==0x0d)break;
                }
                index++;
            }
        
        String str=new String(data,0,index+1);
        if(index>2){
            return str;
        }
        return "";
    }
    
    
    public static String getHttpResponse(byte [] data,int len){
        String dataString = new String(Base64.encode(data, len));
        //System.out.println("Sending: \n"+dataString);
        String response = "";
        if(random.nextInt(10)>=8)response = http + statStrings[random.nextInt(statStrings.length)]+NEWLINE;
        else response = http + OkString+NEWLINE;
        response = response + cacheControl + NEWLINE;
        response = response + contentLength + dataString.length() + NEWLINE;
        response = response + content + contentType[random.nextInt(contentType.length)] + NEWLINE;
        response = response + expires + formatter.format(random.nextInt()) + " GMT" + NEWLINE;
        response = response + lastModified + formatter.format(random.nextInt()) + " GMT" + NEWLINE;
        response = response + etag + getRandomHextString(8) + "-" + getRandomHextString(4) + "-" + getRandomHextString(4) + "-" + getRandomHextString(4) + "-" + getRandomHextString(12) + "}," + random.nextInt(100)+"\""+NEWLINE;
        response = response + resourceTag + getRandomHextString(8) + "-" + getRandomHextString(4) + "-" + getRandomHextString(4) + "-" + getRandomHextString(4) + "-" + getRandomHextString(12) + "@0000000000" + random.nextInt(100)+"\""+NEWLINE;
        response = response + publicExtension + NEWLINE;
        response = response + sprequestGuid + getRandomHextString(8) + "-" + getRandomHextString(4) + "-" + getRandomHextString(4) + "-" + getRandomHextString(4) + "-" + getRandomHextString(12) + NEWLINE;
        response = response + requestID + getRandomHextString(8) + "-" + getRandomHextString(4) + "-" + getRandomHextString(4) + "-" + getRandomHextString(4) + "-" + getRandomHextString(12) + NEWLINE;
        response = response + xFramOption[random.nextInt(xFramOption.length)]+NEWLINE;
        response = response + spRequestDuration + random.nextInt(50)+NEWLINE;
        response = response + spLatency + random.nextInt(50)+NEWLINE;
        response = response + xContentType + getRandomWord() + NEWLINE;
        response = response + xFramOption[random.nextInt(xFramOption.length)]+NEWLINE;
        response = response + date + formatter.format(System.currentTimeMillis())+" GMT"+NEWLINE;
        response = response + connection + NEWLINE;
        response = response + setCookie + path + NEWLINE;
        response = response + NEWLINE;
        response = response + dataString;
        return response;
    }
    private static String getRandomHextString(int len){
        if(len<=0)return null;
        StringBuilder sb = new StringBuilder();
        int n = hexChars.length();
        for(int i=0;i<len;i++){
            sb.append(hexChars.charAt(random.nextInt(n)));
        }
        return sb.toString();
    }
    private static String getRandomWord(){
        int len=5+random.nextInt(10);
        byte [] value=new byte[len];
        for(int i=0;i<len;i++){
            value[i]=characters[random.nextInt(characters.length)];
        }
        return new String(value);
    }
    private static String getRandomWord(int len){
        byte [] value=new byte[len];
        for(int i=0;i<len;i++){
            value[i]=characters[random.nextInt(characters.length)];
        }
        return new String(value);
    }
}
