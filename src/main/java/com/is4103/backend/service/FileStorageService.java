package com.is4103.backend.service;

import com.is4103.backend.util.errors.MyFileNotFoundException;
import com.is4103.backend.util.errors.FileStorageException;
import com.is4103.backend.dto.FileStorageProperties;
import com.is4103.backend.dto.SignupRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import com.is4103.backend.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class FileStorageService {

    private Path fileStorageLocation;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageProperties fileStorageProperties;


    // upload profile pic
    @Autowired
    public FileStorageService() {  
     
        // this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir() + "/profilePics").toAbsolutePath()
        //        .normalize();
       
        // System.out.println("upload path");
        // System.out.println(this.fileStorageLocation);

        // try {
        //     // create the upload directory
        //     Files.createDirectories(this.fileStorageLocation);
        // } catch (Exception ex) {
        //     throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
        //             ex);
        // }
    }

    public String storeFile(MultipartFile file,String filetype, String userEmail) {
     
        if(filetype.equals("profilepic")){
        System.out.println("call profilepic");
  
        this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() +
        "/profilePics").toAbsolutePath().normalize();
        }else if(filetype.equals("bizsupportdoc")){
        System.out.println("bizsupportdoc");
        this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() +
        "/bizSupportDocs").toAbsolutePath().normalize();
        System.out.println(this.fileStorageLocation);
        }
        try {
        // create the upload directory
        Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
        throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
        ex);
        }


        // generate an unique uuid
        UUID uuid = UUID.randomUUID();
     //   System.out.println("print uuid");
       // System.out.println(uuid);

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        System.out.println("print org filename");
        System.out.println(fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            String fileExtension = fileName.split("\\.")[1];
            fileName = userEmail + "." + fileExtension;
            System.out.println(fileName);
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}