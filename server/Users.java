package server;

import java.io.IOException;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.Queue;

import common.Data;

public class Users {
    private static Map<Integer, User> idmap = new HashMap<Integer, User>();
    private static Map<String, User> namemap = new HashMap<String, User>();
    public static void initialize(String file) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(file));
            String string = null;
            while((string=buffer.readLine()) != null) {
                User user = User.fromString(string.substring(5));
                idmap.put(user.identity, user);
                namemap.put(user.name, user);
            }
            buffer.close();
        } catch(IOException exception) { exception.printStackTrace(); }
    }
    public static User verify(String name, String password) {
        User user = namemap.get(name);
        if(user!=null && user.password.equals(password))
            return user;
        return null;
    }
    public static User findByIdentity(int identity) {
        return idmap.get(identity);
    }
    public static User findByName(String name) {
        return namemap.get(name);
    }
}

class User {
    int identity = 0;
    String name = null;
    String password = null;
    int[] users = null;
    int[] groups = null;
    enum Status { OFFLINE, ONLINE }
    Status status = Status.OFFLINE;
    Receiver receiver = null;
    Sender sender = null;

    Queue<Data> buffer = new LinkedList<Data>();

    public static User fromString(String string) {
        User user = new User();
        String[] element = string.split(",");
        user.identity = Integer.parseInt(element[0]);
        user.name = element[1];
        user.password = element[2];
        String[] friends = element[3].split(" |\\[|\\]");
        if(friends.length != 0) {
            user.users = new int[friends.length-1];
            for(int i=0; i<friends.length-1; ++i) {
                user.users[i] = Integer.parseInt(friends[i+1]);
            }
        }
        String[] groups = element[4].split(" |\\[|\\]");
        if(groups.length != 0) {
            user.groups = new int[groups.length-1];
            for(int i=0; i<groups.length-1; ++i)
                user.groups[i] = Integer.parseInt(groups[i+1]);
        }
        return user;
    }
    public static String toString(User user) {
        String string = "user:"+user.identity+","+user.name+","+user.password+",[";
        if(user.users != null) {
            int i = 0;
            for(; i<user.users.length-1; ++i)
                string += user.users[i]+" ";
            string += user.users[i]+"],";
        } else string += "],";
        if(user.groups != null) {
            int i = 0;
            for(; i<user.groups.length-1; ++i)
                string += user.groups[i]+" ";
            string += user.groups[i]+"]";
        } else string += "]";
        return string;
    }
}