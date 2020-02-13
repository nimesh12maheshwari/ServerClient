
package serverclient;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerClient {
    
    static public String name,ip;
    DataOutputStream dos;
    DataInputStream dis;
    ObjectOutputStream os=null;
    ObjectInputStream is=null;
    Socket s=null;
    Thread t1;
    private boolean isConnected = false;//for checking connection
    String sourceFilePath ;
    private FileEvent fileEvent = null;
    private String destinationPath = "./";
    private File dstFile = null;
    private FileOutputStream fileOutputStream = null;String showMessage;
    public chatbox obj;
    
    
	
    void Client(chatbox obj){
        
        try {
            this.obj=obj;
            s = new Socket(ServerClient.ip, 5056);//making connection with server
          
                // obtaining input and out streams 
            os = new ObjectOutputStream(s.getOutputStream()); 
          
           is = new ObjectInputStream(s.getInputStream());
          
           //Oject input stream established 
           
           
            
            receive();//for receiving messages we have run thread in receive function
        } catch (IOException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    void receive(){
        t1=new Thread(){
                public void run(){
                    while(true){
                        try {
                            
                            
                            Object oob = is.readObject();
                            //object received
                            if(oob instanceof String){ 
                              
                                obj.showMessage((String)oob);
                            }
                            else{
                                downloadFile(oob);// for saving file received
                          
                                obj.showMessage(showMessage);
                            }
                            
                            
                            
                        } catch (Exception ex) {
                            try {
                              //closing all streams used
                                is.close();os.close();s.close();
                                break;
                            } catch (IOException ex1) {
                                Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                            
                            System.out.println("receive");
                            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
            };
        t1.start(); // start thread for receiving messages
    }
    // for sending messages of string type
    void send(String send){
        
            try {
                
                    
                os.writeObject(send);
                os.flush();
               
        } catch (IOException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
                
            
        
    }
    
    public void sendFile() {
        fileEvent = new FileEvent(); // creating object of file Event to send file by storing in it 
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());// saving file name
        String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);// path 
        fileEvent.setDestinationDirectory(destinationPath);
        fileEvent.setFilename(fileName);// setting file name
        fileEvent.setSourceDirectory(sourceFilePath);fileEvent.userName=ServerClient.name;
        File file = new File(sourceFilePath);// opening file selected for sending
        
        // now saving in byte array
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
        fileEvent.setStatus("Success"); //  all contents are successfully filled
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
        os.flush();
       
        Thread.sleep(3000);// waiting time for sending files

        } catch (IOException e) {
        e.printStackTrace();
        } catch (InterruptedException e) {
        e.printStackTrace();
        }

    }
    // for download transfered file through object
    public void downloadFile(Object oob) {
        try {
        FileEvent fileEvent=(FileEvent)oob;    

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
// downloading time

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
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
}


    
