package org.bahmni.batch;

import org.apache.commons.io.IOUtils;
import org.bahmni.batch.exception.BatchResourceException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatchUtils {

	public static String convertResourceOutputToString(Resource resource){
		try(InputStream is = resource.getInputStream()) {
			return IOUtils.toString(is);
		}
		catch (IOException e) {
			throw new BatchResourceException("Cannot load the provided resource. Unable to continue",e);
		}
	}

	public static List<String> convertConceptNamesToSet(String conceptNames){
		List<String> conceptNamesSet = new ArrayList<>();
		if(conceptNames ==null ||conceptNames.isEmpty())
			return conceptNamesSet;

		String[] tokens = conceptNames.split("\"(\\s*),(\\s*)\"");
		for(String token: tokens){
			conceptNamesSet.add(token.replaceAll("\"",""));
		}

		return conceptNamesSet;
	}

    private static String constructSqlWithParameter(String sql, String parameter, String value) {
        return sql.replaceAll(String.format(":%s", parameter), value);
    }

    public static String constructSqlWithCohortParameters(String sql, List<String> externalCohortTypes){
		Boolean belongsToExternalCohort = externalCohortTypes.size() >= 1 && !externalCohortTypes.get(0).isEmpty();
		String cohortTypesAsString = getCohortTypesAsString(externalCohortTypes);
		sql = constructSqlWithParameter(sql, "belongsToExternalCohort",
				belongsToExternalCohort.toString());
		sql = constructSqlWithParameter(sql, "externalCohortTypes", cohortTypesAsString);
		return sql;
	}

	private static String getCohortTypesAsString(List<String> cohortTypes) {
		List<String> cohortTypesWithQuotes = new ArrayList<>();
		for (String cohortType : cohortTypes) {
			cohortTypesWithQuotes.add("\"" + cohortType + "\"");
		}
		String cohortTypesAsString = Arrays.toString(cohortTypesWithQuotes.toArray());
		cohortTypesAsString = String.format("%s", cohortTypesAsString.substring(1, cohortTypesAsString.length() - 1));
		return cohortTypesAsString;
	}
}
