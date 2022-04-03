import java.net.MalformedURLException;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        if (args.length != 3) {
            System.out.println("usage: java Crawler <URL> <maximum_depth> <num_threads>");
            return;
        }
        String startURL = args[0];
        int maxDepth = Integer.parseInt(args[1]);
        int numThreads = Integer.parseInt(args[2]);

        UrlPool pool = new UrlPool(maxDepth);
        UrlDepthPair firstPair = new UrlDepthPair(startURL, 0);
        pool.addPair(firstPair);

        for (int i = 0; i < numThreads; i++) {
            CrawlerTask c = new CrawlerTask(pool);
            Thread t = new Thread(c);
            t.start();
        }
        while (pool.getWaitCount() != numThreads) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                System.out.println("Caught unexpected " +
                        "InterruptedException, ignoring...");
            }
        }
        LinkedList<UrlDepthPair> foundUrls = pool.getSeenUrls();
        for (UrlDepthPair pair : foundUrls) {
            System.out.println(pair.toString());
        }
        System.exit(0);
    }
}