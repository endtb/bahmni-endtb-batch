package org.bahmni.batch.form;

import org.bahmni.batch.form.domain.BahmniForm;
import org.bahmni.batch.form.domain.Concept;
import org.bahmni.batch.form.service.ObsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormListProcessorTest {

	@Mock
	private BahmniFormFactory bahmniFormFactory;

	@Mock
	private ObsService obsService;

	private FormListProcessor formListProcessor;

	@Before
	public void setup(){
		initMocks(this);
		formListProcessor = new FormListProcessor();
		formListProcessor.setObsService(obsService);
		formListProcessor.setBahmniFormFactory(bahmniFormFactory);
		formListProcessor.setAllForms("All Observation Templates");
	}

	@Test
	public void shouldRetrieveAllForms(){
		Concept conceptA = new Concept(1, "a", 1);
		Concept bacteriologyConceptSet = new Concept(3, "Bateriology Concept Set", 1);
		List<Concept> conceptList = new ArrayList();
		conceptList.add(conceptA);

		when(obsService.getChildConcepts(formListProcessor.getAllForms())).thenReturn(conceptList);

		when(obsService.getConceptsByNames("Bacteriology Concept Set")).thenReturn(Arrays.asList(
				bacteriologyConceptSet));

		BahmniForm a11 = new BahmniFormBuilder().withName("a11").build();
		BahmniForm a12 = new BahmniFormBuilder().withName("a12").build();
		BahmniForm a13 = new BahmniFormBuilder().withName("a13").build();


		BahmniForm b11 = new BahmniFormBuilder().withName("b11").build();
		BahmniForm b12 = new BahmniFormBuilder().withName("b12").build();
		BahmniForm b13 = new BahmniFormBuilder().withName("b13").build();

		BahmniForm a1 = new BahmniFormBuilder().withName("a1").withChild(a11).withChild(a12).withChild(a13).build();
		BahmniForm b1 = new BahmniFormBuilder().withName("b1").withChild(b11).withChild(b12).withChild(b13).build();

		BahmniForm a = new BahmniFormBuilder().withName("a").withChild(a1).withChild(b1).build();

		BahmniForm bacteriologyResultForm = new BahmniFormBuilder().withName("Bacteriology Result").build();
		BahmniForm bacteriologyForm = new BahmniFormBuilder().withName("Bateriology Concept Set").withChild(bacteriologyResultForm).build();

		when(bahmniFormFactory.createForm(conceptA,null)).thenReturn(a);
		when(bahmniFormFactory.createForm(bacteriologyConceptSet,null)).thenReturn(bacteriologyForm);

		List<BahmniForm> expected = Arrays.asList(a,a1,b1,a11,a12,a13,b11,b12,b13,bacteriologyForm,bacteriologyResultForm);

		List<BahmniForm> actual= formListProcessor.retrieveAllForms();

		assertEquals(expected.size(),actual.size());
		assertEquals(new HashSet(expected),new HashSet(actual));

	}

}
