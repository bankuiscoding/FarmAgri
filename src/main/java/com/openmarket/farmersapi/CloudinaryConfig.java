package com.openmarket.farmersapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

	@Bean
	public Cloudinary cloudinary() {
		return new Cloudinary(ObjectUtils.asMap("cloud_name", "dvgrqaqzg", "api_key", "146977386698153", "api_secret",
				"ME6sr2p1xxao8PC6QC8rDGv2sPU"));
	}
}
