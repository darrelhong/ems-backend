package com.is4103.backend.controller;

import com.is4103.backend.service.EventService;
import com.is4103.backend.service.FileStorageService;
import com.is4103.backend.service.UserService;
import com.is4103.backend.dto.FileStorageProperties;
import com.is4103.backend.dto.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.is4103.backend.model.User;
import com.is4103.backend.model.Event;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    // @PostMapping("/uploadFile")
    @PostMapping("/uploadProfilePicFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file, "profilepic", "");

        // @PostMapping("/uploadFile")
        // public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile
        // file) {
        // String fileName = fileStorageService.storeFile(file);

        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        long userId = user.getId();

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                .path(fileName).toUriString();

        if (user.getProfilePic() != null) {
            String profilepicpath = user.getProfilePic();
            String oldpicfilename = profilepicpath.substring(profilepicpath.lastIndexOf("/") + 1);

            System.out.println(oldpicfilename);
            Path oldFilepath = Paths.get(this.fileStorageProperties.getUploadDir() + "/profilePics/" + oldpicfilename)
                    .toAbsolutePath().normalize();
            System.out.println(oldFilepath);
            try {
                Files.deleteIfExists(oldFilepath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

       //  user = userService.updateProfilePic(user, fileDownloadUri);

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadEventImage")
    public UploadFileResponse uploadEventImage(@RequestParam("file") MultipartFile file,
            @RequestParam(name = "eid", defaultValue = "1") Long eventId) {
        String fileName = fileStorageService.storeFile(file, "profilepic", "");
        // String fileName = fileStorageService.storeFile(file, "eventimage", "");

        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        long userId = user.getId();

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                .path(fileName).toUriString();

        // if (user.getProfilePic() != null) {
        // String profilepicpath = user.getProfilePic();
        // String oldpicfilename =
        // profilepicpath.substring(profilepicpath.lastIndexOf("/") + 1);

        // System.out.println(oldpicfilename);
        // Path oldFilepath = Paths.get(this.fileStorageProperties.getUploadDir() +
        // "/eventPics/" + eventId + oldpicfilename)
        // .toAbsolutePath().normalize();
        // System.out.println(oldFilepath);
        // try {
        // Files.deleteIfExists(oldFilepath);
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }

        Event event = eventService.getEventById(eventId);
        // user = userService.updateProfilePic(user, fileDownloadUri);
        event = eventService.addEventImage(event, fileDownloadUri);

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadBoothlayout")
    public UploadFileResponse uploadBoothlayout(@RequestParam("file") MultipartFile file,
            @RequestParam(name = "eid", defaultValue = "1") Long eventId) {
        String fileName = fileStorageService.storeFile(file, "profilepic", "");
        // String fileName = fileStorageService.storeFile(file, "boothlayout", "");

        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        long userId = user.getId();

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                .path(fileName).toUriString();

        // if (user.getProfilePic() != null) {
        // String profilepicpath = user.getProfilePic();
        // String oldpicfilename =
        // profilepicpath.substring(profilepicpath.lastIndexOf("/") + 1);

        // System.out.println(oldpicfilename);
        // Path oldFilepath = Paths.get(this.fileStorageProperties.getUploadDir() +
        // "/eventPics/" + eventId + oldpicfilename)
        // .toAbsolutePath().normalize();
        // System.out.println(oldFilepath);
        // try {
        // Files.deleteIfExists(oldFilepath);
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }

        Event event = eventService.getEventById(eventId);
        // user = userService.updateProfilePic(user, fileDownloadUri);
        event.setBoothLayout(fileDownloadUri);
        eventService.updateEvent(event);

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile files) {
        return Arrays.asList(files).stream().map(file -> uploadFile(file)).collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource

        Resource resource = fileStorageService.loadFileAsResource(fileName);
        System.out.println(resource);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}