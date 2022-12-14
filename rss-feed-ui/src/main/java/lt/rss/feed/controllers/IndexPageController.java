package lt.rss.feed.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lt.rss.feed.bl.service.FeedService;
import lt.rss.feed.model.dto.Feed;

@Controller
@RequiredArgsConstructor
public class IndexPageController {

    private final FeedService service;

    @RequestMapping
    public String mainPage(Model md) {
        return mainPageAttributes(md, new Feed());
    }

    @PostMapping("/create")
    public String saveForm(Model md,
            @ModelAttribute("feeds") Feed feed) {
        if (!StringUtils.isNoneBlank(feed.getUrl(), feed.getFeedName())) {
            return mainPageAttributes(md, feed);
        }
        service.createOrUpdate(feed);
        return mainPageAttributes(md, new Feed());
    }

    @RequestMapping("/view")
    public String editImage(Model md, @RequestParam(name = "id") Long id) {
        md.addAttribute("feed", service.getFeedViewItem(id));
        return "viewPage";
    }

    private String mainPageAttributes(Model md, Feed feed) {
        md.addAttribute("service", service);
        md.addAttribute("feed", feed);
        return "mainPage";
    }
}