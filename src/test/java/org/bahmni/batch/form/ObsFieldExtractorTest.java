package org.bahmni.batch.form;

import org.bahmni.batch.form.domain.BahmniForm;
import org.bahmni.batch.form.domain.Concept;
import org.bahmni.batch.form.domain.Obs;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ObsFieldExtractorTest {

	@Test
	public void shouldExtractObsListToObjectArray(){
		BahmniForm form = new BahmniForm();
		form.setFormName(new Concept(0,"Blood Pressure",1));
		form.addField(new Concept(1,"Systolic",0));
		form.addField(new Concept(2,"Diastolic",0));
		ObsFieldExtractor fieldExtractor = new ObsFieldExtractor(form);

		Calendar cal = Calendar.getInstance();
		cal.set(2017, 1, 1);
		Date earlierDateCreated = cal.getTime();
		cal.set(2017, 2, 2);
		Date laterDateCreated = cal.getTime();
		cal.set(2017, 3, 3);
		Date earlierDateChanged = cal.getTime();
		cal.set(2017, 4, 4);
		Date laterDateChanged = cal.getTime();

		List<Obs> obsList = new ArrayList<>();
		obsList.add(new Obs("AB1234",1,0, new Concept(1,"Systolic",0),"120", laterDateCreated, laterDateChanged));
		obsList.add(new Obs("AB1234",1,0, new Concept(2,"Diastolic",0),"80", earlierDateCreated, earlierDateCreated));

		List<Object> result = Arrays.asList(fieldExtractor.extract(obsList));

		assertEquals(6,result.size());
		assertTrue(result.contains("120"));
		assertTrue(result.contains("80"));
		assertTrue(result.get(4).equals(earlierDateCreated));
		assertTrue(result.get(5).equals(laterDateChanged));
	}


	@Test
	public void ensureThatSplCharsAreHandledInCSVInTheObsValue(){
		BahmniForm form = new BahmniForm();
		form.setFormName(new Concept(0,"Blood Pressure",1));
		form.addField(new Concept(1,"Systolic",0));
		form.addField(new Concept(2,"Diastolic",0));

		ObsFieldExtractor fieldExtractor = new ObsFieldExtractor(form);

		List<Obs> obsList = new ArrayList<>();
		obsList.add(new Obs("AB1234",1,0, new Concept(1,"Systolic",0),"abc\ndef\tghi,klm"));

		Object[] result = fieldExtractor.extract(obsList);

		assertEquals(new Integer(1),result[0]);
		assertEquals("AB1234",result[1]);
		assertEquals("abc def ghi klm",result[2]);
	}

}
