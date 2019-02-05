package org.bahmni.batch;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BatchUtilsTest {

	@Test
	public void ensureThatTheCommaSeparatedConceptNamesAreConvertedToSet(){
		List<String> conceptNames = BatchUtils.convertConceptNamesToSet("\"a,b\",\"c\",\"d\"");

		assertEquals(3,conceptNames.size());
		assertTrue(conceptNames.contains("c"));
		assertTrue(conceptNames.contains("d"));
		assertTrue(conceptNames.contains("a,b"));
	}


	@Test
	public void ensureThatSetIsNotNullWhenConceptNamesIsEmpty(){
		List<String> conceptNames = BatchUtils.convertConceptNamesToSet("");
		assertNotNull(conceptNames);
		assertEquals(0,conceptNames.size());
	}

	@Test
	public void ensureThatSetIsNotNullWhenConceptNamesIsNull(){
		List<String> conceptNames = BatchUtils.convertConceptNamesToSet(null);
		assertNotNull(conceptNames);
		assertEquals(0,conceptNames.size());
	}

	@Test
	public void shouldUpdateSqlWithEmptyExternalCohortType() {
		List<String> cohortTypes = Collections.singletonList("");
		String sql = "SELECT * from table where (:belongsToExternalCohort) IS TRUE " +
				"AND name IN (:externalCohortTypes)";

		String actual = BatchUtils.constructSqlWithCohortParameters(sql, cohortTypes);

		assertEquals("SELECT * from table where (false) IS TRUE AND name IN (\"\")", actual);
	}

	@Test
	public void shouldUpdateSqlWithSingleExternalCohortType() {
		List<String> cohortTypes = Collections.singletonList("E1");
		String sql = "SELECT * from table where (:belongsToExternalCohort) IS TRUE " +
				"AND name IN (:externalCohortTypes)";

		String actual = BatchUtils.constructSqlWithCohortParameters(sql, cohortTypes);

		assertEquals("SELECT * from table where (true) IS TRUE AND name IN (\"E1\")", actual);
	}

	@Test
	public void shouldUpdateSqlWithMultipleExternalCohortTypes() {
		List<String> cohortTypes = Arrays.asList("E1", "E2");
		String sql = "SELECT * from table where (:belongsToExternalCohort) IS TRUE " +
				"AND name IN (:externalCohortTypes)";

		String actual = BatchUtils.constructSqlWithCohortParameters(sql, cohortTypes);

		assertEquals("SELECT * from table where (true) IS TRUE AND name IN (\"E1\", \"E2\")", actual);
	}

	@Test
	public void shouldNotReplaceParametersWhenSqlIsNotParametrized() {
		List<String> cohortTypes = Arrays.asList("E1", "E2");
		String sql = "SELECT * from table";

		String actual = BatchUtils.constructSqlWithCohortParameters(sql, cohortTypes);

		assertEquals(sql, actual);
	}
}
