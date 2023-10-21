package server;

import java.io.IOException;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;

public class Communicator extends Thread {
    ServerSocket socket;

    public Communicator(int port_) {
    	try {
			socket = new ServerSocket(port_);
			Runtime.getRuntime().addShutdownHook(this);
		} catch (IOException exception) {  }
    }
    
    public void run() { terminate(); }

    public void execute() {
    	try {
	        while(true) {
	            Socket sock = socket.accept();
	            Controller controller = new Controller(sock);
	            controller.start();
	        }
    	} catch(IOException exception) {  }
    }

    public void terminate() {
    	try {
	    	if(socket != null)
	    		socket.close();
    	} catch(IOException exception) { }
    }
}