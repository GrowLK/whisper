package server;

import java.io.IOException;
import java.lang.Thread;
import java.net.Socket;
import java.util.Date;

import common.Data;
import common.Data.Type;
import server.User.Status;

public class Controller extends Thread {
    Socket socket;
    User user;
    User[] session;
    Receiver receiver;
    Sender sender;
    boolean exiting;
    public Controller(Socket socket_) {
        socket = socket_;
        exiting = false;
    }
    public void run() {
        try {
        	receiver = new Receiver(socket.getInputStream());
            sender = new Sender(socket.getOutputStream());
            receiver.start(); sender.start();
            while(!exiting) {
                Data data = receiver.poll();
                if(data.type != Type.COMMAND) handle(data);
                else if(data.source.equals("LOGIN")) login(data);
                else if(data.source.equals("CONNECT")) connect(data);
                else if(data.source.equals("DISCONNECT")) disconnect();
                else if(data.source.equals("LOGOUT")) logout();
                else if(data.source.equals("EXIT")) exit();
            }
        } catch(IOException exception) { }
    }
    
    private void login(Data data) {
    	String args = new String(data.content);
        String[] argv = args.split("&");
        user = Users.verify(argv[0], argv[1]);
        if(user == null) { logout(); return; }
        
        user.status = Status.ONLINE;
        user.receiver = receiver;
        user.sender = sender;
        
        String message = user.name+"&"+user.password+"&";
        if(user.users != null) {
        	int i = 0;
            for(; i<user.users.length-1; ++i)
            	message += Users.findByIdentity(user.users[i]).name+";";
            message += Users.findByIdentity(user.users[i]).name;
        } else message += ";";
        message += "&";
        if(user.groups != null) {
        	int i = 0;
            for(; i<user.groups.length-1; ++i)
            	message += Groups.findByIdentity(user.groups[i]).name+";";
            message += Groups.findByIdentity(user.groups[i]).name;
        } else message += ";";
        
        Type type = Type.MESSAGE;
        Date date = new Date(System.currentTimeMillis());
        String source = "SYSTEM";
        String destination = "LOGIN";
        byte[] content = message.getBytes();
        Data temp = new Data(type, date, source, destination, content);
        sender.offer(temp);
        
        while((temp=user.buffer.poll()) != null)
        	sender.offer(temp);
    }
    
    private void connect(Data data) {
    	String args = new String(data.content);
        String[] argv = args.split("&");
        String current = null;
        if(argv[0].equals("friend")) {
            session = new User[1];
            session[0] = Users.findByName(argv[1]);
            current = "f-"+argv[1];
        } else if(argv[0].equals("team")) {
            Group group = Groups.findByName(argv[1]);
            session = new User[group.members.length-1];
            int i = 0;
            for(; group.members[i]!=user.identity; ++i)
                session[i] = Users.findByIdentity(group.members[i]);
            for(; i<session.length; ++i)
                session[i] = Users.findByIdentity(group.members[i+1]);
            current = "t-"+argv[1];
        }
        
        if(session == null) { disconnect(); return; }
        
        Type type = Type.MESSAGE;
        Date date = new Date(System.currentTimeMillis());
        String source = "SYSTEM";
        String destination = "CONNECT";
        byte[] content = current.getBytes();
        Data temp = new Data(type, date, source, destination, content);
        sender.offer(temp);
    }
    
    private void disconnect() {
        session = null;
        
        Type type = Type.MESSAGE;
        Date date = new Date(System.currentTimeMillis());
        String source = "SYSTEM";
        String destination = "DISCONNECT";
        byte[] content = "".getBytes();
        Data temp = new Data(type, date, source, destination, content);
        sender.offer(temp);
    }
    
    private void logout() {
    	session = null;
    	user.status = Status.OFFLINE;
        user = null;
        
        Type type = Type.MESSAGE;
        Date date = new Date(System.currentTimeMillis());
        String source = "SYSTEM";
        String destination = "LOGOUT";
        byte[] content = "".getBytes();
        Data temp = new Data(type, date, source, destination, content);
        sender.offer(temp);
    }
    
    private void exit() throws IOException {
    	session = null;
    	if(user != null) {
	    	user.status = Status.OFFLINE;
	        user = null;
    	}
        
        Type type = Type.MESSAGE;
        Date date = new Date(System.currentTimeMillis());
        String source = "SYSTEM";
        String destination = "EXIT";
        byte[] content = "".getBytes();
        Data temp = new Data(type, date, source, destination, content);
        sender.offer(temp);
        
        receiver.exit();
        sender.exit();
        exiting = true;
    }
    
    private void handle(Data data) {
    	for(int i=0; i<session.length; ++i) {
            if(session[i].status == Status.ONLINE)
                session[i].sender.offer(data);
            else session[i].buffer.offer(data);
        }
    }
}
