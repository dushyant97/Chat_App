import javax.json.Json;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;

public class Peer {
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Name and (localhost or ip address ) and port number for this peer " +
                                                                                "(all separated by space)");
        String[] setupvalues = bufferedReader.readLine().split(" ");
        ServerThread serverThread = new ServerThread(setupvalues[2]);
        serverThread.start();
        String username = setupvalues[0] + " : " +setupvalues[1];
        new Peer().updateListenToPeers(bufferedReader , username, serverThread);    //instead of username you can
                                                                                     // also pass setupvalues[0]
    }

    public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread) throws Exception{
        System.out.println("Enter localhost/IP_Address:port_number (separated by spaces)");
        System.out.println("of peers to receive message from (s to skip)");
        String input = bufferedReader.readLine();
        String[] inputValues = input.split(" ");

        if(!input.equals("s")){
            for (String inputValue : inputValues) {
                String[] address = inputValue.split(":");
                Socket socket = null;
                try {
                    socket = new Socket(address[0], Integer.parseInt(address[1]));
                    new PeerThread(socket).start();
                } catch (Exception e) {
                    if (socket != null) {
                        socket.close();
                    } else {
                        System.out.println("Invalid input.. skipping to next step");
                        System.out.println("Exception is :"+ e);
                    }
                }
            }
        }
        communicate(bufferedReader, username, serverThread);
    }

    public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread){
        try{
            System.out.println("you can communicate.. (press e to exit or c to add more peers)");
            boolean flag = true;
            while(flag){
                String message = bufferedReader.readLine();
                if(message.equals("e")) {
                    flag = false;
                }else if(message.equals("c")) {
                    updateListenToPeers(bufferedReader, username, serverThread);
                }else{
                    StringWriter stringWriter = new StringWriter();
                    Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                                                    .add("username",username)
                                                    .add("message", message)
                                                    .build());
                    serverThread.sendMessage(stringWriter.toString());
                }
            }
            System.exit(0);
        }catch(Exception e){
            System.out.println("Exception raised while communicating: " + e);
        }
    }
}
