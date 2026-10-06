import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

public class App {
    static ServerSocket ss;
    public static void main(String[] args) throws Exception {
        final int PORT = 8080;

        System.out.println("Server starting...");
        ss = new ServerSocket(PORT);
        System.out.println("listening on: " + PORT);
        Socket cs;
        while (true) {
            cs = ss.accept();
            new Handler(cs).start();
        }
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
        ) {
            String input = in.readLine();
            if (input == null) return;
            String cIP = s.getInetAddress().getHostAddress();

            String userAgent = "Sconosciuto";
            
            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                if (headerLine.startsWith("User-Agent:")) {
                    userAgent = headerLine.substring(12).trim();
                }
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            System.out.printf("[%s] %s -> %s\n", timestamp, cIP, input);
              
            String commonCSS = """
<style>
    body { font-family: monospace; background: #ffffff; color: #111111; margin: 40px; line-height: 1.5; }
    .container { max-width: 600px; border: 1px solid #111111; padding: 20px; }
    h1 { font-size: 1.2rem; font-weight: bold; margin-top: 0; text-transform: uppercase; }
    p { font-size: 0.9rem; }
    .meta { font-size: 0.85rem; color: #555555; border-top: 1px dashed #111111; margin-top: 20px; padding-top: 10px; }
    .error { border-color: #000000; background: #f9f9f9; }
    a { color: #111111; text-decoration: underline; }
</style>
            """;

            if (input.startsWith("GET / HTTP/1.1")) {
                String homeHtml = """
<!DOCTYPE html>
<html>
<head><title>Index</title>%s</head>
<body>
    <div class="container">
        <h1>Server Status: 200 OK</h1>
        <p>Java native socket server responding to HTTP/1.1 requests.</p>
        <div class="meta">
            IP: %s<br>
            UA: %s<br>
            TS: %s
        </div>
    </div>
</body>
</html>
                """.formatted(commonCSS, cIP, userAgent, timestamp);

                sendResponse(out, "200 OK", "text/html", homeHtml);
            } else {
                String errorHtml = """
<!DOCTYPE html>
<html>
<head><title>404</title>%s</head>
<body>
    <div class="container error">
        <h1>Error: 404 Not Found</h1>
        <p>The requested resource could not be found on this server.</p>
        <div class="meta">
            REQ: %s<br>
            <a href="/">Return to index</a>
        </div>
    </div>
</body>
</html>
                """.formatted(commonCSS, input);


                sendResponse(out, "404 Not Found", "text/html", errorHtml);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage() + " : " + Thread.currentThread().getName());
        }
    }

    private void sendResponse(PrintWriter out, String status, String contentType, String content) throws IOException {
        byte[] contentBytes = content.getBytes("UTF-8");
        out.print("HTTP/1.1 " + status + "\r\n");
        out.print("Content-Type: " + contentType + "; charset=UTF-8\r\n");
        out.print("Content-Length: " + contentBytes.length + "\r\n");
        out.print("\r\n");
        out.print(content);
        out.flush();
    }
}
