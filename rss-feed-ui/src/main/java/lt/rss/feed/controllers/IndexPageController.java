package lt.rss.feed.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lt.rss.feed.bl.service.FeedService;
import lt.rss.feed.model.dto.Feed;

@Controller
@RequiredArgsConstructor
public class IndexPageController {

    private final FeedService service;

    @RequestMapping("/")
    public String mainPage(Model md) {
        md.addAttribute("service", service);
        md.addAttribute("feed", new Feed());
        return "mainPage";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String saveForm(Model model,
            @ModelAttribute("feeds") Feed feed,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (!StringUtils.isNoneBlank(feed.getUrl(), feed.getFeedName())) {
            model.addAttribute("service", service);
            model.addAttribute("feed", feed);
            return "mainPage";
        }
        service.createOrUpdate(feed);
        model.addAttribute("service", service);
        model.addAttribute("feed", new Feed());
        return "mainPage";
    }

    @RequestMapping("/view")
    public String editImage(Model md, @RequestParam(name = "id") Long id) {
        md.addAttribute("feed", service.getFeedViewItem(id));
        return "viewPage";
    }
}