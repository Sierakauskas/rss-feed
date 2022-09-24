package lt.rss.feed.bl.service.exception;

public class FeedBadRequestException extends RuntimeException {
    public FeedBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}