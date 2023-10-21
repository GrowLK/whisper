package client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.Date;

import common.Data;
import common.Data.Type;
import common.Block;

public class Input extends Thread {
	OutputStream output;
    Signal signal;
    Scanner scanner;
    public Input(OutputStream output_, Signal signal_) {
    	output = output_;
        signal = signal_;
        scanner = new Scanner(System.in);
    }
    public void run() {
        try {
            while(!signal.exiting) {
                String buf = scanner.nextLine();
                if(!buf.startsWith(".")) send(buf);
                else if(buf.equals(".login")) login();
                else if(buf.equals(".connect")) connect();
                else if(buf.equals(".disconnect")) disconnect();
                else if(buf.equals(".logout")) logout();
                else if(buf.equals(".exit")) exit();
            }
            scanner.close();
        } catch(IOException exception) {  }
    }
    
    private void login() throws IOException {
        while(signal.account == null) {
        	String name = "";
        	while(name.equals("")) {
	            System.out.printf("Username: ");
	            name = scanner.nextLine();
        	}
        	String password = "";
        	while(password.equals("")) {
	            System.out.printf("Password: ");
	            password = scanner.nextLine();
        	}
            
            Type type = Type.COMMAND;
            Date date = new Date(System.currentTimeMillis());
        	String source = "LOGIN";
        	String destination = "SYSTEM";
            byte[] content = (name+"&"+password).getBytes();
            Data data = new Data(type, date, source, destination, content);
            output.write(data.getBytes());
            
            signal.update();
        }
    }
    
    private void connect() throws IOException {
        while(signal.current == null) {
        	String kind = "";
        	while(kind.equals("")) {
	            System.out.printf("Kind: ");
	            kind = scanner.nextLine();
        	}
        	String name = "";
        	while(name.equals("")) {
	            System.out.printf("Name: ");
	            name = scanner.nextLine();
        	}
        	
        	Type type = Type.COMMAND;
            Date date = new Date(System.currentTimeMillis());
        	String source = "CONNECT";
        	String destination = "SYSTEM";
            byte[] content = (kind+"&"+name).getBytes();
            Data data = new Data(type, date, source, destination, content);
            output.write(data.getBytes());
            
            signal.update();
        }
    }
    
    private void disconnect() throws IOException {
    	while(signal.current != null) {
        	Type type = Type.COMMAND;
            Date date = new Date(System.currentTimeMillis());
        	String source = "DISCONNECT";
        	String destination = "SYSTEM";
            byte[] content = ("").getBytes();
            Data data = new Data(type, date, source, destination, content);
            output.write(data.getBytes());
            
            signal.update();
        }
    }
    
    private void logout() throws IOException {
    	while(signal.current != null) {
    		Type type = Type.COMMAND;
            Date date = new Date(System.currentTimeMillis());
        	String source = "LOGOUT";
        	String destination = "SYSTEM";
            byte[] content = ("").getBytes();
            Data data = new Data(type, date, source, destination, content);
            output.write(data.getBytes());
            
            signal.update();
    	}
    }
    
    private void exit() throws IOException {
    	while(!signal.exiting) {
    		Type type = Type.COMMAND;
            Date date = new Date(System.currentTimeMillis());
        	String source = "EXIT";
        	String destination = "SYSTEM";
            byte[] content = ("").getBytes();
            Data data = new Data(type, date, source, destination, content);
            output.write(data.getBytes());
            
            signal.update();
    	}
    }
    
    private void send(String buf) throws IOException {
    	if(signal.account == null) {
    		System.out.println("not connected");
    		return;
    	}
    	if(signal.current == null) {
    		System.out.println("not logged in");
    		return;
    	}
    	
    	Type type = Type.TEXT;
    	Date date = new Date(System.currentTimeMillis());
    	String source = "f-"+signal.account.name;
    	String destination = signal.current;
    	
    	int size = 4096-12-source.getBytes().length-destination.getBytes().length;
    	Block buffer = new Block(buf.getBytes(), size);
    	
    	byte[] content = null;
    	while((content=buffer.push()) != null) {
	        Data data = new Data(type, date, source, destination, content);
	        output.write(data.getBytes());
    	}
    }
}
