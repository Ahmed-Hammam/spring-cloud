package vis.spain.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "vis.spain.integration", "vis.spain.backend_util", "vis.spain.utility", "vis.common.core.exception",
		"vis.spain.cache", "vis.common.core.authentication", " vis.spain.model", "vis.common.utils",
		"vis.common.core", "vis.spain.integration.config" })
public class CacheServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(CacheServiceApplication.class, args);

	}

}
