package lt.rss.feed.bl.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static lt.rss.feed.bl.service.FeedService.ITEM_LIST_ELEMENT_COUNT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.net.MediaType;

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

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    private static final Long ID = 100L;
    private static final LocalDateTime LAST_UPDATE_DATE_TIME = LocalDateTime.of(2022, 9, 26, 11, 11);
    private static final String LAST_UPDATE_DATE_TIME_STRING_VALUE = "2022-09-26 11:11";
    private static final String FEED_NAME = "feed-localhost";
    private static final String URL = "http://localhost:8080/rss";
    private static final String TITLE = "Test feed title";
    private static final Long ITEM_COUNT = 2L;

    private static final LocalDateTime FIRST_ITEM_PUBLISHED_DATE_TIME = LocalDateTime.of(2022, 9, 26, 10, 1);
    private static final String FIRST_ITEM_TITLE = "First feed item title";
    private static final String FIRST_ITEM_LINK = "http://localhost:8080/first-item";
    private static final String FIRST_ITEM_DESCRIPTION = "First item description";

    private static final LocalDateTime SECOND_ITEM_PUBLISHED_DATE_TIME = LocalDateTime.of(2022, 9, 26, 9, 56);
    private static final String SECOND_ITEM_TITLE = "Second feed item title";
    private static final String SECOND_ITEM_LINK = "http://localhost:8080/second-item";
    private static final String SECOND_ITEM_DESCRIPTION = "Second item description";

    private static final String FEED_XML_PATH = "/rssFeed.xml";

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private ItemRepository itemRepository;

    private FeedService service;

    @BeforeEach
    void setUp() {
        service = new FeedService(feedRepository, itemRepository);
    }

    @Test
    void saveFeedShouldThrowException() {
        Feed feed = Feed.builder()
                .url("Malformed Url")
                .build();

        assertThrows(FeedBadRequestException.class, () -> service.createOrUpdate(feed));
    }

    @Test
    void saveFeedShouldSaveAndReturnId() {
        when(feedRepository.findByFeedNameAndUrl(FEED_NAME, URL))
                .thenReturn(Optional.empty());

        FeedEntity mockedFeedEntity = mock(FeedEntity.class);
        when(mockedFeedEntity.getId())
                .thenReturn(ID);
        when(feedRepository.save(argThat(matchesFeed())))
                .thenReturn(mockedFeedEntity);

        Matcher<Iterable<? extends ItemEntity>> itemsMatcher = contains(
                Arrays.asList(matchesFirstItem(), matchesSecondItem()));
        when(itemRepository.saveAll(argThat(itemsMatcher)))
                .thenReturn(Collections.emptyList());

        WireMockServer wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        stubFor(WireMock.get(urlEqualTo("/rss"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.XHTML_UTF_8.type())
                        .withBody(readXmlFile())));

        Feed feed = Feed.builder()
                .url(URL)
                .feedName(FEED_NAME)
                .build();

        assertThat(service.createOrUpdate(feed), is(ID));
        wireMockServer.shutdown();
    }

    @Test
    void getFeedListItemsShouldReturnListItem() {
        FeedEntity mockedFeedEntity = mock(FeedEntity.class);
        when(mockedFeedEntity.getId())
                .thenReturn(ID);
        when(mockedFeedEntity.getFeedName())
                .thenReturn(FEED_NAME);
        when(feedRepository.findAll())
                .thenReturn(Collections.singletonList(mockedFeedEntity));

        assertThat(service.getFeedListItems(), contains(allOf(
                hasProperty(FeedListItem.Fields.id, is(ID)),
                hasProperty(FeedListItem.Fields.feedName, is(FEED_NAME))
        )));
    }

    @Test
    void getFeedViewItemShouldReturnViewItem() {
        FeedEntity mockedFeedEntity = mock(FeedEntity.class);
        when(mockedFeedEntity.getId())
                .thenReturn(ID);
        when(mockedFeedEntity.getLastUpdateDateTime())
                .thenReturn(LAST_UPDATE_DATE_TIME);
        when(mockedFeedEntity.getUrl())
                .thenReturn(URL);
        when(mockedFeedEntity.getTitle())
                .thenReturn(TITLE);
        when(mockedFeedEntity.getFeedName())
                .thenReturn(FEED_NAME);
        when(feedRepository.findById(ID))
                .thenReturn(Optional.of(mockedFeedEntity));

        when(itemRepository.countAllByFeedId(ID))
                .thenReturn(ITEM_COUNT);

        ItemEntity firstItemEntity = mock(ItemEntity.class);
        when(firstItemEntity.getTitle())
                .thenReturn(FIRST_ITEM_TITLE);
        when(firstItemEntity.getLink())
                .thenReturn(FIRST_ITEM_LINK);
        ItemEntity secondItemEntity = mock(ItemEntity.class);
        when(secondItemEntity.getTitle())
                .thenReturn(SECOND_ITEM_TITLE);
        when(secondItemEntity.getLink())
                .thenReturn(SECOND_ITEM_LINK);

        PageRequest itemPageRequest = PageRequest.of(0, ITEM_LIST_ELEMENT_COUNT, Sort.by(Sort.Direction.DESC, ItemEntity.Fields.publishedDateTime));
        when(itemRepository.findAllByFeedId(ID, itemPageRequest))
                .thenReturn(Arrays.asList(firstItemEntity, secondItemEntity));

        assertThat(service.getFeedViewItem(ID), is(
                allOf(
                        hasProperty(FeedViewItem.Fields.id, is(ID)),
                        hasProperty(FeedViewItem.Fields.lastUpdateDateTime, is(LAST_UPDATE_DATE_TIME_STRING_VALUE)),
                        hasProperty(FeedViewItem.Fields.url, is(URL)),
                        hasProperty(FeedViewItem.Fields.title, is(TITLE)),
                        hasProperty(FeedViewItem.Fields.feedName, is(FEED_NAME)),
                        hasProperty(FeedViewItem.Fields.itemCount, is(ITEM_COUNT)),
                        hasProperty(FeedViewItem.Fields.items, contains(
                                allOf(
                                        hasProperty(ItemListItem.Fields.title, is(FIRST_ITEM_TITLE)),
                                        hasProperty(ItemListItem.Fields.link, is(FIRST_ITEM_LINK))
                                ),
                                allOf(
                                        hasProperty(ItemListItem.Fields.title, is(SECOND_ITEM_TITLE)),
                                        hasProperty(ItemListItem.Fields.link, is(SECOND_ITEM_LINK))
                                )
                        ))
                )
        ));
    }

    @Test
    void getFeedViewItemShouldThrowException() {
        when(feedRepository.findById(ID))
                .thenReturn(Optional.empty());

        assertThrows(FeedNotFoundException.class, () -> service.getFeedViewItem(ID));
    }

    private Matcher<FeedEntity> matchesFeed() {
        return allOf(
                hasProperty(FeedEntity.Fields.url, is(URL)),
                hasProperty(FeedEntity.Fields.title, is(TITLE)),
                hasProperty(FeedEntity.Fields.feedName, is(FEED_NAME))
        );
    }

    private Matcher<ItemEntity> matchesFirstItem() {
        return allOf(
                hasProperty(ItemEntity.Fields.publishedDateTime, is(FIRST_ITEM_PUBLISHED_DATE_TIME)),
                hasProperty(ItemEntity.Fields.title, is(FIRST_ITEM_TITLE)),
                hasProperty(ItemEntity.Fields.link, is(FIRST_ITEM_LINK)),
                hasProperty(ItemEntity.Fields.description, is(FIRST_ITEM_DESCRIPTION))
        );
    }

    private Matcher<ItemEntity> matchesSecondItem() {
        return allOf(
                hasProperty(ItemEntity.Fields.publishedDateTime, is(SECOND_ITEM_PUBLISHED_DATE_TIME)),
                hasProperty(ItemEntity.Fields.title, is(SECOND_ITEM_TITLE)),
                hasProperty(ItemEntity.Fields.link, is(SECOND_ITEM_LINK)),
                hasProperty(ItemEntity.Fields.description, is(SECOND_ITEM_DESCRIPTION))
        );
    }

    private byte[] readXmlFile() {
        try (InputStream is = new ClassPathResource(FEED_XML_PATH).getInputStream()) {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading file data", e);
        }
    }
}
