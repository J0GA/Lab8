import java.util.HashSet;
import java.util.LinkedList;

public class UrlPool {
    LinkedList<UrlDepthPair> urlsToCrawl;
    LinkedList<UrlDepthPair> urlsSeen;
    int maxDepth;
    int waitCount;
    HashSet<String> urlsSeenSet;

    public UrlPool(int maxDepth) {
        this.maxDepth = maxDepth;
        urlsToCrawl = new LinkedList<UrlDepthPair>();
        urlsSeen = new LinkedList<UrlDepthPair>();
        waitCount = 0;
        urlsSeenSet = new HashSet<String>();
    }

    public synchronized UrlDepthPair getNextPair() {
        while (urlsToCrawl.size() == 0) {
            try {
                waitCount++;
                wait();
                waitCount--;
            } catch (InterruptedException e) {
                System.out.println("Caught unexpected " +
                        "InterruptedException, ignoring...");
            }
        }
        UrlDepthPair nextPair = urlsToCrawl.removeFirst();
        return nextPair;
    }

    public synchronized void addPair(UrlDepthPair pair) {
        if (urlsSeenSet.contains(pair.getURLString())) {
            return;
        }
        urlsSeen.add(pair);
        if (pair.getDepth() < maxDepth) {
            urlsToCrawl.add(pair);
            notify();
        }
        urlsSeenSet.add(pair.getURLString());
    }

    public synchronized int getWaitCount() {
        return waitCount;
    }

    public LinkedList<UrlDepthPair> getSeenUrls() {
        return urlsSeen;
    }
}