package lt.rss.feed.bl.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lt.rss.feed.bl.repo.FeedRepository;
import lt.rss.feed.model.dto.Feed;

@Service("feedService")
@Transactional
@RequiredArgsConstructor
public class FeedService {

    private static final int MAX_RESULT_LIMIT_FOR_FIND_ALL_SEARCH = 20;

    private final FeedRepository feedRepository;

    public Long create(Feed feed) {
        return null;
    }
}