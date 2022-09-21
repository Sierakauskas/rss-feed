package lt.rss.feed.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class Feed {
    private Long id;
    private LocalDateTime lastUpdateDateTime;
    private String url;
    private String title;
    private String feedName;
    private List<Item> items;
}
