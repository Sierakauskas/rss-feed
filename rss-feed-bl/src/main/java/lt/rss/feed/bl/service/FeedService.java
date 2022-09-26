package lt.rss.feed.bl.service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import lt.rss.feed.bl.repo.FeedRepository;
import lt.rss.feed.bl.repo.ItemRepository;
import lt.rss.feed.bl.service.exception.FeedBadRequestException;
import lt.rss.feed.bl.service.exception.FeedNotFoundException;
import lt.rss.feed.model.FeedEntity;
import lt.rss.feed.model.ItemEntity;
import lt.rss.feed.model.dto.Feed;
import lt.rss.feed.model.dto.FeedListItem;
import lt.rss.feed.model.dto.FeedViewItem;
import lt.rss.feed.model.dto.ItemListItem;

@Service
@CommonsLog
@Transactional
@RequiredArgsConstructor
public class FeedService {

    public static final int ITEM_LIST_ELEMENT_COUNT = 5;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FeedRepository feedRepository;
    private final ItemRepository itemRepository;

    public Long createOrUpdate(Feed feedDto) {
        try {
            URL feedSource = new URL(feedDto.getUrl());
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed syndFeed = input.build(new XmlReader(feedSource));
            return saveFeed(syndFeed, feedDto.getFeedName());
        } catch (Exception e) {
            throw new FeedBadRequestException("Error saving feed", e);
        }
    }

    public List<FeedListItem> getFeedListItems() {
        return feedRepository.findAll().stream()
                .map(feed -> FeedListItem.builder()
                        .id(feed.getId())
                        .feedName(feed.getFeedName())
                        .build())
                .collect(Collectors.toList());
    }

    public FeedViewItem getFeedViewItem(Long id) {
        FeedEntity entity = feedRepository.findById(id)
                .orElseThrow(() -> new FeedNotFoundException("Feed was not found with id " + id));

        Long itemCount = itemRepository.countAllByFeedId(entity.getId());

        PageRequest itemPageRequest = PageRequest.of(0, ITEM_LIST_ELEMENT_COUNT, Sort.by(Sort.Direction.DESC, ItemEntity.Fields.publishedDateTime));
        List<ItemListItem> items = itemRepository.findAllByFeedId(entity.getId(), itemPageRequest).stream()
                .map(itemEntity -> ItemListItem.builder()
                        .title(itemEntity.getTitle())
                        .link(itemEntity.getLink())
                        .build())
                .collect(Collectors.toList());

        return FeedViewItem.builder()
                .id(entity.getId())
                .lastUpdateDateTime(formatLocalDateTime(entity.getLastUpdateDateTime()))
                .url(entity.getUrl())
                .title(entity.getTitle())
                .feedName(entity.getFeedName())
                .itemCount(itemCount)
                .items(items)
                .build();
    }

    private Long saveFeed(SyndFeed syndFeed, String feedName) {
        FeedEntity feedEntity = feedRepository.findByFeedNameAndUrl(feedName, syndFeed.getLink())
                .orElse(new FeedEntity());

        feedEntity = feedRepository.save(feedEntity.toBuilder()
                .lastUpdateDateTime(LocalDateTime.now())
                .url(syndFeed.getLink())
                .title(syndFeed.getTitle())
                .feedName(feedName)
                .build());

        if (CollectionUtils.isNotEmpty(syndFeed.getEntries())) {
            List<ItemEntity> itemEntities = new ArrayList<>();
            for (Object entry : syndFeed.getEntries()) {
                itemEntities.add(buildItemEntity((SyndEntry) entry, feedEntity));
            }
            itemRepository.saveAll(itemEntities);
        }
        return feedEntity.getId();
    }

    private ItemEntity buildItemEntity(SyndEntry syndEntry, FeedEntity feed) {
        return ItemEntity.builder()
                .feed(feed)
                .title(syndEntry.getTitle())
                .link(syndEntry.getLink())
                .description(syndEntry.getDescription().getValue())
                .publishedDateTime(convertToLocalDateTime(syndEntry.getPublishedDate()))
                .build();
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}