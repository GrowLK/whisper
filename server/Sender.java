package server;

import java.io.IOException;
import java.lang.InterruptedException;
import java.io.OutputStream;
import java.lang.Thread;
import java.util.Queue;
import java.util.LinkedList;

import common.Data;

public class Sender extends Thread {
	OutputStream output;
    Queue<Data> buffer;
    public Sender(OutputStream output_) {
        output = output_;
        buffer = new LinkedList<Data>();
    }
    
    public void run() {
        try {
            while(true) {
                Data data = null;
                while((data=buffer.poll()) == null)
                    Thread.yield();
                output.write(data.getBytes());
                Thread.yield();
            }
        } catch(IOException exception) {  }
    }
    
    public void offer(Data data) {
        while(!buffer.offer(data))
        	Thread.yield();
    }
    
    public Data poll() {
        Data data = null;
        while((data=buffer.poll()) == null)
            Thread.yield();
        return data;
    }
    
    public void exit() {
        try {
            Thread.sleep(100);
            while(!buffer.isEmpty()) {
                while(!buffer.isEmpty())
                    Thread.yield();
                Thread.sleep(100);
            }
            output.close();
        } catch(InterruptedException exception) {
        } catch(IOException exception) {  }
    }
}
