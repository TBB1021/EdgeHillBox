//This is the source code that will act as my http webserver known as EdgeBox
//Below are all the packages used to create the server
import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;
//Declares public class EdgeBox
public class EdgeBox {
    //Creates a Hashmap that will act as the server storage of files
    //The key(String) will store the file name and the value(byte[]) will store the serialised data of file
    //"Final" makes it so that the server can not be inherited or overwritten
    //"Private" makes it so that only this class can access it
    private static final Map<String,byte[]> edgebox = new HashMap<>();

    //The main functions run instantly after file is executed
    //throws ioexception
    public static void main(String[] args) throws IOException {
        //Grabs file from inserted file path and checks to see if it exist
        //if true function "Loadserver" is called with a file paramter returning nothing
        File server= new File("C:\\Users\\TMBak\\IdeaProjects\\EdgeHillBox\\src\\EdgeboxServer.ser");
        if (server.exists()) {
            try {
                Loadserver(server);
            } catch (ClassNotFoundException e) {
            }
        }
        //Creates the server at port 2023
        HttpServer box = HttpServer.create(new InetSocketAddress(2023), 0);
        //creates context at port 2023 with the path "/EdgeBox" and sets up the method "Response"
        box.createContext("/EdgeBox",new Response());
        //sets executor to null and starts the server
        box.setExecutor(null);
        box.start();
    }

    //Method is called if the server file is found
    public static void Loadserver(File server) throws IOException, ClassNotFoundException {
        //uses deserialize the contents of File server
        FileInputStream f = new FileInputStream(server.getAbsoluteFile());
        ObjectInputStream o = new ObjectInputStream(f);
        //Maps the content from file to a temp hashmap which is then transferred to the edgebox hashmap
        Map<String,byte[]> temp = (Map<String, byte[]>) o.readObject();
        edgebox.putAll(temp);

    }


    //Response handles all http request sent from Main.java
    static class Response implements HttpHandler{
        public void handle(HttpExchange link) throws IOException {
            //Retrieves the method, headers and body
            String rmethod = link.getRequestMethod();
            OutputStream output = link.getResponseBody();
            String action = link.getRequestHeaders().getFirst("Target");
            //if method is GET then the programs will do one of many things based on the header "Target"
            if ("GET".equals(rmethod)) {
                System.out.println(action);
                //If start up then the file will send a response code 200 (meaning ok) stating the server is online
                if (link.getRequestHeaders().getFirst("Target").equals("Start up")) {
                    String x = "Welcome to EdgeBox";
                    link.sendResponseHeaders(200, x.length());
                    output.write(x.getBytes());
                    output.close();
                }
                //if View then program will check the server and retrieve and send any files found
                //if files are found the they are appended to a string builder with the phrase "SPACE" separating them which are then sent
                //if server is empty then program will send a response code 204 meaning no content
                else if (link.getRequestHeaders().getFirst("Target").equals("View")) {
                    if (edgebox.isEmpty()) {
                        link.sendResponseHeaders(204, 0);
                    } else if (link.getRequestHeaders().getFirst("Name").equals("*")) {
                        StringBuilder files = new StringBuilder();
                        for (String z : edgebox.keySet()) {
                            files.append(z);
                            files.append("SPACE");
                        }
                        String ffiles = files.toString();
                        link.sendResponseHeaders(200, ffiles.length());
                        output.write(ffiles.getBytes());
                        output.close();
                    }
                }
                //if download then program will retrieve the byte[] associated with the given key (file name)
                //byte[] is then sent
                else if (link.getRequestHeaders().getFirst("Target").equals("Download")){
                    StringBuilder files = new StringBuilder();
                    String fname = link.getRequestHeaders().getFirst("Name");
                    byte[] ffiles= edgebox.get(fname);
                    link.sendResponseHeaders(200, ffiles.length);
                    output.write(ffiles);
                    output.close();
                }
                //If delete then key and value (file name and byte[]) are removed from the server
                //A delete message is sent
                else if (link.getRequestHeaders().getFirst("Target").equals("Delete")) {
                    String fname = link.getRequestHeaders().getFirst("Name");
                    edgebox.remove(fname);
                    String delete = fname+" has been deleted";
                    link.sendResponseHeaders(200, delete.length());
                    output.write(delete.getBytes());
                    output.close();
                }
                //if disconnect then server is serialised and saved to the path shown below
                else if (link.getRequestHeaders().getFirst("Target").equals("Disconnect")){
                    FileOutputStream f = new FileOutputStream("C:\\Users\\TMBak\\IdeaProjects\\EdgeHillBox\\src\\EdgeboxServer.ser");
                    ObjectOutputStream o = new ObjectOutputStream(f);
                    o.writeObject(edgebox);
                    link.sendResponseHeaders(200,0);
                }
            }
            //if method is POST
            else if (rmethod.equals("POST")) {
                //program will retrieve the byte[] from requestbody and file name from header
                //they are then stored into edgebox hashmap
                byte[] requestBody = link.getRequestBody().readAllBytes();
                String filename = link.getRequestHeaders().getFirst("Name");
                link.sendResponseHeaders(201,0);
                edgebox.put(filename,requestBody);
                link.close();
            }
        }


    }
}

