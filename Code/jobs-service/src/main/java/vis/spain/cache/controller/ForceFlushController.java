package vis.spain.cache.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vis.spain.cache.service.CacheService;
import vis.spain.errorHandling.GeneralSystemFailureException;

@RestController
@RequestMapping("/es/v1/forceFlush")
public class ForceFlushController {
	@Autowired
	CacheService cacheService;

	@GetMapping
	public ResponseEntity<?> flushCache() throws GeneralSystemFailureException {
		cacheService.updateCache();
		return ResponseEntity.noContent().build();
	}
}
