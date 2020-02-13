//class file for storing message as object
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