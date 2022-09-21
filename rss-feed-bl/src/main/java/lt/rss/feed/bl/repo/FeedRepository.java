package lt.rss.feed.bl.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import lt.rss.feed.model.FeedEntity;

@NoRepositoryBean
public interface FeedRepository extends JpaRepository<FeedEntity, Long>,
        JpaSpecificationExecutor<FeedEntity> {
}