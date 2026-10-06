import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    
    final static int PORT = 3000;

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("172.16.198.213", PORT);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(s.getInputStream())
        );
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Client ===");
        System.out.println("commandi:");
        System.out.println("\t:exit -> quits the program cleanly");
        System.out.println();

        while (true) {
            System.out.print("Inserisci una stringa: ");
            System.out.flush();
            String inp = sc.nextLine();
            if (inp == null) continue;


            out.println(inp);

            if (inp.equals(":exit")) {
                break;
            }
            String res = in.readLine();
            System.out.println("Risultato: " + res);
        }
        sc.close();
        s.close();
    }
}
