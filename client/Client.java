package client;

public class Client {
    public static void main(String[] args) {
        Communicatee communicatee = new Communicatee("127.0.0.1", 8080);
        communicatee.execute();
    }
}
