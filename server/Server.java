package server;

public class Server {
    public static void main(String[] args) {
        Users.initialize("server\\users.txt");
        Groups.initialize("server\\groups.txt");
        Communicator communicator = new Communicator(8080);
        communicator.execute();
    }
}
