package vis.spain.cache.service;

import vis.spain.errorHandling.GeneralSystemFailureException;

public interface CacheService {
	void updateCache() throws GeneralSystemFailureException;
}
