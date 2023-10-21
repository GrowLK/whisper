package server;

import java.io.IOException;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;

public class Groups {
    private static Map<Integer, Group> idmap = new HashMap<Integer, Group>();
    private static Map<String, Group> namemap = new HashMap<String, Group>();
    public static void initialize(String file) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(file));
            String string = null;
            while((string=buffer.readLine()) != null) {
                Group group = Group.fromString(string.substring(6));
                idmap.put(group.identity, group);
                namemap.put(group.name, group);
            }
            buffer.close();
        } catch(IOException exception) { exception.printStackTrace(); }
    }
    public static Group findByIdentity(int identity) {
        return idmap.get(identity);
    }
    public static Group findByName(String name) {
        return namemap.get(name);
    }
}

class Group {
    int identity = 0;
    String name = null;
    int[] members = null;
    public static Group fromString(String string) {
        Group group = new Group();
        String[] element = string.split(",");
        group.identity = Integer.parseInt(element[0]);
        group.name = element[1];
        String[] members = element[2].split(" |\\[|\\]");
        if(members.length != 0) {
            group.members = new int[members.length-1];
            for(int i=0; i<members.length-1; ++i)
                group.members[i] = Integer.parseInt(members[i+1]);
        }
        return group;
    }
    public static String toString(Group group) {
        String string = "group:"+group.identity+","+group.name+",[";
        if(group.members != null) {
            int i = 0;
            for(; i<group.members.length-1; ++i)
                string += group.members[i]+" ";
            string += group.members[i]+"]";
        } else string += "]";
        return string;
    }
}