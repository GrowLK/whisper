package client;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import common.Data;

public class Account {
	public String name;
	public String password;
	public Map<String, Information> list;
	public Account(String name_, String password_) {
		name = name_;
		password = password_;
		list = new HashMap<String, Information>();
	}
}

class Information {
	public String name;
	public Queue<Data> buffer;
	public Information(String name_) {
		name = name_;
		buffer = new LinkedList<Data>();
	}
}

class Friend extends Information {
	public Friend(String name_) {
		super(name_);
	}
}

class Team extends Information {
	public Team(String name_) {
		super(name_);
	}
}
