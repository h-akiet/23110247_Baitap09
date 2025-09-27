package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import vn.iotstar.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class BtGraphQlApplication {

	public static void main(String[] args) {
		SpringApplication.run(BtGraphQlApplication.class, args);
	}

}
