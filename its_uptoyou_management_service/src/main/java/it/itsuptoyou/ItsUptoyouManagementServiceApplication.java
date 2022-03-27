package it.itsuptoyou;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.utils.Utils;

@SpringBootApplication
@EnableDiscoveryClient
public class ItsUptoyouManagementServiceApplication {

	public static void main(String[] args) {
		ImageKit imageKit = ImageKit.getInstance();
		Configuration config;
		try {
			config = Utils.getSystemConfig(ItsUptoyouManagementServiceApplication.class);
			imageKit.setConfig(config);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SpringApplication.run(ItsUptoyouManagementServiceApplication.class, args);
	}

}
