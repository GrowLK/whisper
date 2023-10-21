package common;

import java.lang.Thread;

public class Hook extends Thread {
    private Terminate terminate;
    public Hook(Terminate terminate_) {
    	terminate = terminate_;
    }
    public void run() {
    	terminate.terminate();
    }
    public interface Terminate {
        public void terminate();
    }
}