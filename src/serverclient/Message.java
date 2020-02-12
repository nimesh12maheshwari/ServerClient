/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverclient;

/**
 *
 * @author Lenovo
 */
import java.io.*;

public class Message implements Serializable {
   private static final long serialVersionUID = -5399605122490343339L;
    String str;

    Message(String str){
        this.str=str;
    }
    
}