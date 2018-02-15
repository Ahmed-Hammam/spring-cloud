package vis.spain.cache.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.bulk.BulkWriteResult;

import lombok.extern.slf4j.Slf4j;
import vis.spain.errorHandling.GeneralSystemFailureException;
import vis.spain.errorHandling.GeneralSystemFailureException.GeneralSystemFailureExceptionEnum;
import vis.spain.integration.servicesdata.dtos.ServiceDataFromRP;

@Slf4j
@Component
public class CacheServiceImpl implements CacheService {

	@Autowired
	private RestTemplate restTemplate;
	@Value("${serviceDataUrl:http://195.233.178.74:19200/api/servicios}")
	private String catalogurl;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	@Autowired
	private Mongo mongo;

	private static final long ONE_HOUR = 60 * 60 * 1000L;

	// @Scheduled(initialDelay = ONE_HOUR, fixedDelay = ONE_HOUR)
	// @PostConstruct
	public void updateCache() throws GeneralSystemFailureException {
		// ServiceDataFromRP[] servicisList =
		// restTemplate.getForObject(catalogurl, ServiceDataFromRP[].class);
		ServiceDataFromRP[] servicisList = new ServiceDataFromRP[2];
		servicisList[0] = new ServiceDataFromRP();
		servicisList[0].setCode("aewr");
		servicisList[0].setCategory("rafif");

		servicisList[1] = new ServiceDataFromRP();
		servicisList[1].setCode("a111ewr");
		servicisList[1].setCategory("445656");

		
		/*servicisList[2] = new ServiceDataFromRP();
		servicisList[2].setCode("222");
		servicisList[2].setCategory("sherif");*/
		
		/*
		  DB db = mongo.getDB("vfes");

		  // get a single collection
		  DBCollection collection = db.getCollection("CatalogCache");
		  new BasicDBObject("$set", toSet)
		  
		  
		  
		  // find type = vps , update all matched documents , clients value to 888
		  BasicDBObject updateQuery = new BasicDBObject();
		  updateQuery.append("$set", new BasicDBObject().append("clients", "888"));

		  BasicDBObject searchQuery3 = new BasicDBObject();
		  searchQuery3.append("type", "vps");

		  collection.updateMulti(searchQuery3, updateQuery);
		  	for (ServiceDataFromRP catalog : servicisList) {
			DBObject dbObject = new BasicDBObject();
			mongoTemplate.getConverter().write(catalog, dbObject);
			Pair<Query, Update> pair = Pair.<Query, Update>of(Query.query(Criteria.where("code").is(catalog.getCode())),
					Update.fromDBObject(dbObject));
			catalogUpsert.add(pair);
			codes.add(catalog.getCode());
			
		}
		
		  */
		 
	//	mongoTemplate.insert(Arrays.asList(servicisList), ServiceDataFromRP.class);
		if (servicisList == null)
			throw new GeneralSystemFailureException(GeneralSystemFailureExceptionEnum.UNXPECTED_ERROR_OCCURED);
		// mongoTemplate.insert(Arrays.asList( servicisList), ServiceDataFromRP.class);

		BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkMode.UNORDERED, ServiceDataFromRP.class);
		List<Pair<Query, Update>> catalogUpsert = new ArrayList<>();
		List<String> codes = new ArrayList<>();
		for (ServiceDataFromRP catalog : servicisList) {
			DBObject dbObject = new BasicDBObject();
			mongoTemplate.getConverter().write(catalog, dbObject);
			Pair<Query, Update> pair = Pair.<Query, Update>of(Query.query(Criteria.where("code").is(catalog.getCode())),
					Update.fromDBObject(new BasicDBObject("$set", dbObject) ));
			catalogUpsert.add(pair);
			codes.add(catalog.getCode());
		}
		bulkOperations.upsert(catalogUpsert);
		bulkOperations.remove(Query.query(Criteria.where("code").nin(codes)));
		com.mongodb.BulkWriteResult result = bulkOperations.execute();

		log.info("No of insert" + result.getInsertedCount());
		log.info("No of modifiy" + result.getModifiedCount());
		log.info("No of removed" + result.getRemovedCount());
	}
}
