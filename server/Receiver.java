package server;

import java.io.IOException;
import java.lang.InterruptedException;
import java.io.InputStream;
import java.lang.Thread;
import java.util.Queue;
import java.util.LinkedList;

import common.Data;

public class Receiver extends Thread {
	InputStream input;
    Queue<Data> buffer;
    public Receiver(InputStream input_) {
        input = input_;
        buffer = new LinkedList<Data>();
    }
    
    public void run() {
        try {
            int len; byte[] buf = new byte[4096];
            while((len = input.read(buf)) != 0) {
            	if(len == -1) break;
                Data data = new Data(buf, 0, len);
                while(!buffer.offer(data)) Thread.yield();
            }
        } catch(IOException exception) {  }
    }
    
    public void offer(Data data) {
        while(!buffer.offer(data)) Thread.yield();
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
            input.close();
        } catch(InterruptedException exception) {
        } catch(IOException exception) {  }
    }
}
