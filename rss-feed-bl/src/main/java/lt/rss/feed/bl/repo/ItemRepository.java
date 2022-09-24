package lt.rss.feed.bl.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.rss.feed.model.ItemEntity;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    Long countAllByFeedId(Long feedId);
    List<ItemEntity> findAllByFeedId(Long feedId, Pageable pageable);
}