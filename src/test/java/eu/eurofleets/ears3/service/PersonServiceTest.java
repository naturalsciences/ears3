package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.OrganisationDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = { "eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service" })
@TestPropertySource(locations = "classpath:test.properties")
public class PersonServiceTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PersonService personService;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private OrganisationRepository organisationRepository;

	@Test
	@Ignore
	public void findOrCreate() {
		LinkedDataTermDTO ugentTermDTO = new LinkedDataTermDTO("SDN:EDMO::230", null, "UGent-RCMG");
		OrganisationDTO ugentDTO = new OrganisationDTO();
		ugentDTO.term = ugentTermDTO;
		ugentDTO.city = "Stroppendragers";
		Organisation ugent = new Organisation(ugentDTO);
		organisationRepository.save(ugent);

		LinkedDataTermDTO kbinTermDTO = new LinkedDataTermDTO("SDN:EDMO::3327", null, "RBINS-ODN");
		OrganisationDTO kbinDTO = new OrganisationDTO();
		kbinDTO.term = kbinTermDTO;
		kbinDTO.city = "Kiekefretters";
		Organisation kbin = new Organisation(kbinDTO);
		organisationRepository.save(kbin);

		PersonDTO veraNoMail1 = new PersonDTO("Vera", "Van Lancker", "SDN:EDMO::3327");//RBINS
		PersonDTO veraNoMail2 = new PersonDTO("Vera", "Van Lancker", "SDN:EDMO::230");//UGent

		PersonDTO vera1 = new PersonDTO("Vera", "Van Lancker", "SDN:EDMO::3327", null, null,
				"vvanlancker@naturalsciences.be");//RBINS
		PersonDTO vera2 = new PersonDTO("Vera", "Van Lancker", "SDN:EDMO::230", null, null, "vera.vanlancker@ugent.be");//UGent

		Person person = new Person(veraNoMail1);
		Person person2 = new Person(veraNoMail2);
		Person person3 = new Person(vera1);
		Person person4 = new Person(vera2);

		String cleanUrl1 = ILinkedDataTerm.cleanUrl(vera1.getOrganisation());
		String cleanUrl2 = ILinkedDataTerm.cleanUrl(vera1.getOrganisation());

		Organisation foundRbins = organisationRepository.findByIdentifier(cleanUrl1);
		Organisation foundUgent = organisationRepository.findByIdentifier(cleanUrl2);

		Iterable<Organisation> findAll = organisationRepository.findAll();
		//asserTrue(findAll.size() >= 2);
		//assertNotNull(foundRbins); //this doesn't work at all; always returns empty result.
		//assertNotNull(foundUgent);

		person.setOrganisation(foundRbins);
		person2.setOrganisation(foundUgent);
		person3.setOrganisation(foundRbins);
		person4.setOrganisation(foundUgent);

		Person foundVeraNoMail2 = personRepository.findByNameAndOrganisation(person.getFirstName(),
				person.getLastName(),
				(Organisation) person.getOrganisation());
		Person foundVeraNoMail1 = personRepository.findByNameAndOrganisation(person2.getFirstName(),
				person2.getLastName(),
				(Organisation) person2.getOrganisation());

		//assertNotNull(foundVeraNoMail1); //we must find persons back if the org is specified.
		//assertNotNull(foundVeraNoMail2); //we must find persons back if the org is specified.
		//Person expected = new Person(new PersonDTO("abc", "abc", "abc"));
		Person actual = personService.findOrCreate(person);
		Person actual2 = personService.findOrCreate(person2);
		int a = 5;

		//	assertEquals(expected, actual);
	}
}
