package lt.rss.feed.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "FEEDS")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants
@Getter
@Setter
public class FeedEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LAST_UPDATE")
    private LocalDateTime lastUpdateDateTime;

    @Column(name = "URL", length = 255)
    private String url;

    @Column(name = "TITLE", length = 45)
    private String title;

    @Column(name = "FEED_NAME", length = 255)
    private String feedName;

    @Builder.Default
    @OneToMany(mappedBy = "feed", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    private List<ItemEntity> items = new ArrayList<>();
}