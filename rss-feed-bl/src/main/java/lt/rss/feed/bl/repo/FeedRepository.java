package lt.rss.feed.bl.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.rss.feed.model.FeedEntity;

@Repository
public interface FeedRepository extends JpaRepository<FeedEntity, Long> {
    Optional<FeedEntity> findByFeedNameAndUrl(String feedName, String url);
}