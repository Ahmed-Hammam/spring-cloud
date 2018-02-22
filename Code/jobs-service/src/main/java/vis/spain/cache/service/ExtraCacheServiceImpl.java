package vis.spain.cache.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBObject;

import lombok.extern.slf4j.Slf4j;
import vis.spain.errorHandling.GeneralSystemFailureException;
import vis.spain.errorHandling.GeneralSystemFailureException.GeneralSystemFailureExceptionEnum;
import vis.spain.integration.servicesdata.dtos.ExtrasResponseDTO;
import vis.spain.integration.servicesdata.dtos.ServiceDataFromRP;

@Slf4j
@Component
@Qualifier("ExtraCacheServiceImpl")
public class ExtraCacheServiceImpl implements CacheService {

	@Autowired
	private RestTemplate restTemplate;
	@Value("${extraDataUrl:http://195.233.178.74:19200/api/extras}")
	private String extraCatalogurl;
	@Autowired
	private MongoTemplate mongoTemplate;
	private static final long ONE_HOUR = 60 * 60 * 1000L;

	@Scheduled(fixedDelay = ONE_HOUR)
	public void init() {
		try {
			updateCache();
		} catch (Exception e) {
			log.info("error occured in initialization " + e.getMessage());
		}
	}

	public void updateCache() throws GeneralSystemFailureException {
		try {
			ExtrasResponseDTO[] servicisList = restTemplate.getForObject(extraCatalogurl, ExtrasResponseDTO[].class);
			if (servicisList == null)
				throw new GeneralSystemFailureException(GeneralSystemFailureExceptionEnum.UNXPECTED_ERROR_OCCURED);

			BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkMode.UNORDERED, ExtrasResponseDTO.class);
			List<Pair<Query, Update>> catalogUpsert = new ArrayList<>();
			List<String> codes = new ArrayList<>();
			for (ExtrasResponseDTO catalog : servicisList) {
				DBObject dbObject = new BasicDBObject();
				mongoTemplate.getConverter().write(catalog, dbObject);
				Pair<Query, Update> pair = Pair.<Query, Update>of(
						Query.query(Criteria.where("code").is(catalog.getCode())),
						Update.fromDBObject(new BasicDBObject("$set", dbObject)));
				catalogUpsert.add(pair);
				codes.add(catalog.getCode());
			}
			bulkOperations.upsert(catalogUpsert);
			bulkOperations.remove(Query.query(Criteria.where("code").nin(codes)));
			BulkWriteResult result = bulkOperations.execute();
			log.info("No of insert" + result.getInsertedCount());
			log.info("No of modifiy" + result.getModifiedCount());
			log.info("No of removed" + result.getRemovedCount());
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new GeneralSystemFailureException(GeneralSystemFailureExceptionEnum.UNXPECTED_ERROR_OCCURED);
		}
	}
}
