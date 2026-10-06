import java.net.*;
import java.io.*;

public class App {
    static ServerSocket ss;
    public static void main(String[] args) throws Exception {
        System.out.println("Server starting...");
        ss = new ServerSocket(8080);
        System.out.println("listening on: " + 8080);
        Socket cs;
        while ((cs = ss.accept()) != null) {
            new Handler(cs).start();
        }
        ss.close();
    }
}


class Handler extends Thread {
    Socket s;

    public Handler(Socket s) {
        super();
        this.s = s;
    }

    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            Socket autoCloseSocket = s 
        ) {
            String input = in.readLine();
            
            if (input != null) {
                System.out.println("[REQUEST] " + input);
                
                if (input.startsWith("GET / HTTP/1.1")) {
                    String home = """
<html><body><h1>Home page</h1><p>this is my cool website!</p></body></html>""";
                    
                    out.print("HTTP/1.1 200 OK\r\n");
                    out.print("Content-Type: text/html; charset=UTF-8\r\n");
                    out.print("Content-Length: " + home.getBytes().length + "\r\n"); 
                    out.print("\r\n");
                    out.print(home);
                    out.flush(); 
                } else {
                    String page404 = """
<html><body><h1>Page not found</h1><p>this page does not exist! go to '/'</p></body></html>""";
                    
                    out.print("HTTP/1.1 404 Not Found\r\n");
                    out.print("Content-Type: text/html; charset=UTF-8\r\n");
                    out.print("Content-Length: " + page404.getBytes().length + "\r\n");
                    out.print("\r\n");
                    out.print(page404);
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage() + " : " + Thread.currentThread().getName());
        }
    }
}
