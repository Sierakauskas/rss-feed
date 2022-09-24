package lt.rss.feed.bl.service.exception;

public class FeedNotFoundException extends RuntimeException {
    public FeedNotFoundException(String message) {
        super(message);
    }
}