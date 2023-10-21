package client;

import java.lang.Thread;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Communicatee extends Thread {
	Socket socket;
	Input input;
	Output output;
	
    public Communicatee(String server_, int port_) {
    	try {
    		socket = new Socket(server_, port_);
    		Runtime.getRuntime().addShutdownHook(this);
    	} catch(IOException exception) {  }
    }
    
    public void run() { terminate(); }
    
    public void execute() {
    	try {
    		OutputStream out = socket.getOutputStream();
    		InputStream in = socket.getInputStream();
            Signal signal = new Signal();
            input = new Input(out, signal);
            output = new Output(in, signal);
            input.start(); output.start();
            
            while(!signal.exiting) Thread.yield();
            
            socket.close();
        } catch(IOException exception) {  }
    }
    
    public void terminate() {
    	try {
	    	if(input!=null && input.scanner!=null)
	    		input.scanner.close();
	    	if(socket != null)
	    		socket.close();
    	} catch(IOException exception) { }
    }
}

class Signal {
    public boolean updated = false;
    public Account account = null;
    public String current = null;
    public boolean exiting = false;
    
    public void update() {
        while(!updated) Thread.yield();
        updated = false;
    }
}
