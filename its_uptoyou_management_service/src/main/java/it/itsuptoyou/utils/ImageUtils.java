package it.itsuptoyou.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.utils.Utils;
import it.itsuptoyou.collections.User;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class ImageUtils {

	@Value(value="${UrlEndpoint}")
	private String imageUrlEndpoint;
	
	@Value(value="${base.image.url.path}")
	private String imageBaseUrlPath;
	
	
	public String getImage(User user) {
		String url="";
		Map<String, Object> options = new HashMap<>();
		List<Map<String, String>> transformation=new ArrayList<Map<String, String>>();
		Map<String, String> scale=new HashMap<>();
		transformation.add(scale);
		options.put("src", imageUrlEndpoint+"/"+imageBaseUrlPath+"/"+user.getUserId()+"/user-image-"+user.getUserId());
		options.put("transformation", transformation);
		return ImageKit.getInstance().getUrl(options);
	}
	
	public void uploadImage(MultipartFile image, User user) throws IOException {
		FileCreateRequest fcr = new FileCreateRequest(image.getBytes(),"user-image-"+user.getUserId());
		fcr.setUseUniqueFileName(false);
		fcr.setFolder( "/"+imageBaseUrlPath+"/"+user.getUserId()+"/");
		Result result = ImageKit.getInstance().upload(fcr);
		log.info(result);
		log.info("Raw Response: " + result.getRaw());
		log.info("Map Response:" + result.getMap());
	}
}
