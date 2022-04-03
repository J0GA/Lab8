import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class CrawlerTask implements Runnable {
    UrlPool pool;
    static final String HREF_TAG = "<a href=\"http";

    public CrawlerTask(UrlPool pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        while (true) {
            UrlDepthPair pair = pool.getNextPair();
            int currDepth = pair.getDepth();
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress(pair.getHost(), 80), 3000);
                sock.setSoTimeout(3000);
                System.out.println("Connected to " + pair.getURLString());
                PrintWriter out =
                        new PrintWriter(sock.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                out.println("GET " + pair.getPath() + " HTTP/1.1");
                out.println("Host: " + pair.getHost());
                out.println("Connection: close");
                out.println();
                out.flush();

                String line;
                int lineLength;
                int shiftIdx;
                while ((line = in.readLine()) != null) {
                    boolean foundFullLink = false;
                    int idx = line.indexOf(HREF_TAG);
                    if (idx > 0) {
                        StringBuilder sb = new StringBuilder();
                        shiftIdx = idx + 9;
                        char c = line.charAt(shiftIdx);
                        lineLength = line.length();
                        while (c != '"' && shiftIdx < lineLength - 1) {
                            sb.append(c);
                            shiftIdx++;
                            c = line.charAt(shiftIdx);
                            if (c == '"') {
                                foundFullLink = true;
                            }
                        }
                        String newUrl = sb.toString();
                        if (foundFullLink) {
                            UrlDepthPair newPair =
                                    new UrlDepthPair(newUrl, currDepth + 1);
                            pool.addPair(newPair);
                        }
                    }
                }
                sock.close();
            }
            catch (IOException e) {
            }

        }
    }
}