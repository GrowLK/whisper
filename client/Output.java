package client;

import java.io.InputStream;
import java.lang.Thread;
import java.io.IOException;
import java.util.Queue;

import common.Data;
import common.Data.Type;

public class Output extends Thread {
	InputStream input;
    Signal signal;
    public Output(InputStream input_, Signal signal_) {
        input = input_;
        signal = signal_;
    }
    public void run() {
        try {
            byte[] buf = new byte[4096];
            int head=0, tail=0, len = 0;
            while(!signal.exiting) {
                len = input.read(buf, tail, 4096-tail);
                if(len == -1) break;
                
                while(tail<4096 && buf[tail]!=0) ++tail;
                if(tail == head) { head=0; tail=0; break; }
                if(tail == 4096) {
                	for(int i=head; i<tail; ++i)
                		buf[i-head] = buf[i];
                	tail = tail - head;
                	head = 0;
                }
                
                Data data = new Data(buf, head, tail-head);
                if(data.type != Type.MESSAGE) receive(data);
                else if(data.destination.equals("LOGIN")) login(data);
                else if(data.destination.equals("CONNECT")) connect(data);
                else if(data.destination.equals("DISCONNECT")) disconnect();
                else if(data.destination.equals("LOGOUT")) logout();
                else if(data.destination.equals("EXIT")) exit();
                
                head = (tail = tail+1);
            }
        } catch(IOException exception) {  }
    }
    
    private void login(Data data) {
    	String args = new String(data.content);
    	String[] argv = args.split("&");
    	String[] friends = argv[2].split(";");
    	String[] teams = argv[3].split(";");
    	
    	signal.account = new Account(argv[0], argv[1]);
    	for(int i=0; i<friends.length; ++i) {
    		Friend friend = new Friend(friends[i]);
    		signal.account.list.put("f-"+friend.name, friend);
    	}
    	for(int i=0; i<teams.length; ++i) {
    		Team team = new Team(teams[i]);
    		signal.account.list.put("t-"+team.name, team);
    	}
    	
    	System.out.println("login successfully");
        signal.updated = true;
    }
    
    private void connect(Data data) {
        signal.current = new String(data.content);
        
        Information information = signal.account.list.get(signal.current);
        Queue<Data> buf = information.buffer;
        
        Data temp = null;
    	while((temp=buf.poll()) != null)
    		receive(temp);

        System.out.println("connect successfully");
        signal.updated = true;
    }
    
    private void disconnect() {
        signal.current = null;
        
        System.out.println("disconnect successfully");
        signal.updated = true;
    }
    
    private void logout() {
        signal.account = null;
        signal.current = null;
        
        System.out.println("logout successfully");
        signal.updated = true;
    }
    
    private void exit() {
    	signal.account = null;
    	signal.current = null;
    	signal.exiting = true;
    	
    	System.out.println("exit successfully");
    	signal.updated = true;
    }
    
    private void receive(Data data) {
    	if(data.source.equals(signal.current) || data.destination.equals(signal.current)) {
        	String string = new String(data.content);
        	System.out.println(string);
        } else {
        	Information information = signal.account.list.get(data.source);
        	Queue<Data> buf = information.buffer;
        	buf.offer(data);
        }
    }
}
