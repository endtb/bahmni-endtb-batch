package org.bahmni.batch.form;

import org.bahmni.batch.form.domain.BahmniForm;
import org.bahmni.batch.form.domain.Concept;
import org.bahmni.batch.form.domain.Obs;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ObservationProcessorTest {

	@Mock
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Mock
	private FormFieldTransformer formFieldTransformer;

	private List<Obs> obsList;

	private BahmniForm form;

	@Before
	public void setup(){
		initMocks(this);

		form = new BahmniForm();
		form.setFormName(new Concept(1,"formName",0));

		obsList = new ArrayList<>();
		obsList.add(new Obs("treatment1",1,null,new Concept(1,"systolic",0),"120"));
		obsList.add(new Obs("treatment1",1,null,new Concept(1,"diastolic",0),"80"));
		obsList.add(new Obs("treatment1",1,null,new Concept(1,"abcd",0),"180"));

	}

	@Test
	public void shouldRetrieveAllChildObsIds() throws Exception {
		ObservationProcessor observationProcessor = new ObservationProcessor();
		observationProcessor.setJdbcTemplate(namedParameterJdbcTemplate);
		observationProcessor.setObsDetailSqlResource(new ByteArrayResource("blah..blah..blah".getBytes()));
		observationProcessor.setLeafObsSqlResource(new ByteArrayResource("leafobssql".getBytes()));
		observationProcessor.setForm(form);
		observationProcessor.postConstruct();

		Map<String,Object> firstLevelObs1 =   new HashMap<>();
		firstLevelObs1.put("isSet",true);
		firstLevelObs1.put("obsId",2);
		Map<String,Object> firstLevelObs2 =   new HashMap<>();
		firstLevelObs2.put("isSet",false);
		firstLevelObs2.put("obsId",3);

		List<Map<String,Object>> firstLevelObs = new ArrayList<>();
		firstLevelObs.add(firstLevelObs1);
		firstLevelObs.add(firstLevelObs2);

		Map<String,Object> secondLevelObs1 =   new HashMap<>();
		secondLevelObs1.put("isSet",true);
		secondLevelObs1.put("obsId",2);
		Map<String,Object> secondLevelObs2 =   new HashMap<>();
		secondLevelObs2.put("isSet",false);
		secondLevelObs2.put("obsId",3);

		List<Map<String,Object>> secondLevelObs = new ArrayList<>();
		secondLevelObs.add(secondLevelObs1);
		secondLevelObs.add(secondLevelObs2);



		Map<String,Object> thirdLevelObs1 =   new HashMap<>();
		thirdLevelObs1.put("isSet",false);
		thirdLevelObs1.put("obsId",5);

		List<Map<String,Object>> thirdLevelObs = new ArrayList<>();
		thirdLevelObs.add(thirdLevelObs1);


		when(namedParameterJdbcTemplate.query(eq("blah..blah..blah"),any(Map.class),any(ColumnMapRowMapper.class)))
				.thenReturn(firstLevelObs)
				.thenReturn(secondLevelObs)
				.thenReturn(thirdLevelObs);

		List<Integer> allChildObsGroupIds = new ArrayList<>();
		observationProcessor.retrieveChildObsIds(allChildObsGroupIds,Arrays.asList(1));
		assertEquals(3, allChildObsGroupIds.size());
	}

	@Test
	public void shouldReturnObsListOfAllLeafObs() throws Exception {
		ObservationProcessor observationProcessor = new ObservationProcessor();
		observationProcessor.setJdbcTemplate(namedParameterJdbcTemplate);
		observationProcessor.setObsDetailSqlResource(new ByteArrayResource("blah..blah..blah".getBytes()));
		observationProcessor.setLeafObsSqlResource(new ByteArrayResource("get..all..child".getBytes()));
		observationProcessor.setFormFieldTransformer(formFieldTransformer);
		observationProcessor.setExternalCohortTypes(new ArrayList<String>());

		observationProcessor.setForm(form);
		observationProcessor.postConstruct();

		when(namedParameterJdbcTemplate.query(eq("blah..blah..blah"),any(Map.class),any(SingleColumnRowMapper.class)))
				.thenReturn(Arrays.asList(2,3,4))
				.thenReturn(Arrays.asList(5,6,7))
				.thenReturn(new ArrayList());

		when(namedParameterJdbcTemplate.query(eq("get..all..child"),any(Map.class),any(BeanPropertyRowMapper.class)))
				.thenReturn(obsList);

		Map<String, Object> map = new HashMap<>();
		map.put("parent_obs_id",new Integer(1));
		map.put("obs_id",new Integer(0));

		List<Obs> obsListActual = observationProcessor.process(map);
		assertEquals(3, obsListActual.size());
		assertEquals(new Integer(1), obsListActual.get(0).getParentId());
		assertEquals(new Integer(1), obsListActual.get(1).getParentId());
		assertEquals(new Integer(1), obsListActual.get(2).getParentId());
	}

}
