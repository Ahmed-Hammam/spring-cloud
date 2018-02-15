package vis.spain.cache.mongodbconfig;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
// @EnableMongoRepositories("vis.spain.account.repository")
@Slf4j
public class MongoConfig extends AbstractMongoConfiguration {

	@Value("${mongodb.host}")
	private String mongoHost;

	@Value("${mongodb.port}")
	private String mongoPort;

	@Value("${mongodb.database}")
	private String mongoDB;

	@Override
	public MongoMappingContext mongoMappingContext() throws ClassNotFoundException {
		return super.mongoMappingContext();
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception {
		log.info("++++++++++++++++++++++++++++++++++++++++");
		log.info("mongoHost:" + mongoHost);
		log.info("mongoPort:" + mongoPort);
		log.info("++++++++++++++++++++++++++++++++++++++++");
		return new MongoClient(mongoHost + ":" + mongoPort);
	}

	@Override
	protected String getDatabaseName() {
		return mongoDB;
	}
}