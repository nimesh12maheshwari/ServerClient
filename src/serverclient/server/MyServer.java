/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverclient.server;
import java.io.*; 
import java.util.*; 
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyServer {
    
    static public String name,ip;
    DataOutputStream dos;
    DataInputStream dis;
    ObjectOutputStream os=null,os2;
    ObjectInputStream is=null,is2;
    Socket s=null,s2;
    Thread t1;
    private boolean isConnected = false;
    String sourceFilePath = "C:/Users/Nishu/Downloads/download.png";
    private FileEvent fileEvent = null;
    private String destinationPath = "./";
    private File dstFile = null;
    private FileOutputStream fileOutputStream = null;String showMessage;
    ServerSocket ss; 
     
    public static void main(String args[]){
         MyServer ob=new MyServer();
         ob.Server();
         
    }
    
    void Server(){
       
        try {
            ss = new ServerSocket(5056);
            s=ss.accept();
            Server2();
            System.out.println("A new client is connected : " + s); 
                  
                // obtaining input and out streams 
            //dos = new DataOutputStream(s.getOutputStream());
            //dis = new DataInputStream(s.getInputStream()); 
            is = new ObjectInputStream(s.getInputStream());
            os = new ObjectOutputStream(s.getOutputStream());
          
            //os.flush();
            System.out.println("input Oject stream established");
             //is = new ObjectInputStream(s.getInputStream());
            System.out.println("Oject stream established");
            receive(is,1);   
        } catch (IOException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void Server2(){
         
        try {
            
            s2=ss.accept();
            System.out.println("A new client is connected : " + s2); 
                  
                // obtaining input and out streams 
            //dos = new DataOutputStream(s.getOutputStream());
            //dis = new DataInputStream(s.getInputStream()); 
            is2 = new ObjectInputStream(s2.getInputStream());
            os2 = new ObjectOutputStream(s2.getOutputStream());
          
            //os.flush();
            System.out.println("input Oject stream established");
             //is = new ObjectInputStream(s.getInputStream());
            System.out.println("Oject stream established");
            receive(is2,2);   
        } catch (IOException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void receive(ObjectInputStream i,int num){
        ObjectInputStream is=i;
        t1=new Thread(){
                public void run(){
                    while(true){
                        try {
                            System.out.println("receive while(true)");
                            //String received = dis.readUTF();
                            Object oob = is.readObject();
                            System.out.println("object received");
                            if(oob instanceof String){
                                //Message m=(Message)oob;
                                System.out.println("after readUTF "+oob);
                                if(num==1){
                                    send((String)oob,os2);
                                }
                                else{
                                    send((String)oob,os);
                                }
                            }
                            else{
                                System.out.println("before download");
                                if(num==1){
                                    os2.writeObject(oob);
                                    os2.flush();
                                    System.out.println("Done...Going to exit");
                                    //Thread.sleep(3000);
                                    
                                }
                                else{
                                    os.writeObject(oob);
                                    os.flush();
                                    System.out.println("Done...Going to exit");
                                    //Thread.sleep(3000);
                                }
                                System.out.println("back in receive thread after donwloadFile"+showMessage);
                                
                            }
                            //System.out.println("after readUTF "+received);
                            
                            
                        } catch (Exception ex) {
                            try {
                                System.out.println("in receive thread exception");
                                is.close();os.close();s.close();s2.close();
                                break;
                            } catch (IOException ex1) { 
                                Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                            
                            System.out.println("receive");
                            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
            };
        t1.start();
    }
    
    void send(String send,ObjectOutputStream os){
        
            try {
                System.out.println("in send");
                //dos.writeUTF(send);
                //dos.flush();
                //Message message = new Message(send);      
                os.writeObject(send);
                os.flush();
                System.out.println("after flush"+send);
        } catch (IOException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        } 
                
            
        
    }
    
    public void sendFile() {
        fileEvent = new FileEvent();
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
        fileEvent.setDestinationDirectory(destinationPath);
        fileEvent.setFilename(fileName);
        fileEvent.setSourceDirectory(sourceFilePath);fileEvent.userName="nim";
        File file = new File(sourceFilePath);
        if (file.isFile()) {
        try {
        DataInputStream diStream = new DataInputStream(new FileInputStream(file));
        long len = (int) file.length();
        byte[] fileBytes = new byte[(int) len];
        int read = 0;
        int numRead = 0;
        while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
        read = read + numRead;
        }
        fileEvent.setFileSize(len);
        fileEvent.setFileData(fileBytes);
        fileEvent.setStatus("Success");
        } catch (Exception e) {
        e.printStackTrace();
        fileEvent.setStatus("Error");
        }
        } else {
        System.out.println("path specified is not pointing to a file");
        fileEvent.setStatus("Error");
        }
        //Now writing the FileEvent object to socket
        try {
        os.writeObject(fileEvent);
        System.out.println("Done...Going to exit");
        Thread.sleep(3000);

        } catch (InterruptedException e) {
        e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void downloadFile(Object oob) {
        try {
        FileEvent fileEvent=(FileEvent)oob;    
        System.out.println("in downloadFile");
        if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
            System.out.println("Error occurred ..So exiting");
        
        }
        showMessage= fileEvent.userName+": "+fileEvent.getDestinationDirectory()+fileEvent.getFilename();
        String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
        if (!new File(fileEvent.getDestinationDirectory()).exists()) {
        new File(fileEvent.getDestinationDirectory()).mkdirs();
        }
        dstFile = new File(outputFile);
        fileOutputStream = new FileOutputStream(dstFile);
        fileOutputStream.write(fileEvent.getFileData());
        fileOutputStream.flush();
        System.out.println("Output file : " + outputFile + " is successfully saved ");
        Thread.sleep(3000);


        } catch (IOException e) {
        e.printStackTrace();
        } catch (InterruptedException e) {
        e.printStackTrace();
        }
    }
    
    void close(){
        try {
            System.out.println("Socket Closed");
            os.close();
            is.close();
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
}
