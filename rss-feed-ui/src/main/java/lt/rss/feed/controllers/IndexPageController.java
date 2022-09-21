package lt.rss.feed.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lt.rss.feed.bl.service.FeedService;

@Controller
@RequiredArgsConstructor
public class IndexPageController {

    private final FeedService service;

    @RequestMapping("/")
    public String Welcome(Model md) {
        md.addAttribute("service", service);
        return "mainPage";
    }

    // @RequestMapping("/login")
    // public String login() {
    //     return "login";
    // }
    //
    // @PostMapping("/saveImage")
    // public String addImage(Model md, @RequestParam(value = "file", required = false) @RequestBody MultipartFile file) {
    //     md.addAttribute("service", imageService);
    //     String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    //     String fileType = file.getContentType();
    //     if (!fileType.startsWith("image/") || file.getSize() < 5) {
    //         md.addAttribute("wrongType", "Sorry, not an image File");
    //         md.addAttribute("users", imageService.getfive());
    //         return "homePageTemplate";
    //     }
    //     fileType = fileType.replace("image/", ".");
    //     try {
    //         // Check if the file's name contains invalid characters
    //         if (fileName.contains("..")) {
    //             throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
    //         }
    //         byte[] data = file.getBytes();
    //         long fileSizeinKb = file.getSize();
    //         fileSizeinKb /= 1024;
    //         String fileSize = Objects.toString(fileSizeinKb, null);
    //         fileSize += " Kb";
    //         ImageEntity imageEntity = imageService.storeFile(data, fileName, fileType, fileSize);
    //         md.addAttribute("msg", "Succesfully uploaded file: " + file.getOriginalFilename());
    //         md.addAttribute("entity", imageEntity);
    //         return "editImagePage";
    //
    //     } catch (IOException ex) {
    //         throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
    //     }
    // }
    //
    // @RequestMapping(value = "/imageDisplay", method = RequestMethod.GET)
    // public void showImage(@RequestParam("id") Long imageId, HttpServletResponse response, HttpServletRequest request)
    //         throws ServletException {
    //     ImageEntity item = imageService.getImageById(imageId);
    //     response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
    //
    //     try (OutputStream os = response.getOutputStream()) {
    //         os.write(item.getImage());
    //     } catch (IOException e) {
    //         log.error(e);
    //     }
    // }
    //
    // @RequestMapping(value = "/smallimageDisplay", method = RequestMethod.GET)
    // public void showSmallImage(@RequestParam("id") Long imageId, HttpServletResponse response, HttpServletRequest request) {
    //     ImageEntity item = imageService.getImageById(imageId);
    //     InputStream is = new ByteArrayInputStream(item.getImage());
    //     response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
    //     try {
    //         BufferedImage srcImage = ImageIO.read(is);
    //         try {
    //             BufferedImage scaledImage = Scalr.resize(srcImage, 150);
    //             ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //             ImageIO.write(scaledImage, "jpg", baos);
    //             baos.flush();
    //             byte[] imageInByte = baos.toByteArray();
    //             baos.close();
    //             OutputStream os = response.getOutputStream();
    //             os.write(imageInByte);
    //             os.close();
    //         } catch (IllegalArgumentException e) {
    //             log.error(e);
    //         }
    //     } catch (IOException e) {
    //         log.error(e);
    //
    //     }
    // }
    //
    // @RequestMapping("/results/search={searchtext}")
    // public String searchImage(Model md,
    //         @RequestParam(name = "searchtext", defaultValue = "") @PathVariable("searchtext") String searchtext,
    //         @RequestParam(value = "optradio", defaultValue = "ID")
    //                 String optradio) {
    //     md.addAttribute("lastchecked", optradio);
    //     md.addAttribute("service", imageService);
    //     if (searchtext.equals("")) {
    //         md.addAttribute("searchresult", "Showing: All");
    //         md.addAttribute("users", imageService.getAll());
    //         md.addAttribute("showsearch", "Show All");
    //         return "showSearchImages";
    //     } else if (optradio.equals("Tag")) {
    //         md.addAttribute("users", imageService.getByTagsName(searchtext));
    //         if (imageService.getByTagsName(searchtext).isEmpty()) {
    //             md.addAttribute("emptytext", "Nothing interesting found. Try again.");
    //         }
    //     } else {
    //         md.addAttribute("users", imageService.getrequired(searchtext, optradio));
    //         if (imageService.getrequired(searchtext, optradio).isEmpty()) {
    //             md.addAttribute("emptytext", "Nothing interesting found. Try again.");
    //         }
    //     }
    //     md.addAttribute("showsearch", searchtext);
    //     md.addAttribute("searchresult", "Showing by " + optradio + ": " + searchtext);
    //     return "showSearchImages";
    // }

}