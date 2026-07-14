package com.openmarket.farmersapi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin("*")
public class ImageController {

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.emptyMap());

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            return "Upload Failed";
        }
    }
}
