package lt.rss.feed.model.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@Builder
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedViewItem {
    private Long id;
    private String lastUpdateDateTime;
    private String url;
    private String title;
    private String feedName;
    private Long itemCount;
    private List<ItemListItem> items;
}
