package com.is4103.backend.service;

import com.is4103.backend.util.errors.MyFileNotFoundException;
import com.is4103.backend.util.errors.FileStorageException;
import com.is4103.backend.dto.FileStorageProperties;

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

@Service
public class FileStorageService {

    private Path fileStorageLocation;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private static String PROFILE_PIC_TYPE = "profilepic";

    @Autowired
    private static String BIZSUPPORT_DOC_TYPE = "bizsupportdoc";

    @Autowired
    private static String EVENT_IMAGE = "eventimage";

    @Autowired
    private static String BOOTH_BROCHURE= "brochure";

    @Autowired
    private static String PRODUCT_IMAGE= "productImage";

    @Autowired
    private static String BOOTH_LAYOUT= "boothlayout";

    // upload profile pic
    @Autowired
    public FileStorageService() {

        // this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir() +
        // "/profilePics").toAbsolutePath()
        // .normalize();

        // System.out.println("upload path");
        // System.out.println(this.fileStorageLocation);

        // try {
        // // create the upload directory
        // Files.createDirectories(this.fileStorageLocation);
        // } catch (Exception ex) {
        // throw new FileStorageException("Could not create the directory where the
        // uploaded files will be stored.",
        // ex);
        // }
    }

    public String storeFile(MultipartFile file, String filetype, String userEmail) {

        if (filetype.equals(PROFILE_PIC_TYPE)) {

            this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + "/profilePics")
                    .toAbsolutePath().normalize();
        } else if (filetype.equals(BIZSUPPORT_DOC_TYPE)) {

            this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + "/bizSupportDocs")
                    .toAbsolutePath().normalize();
            System.out.println(this.fileStorageLocation);
        } else if (filetype.equals(EVENT_IMAGE)) {
            this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + "/eventImages")
                    .toAbsolutePath().normalize();
            System.out.println(this.fileStorageLocation);
        } else if (filetype.equals(BOOTH_BROCHURE)) {
            this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + "/brochures")
                    .toAbsolutePath().normalize();
            System.out.println(this.fileStorageLocation);
        } else if (filetype.equals(PRODUCT_IMAGE)) {
            this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + "/productImages")
                    .toAbsolutePath().normalize();
            System.out.println(this.fileStorageLocation);
        } else if (filetype.equals(BOOTH_LAYOUT)) {
            this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + "/boothLayouts")
                    .toAbsolutePath().normalize();
        }
        try {
            // create the upload directory
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
                    ex);
        }

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        System.out.println("print org filename");
        System.out.println(fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            if (filetype.equals(PROFILE_PIC_TYPE)) {
                // generate an unique uuid
                UUID uuid = UUID.randomUUID();
                String fileExtension = fileName.split("\\.")[1];
                fileName = "profilepic-" + uuid + "." + fileExtension;
            } else if (filetype.equals(BIZSUPPORT_DOC_TYPE)) {
                String fileExtension = fileName.split("\\.")[1];
                fileName = "bizsupportdoc-" + userEmail + "." + fileExtension;
            } else if (filetype.equals(EVENT_IMAGE)) {
                // generate an unique uuid
                UUID uuid = UUID.randomUUID();
                String fileExtension = fileName.split("\\.")[1];
                fileName = "eventimage-" + uuid + "." + fileExtension;
            } else if (filetype.equals(BOOTH_BROCHURE)) {
                UUID uuid = UUID.randomUUID();
                String fileExtension = fileName.split("\\.")[1];
                fileName = "brochure-" + uuid + "." + fileExtension;
            } else if (filetype.equals(PRODUCT_IMAGE)) {
                UUID uuid = UUID.randomUUID();
                String fileExtension = fileName.split("\\.")[1];
                fileName = "productImage-" + uuid + "." + fileExtension;
            } else if (filetype.equals(BOOTH_LAYOUT)) {
                UUID uuid = UUID.randomUUID();
                String fileExtension = fileName.split("\\.")[1];
                fileName = "boothlayout-" + uuid + "." + fileExtension;
            }
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

            System.out.println(fileName);
            String[] parts = fileName.split("-");
            String fileType = parts[0];
            if (fileType.equals(PROFILE_PIC_TYPE)) {
                this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + "/profilePics")
                        .toAbsolutePath().normalize();
            } else if (fileType.equals(BIZSUPPORT_DOC_TYPE)) {
                this.fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + "/bizSupportDocs")
                        .toAbsolutePath().normalize();
            }
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