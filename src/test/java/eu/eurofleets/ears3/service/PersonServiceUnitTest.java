package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.OrganisationDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
//import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//@RunWith(MockitoJUnitRunner.class)
public class PersonServiceUnitTest {
	static Organisation ugent;
	static Organisation rbins;

	static Person veraNoMailRBINS;
	static Person veraNoMailUGent;
	static Person veraRBINS;
	static Person veraUGent;

	static {
		LinkedDataTermDTO ugentTermDTO = new LinkedDataTermDTO("SDN:EDMO::230", null, "UGent-RCMG");
		OrganisationDTO ugentDTO = new OrganisationDTO();
		ugentDTO.term = ugentTermDTO;
		ugentDTO.city = "Stroppendragers";
		ugent = new Organisation(ugentDTO);

		LinkedDataTermDTO kbinTermDTO = new LinkedDataTermDTO("SDN:EDMO::3327", null, "RBINS-ODN");
		OrganisationDTO kbinDTO = new OrganisationDTO();
		kbinDTO.term = kbinTermDTO;
		kbinDTO.city = "Kiekefretters";
		rbins = new Organisation(kbinDTO);

		//organisationRepository.save(ugent); this is a unit test for the Person Service, no data is persisted.
		//organisationRepository.save(kbin);

		PersonDTO veraNoMailRBINSDTO = new PersonDTO("Vera", "Van Lancker", "SDN:EDMO::3327");//RBINS
		PersonDTO veraNoMailUGentDTO = new PersonDTO("Vera", "Van Lancker", "SDN:EDMO::230");//UGent
		PersonDTO veraRBINSDTO = new PersonDTO("Vera", "Van Lancker", "SDN:EDMO::3327", null, null,
				"vvanlancker@naturalsciences.be"); //RBINS
		PersonDTO veraUGentDTO = new PersonDTO("Vera", "Van Lancker", "SDN:EDMO::230", null, null,
				"vera.vanlancker@ugent.be");//UGent

		veraNoMailRBINS = new Person(veraNoMailRBINSDTO);
		veraNoMailUGent = new Person(veraNoMailUGentDTO);
		veraRBINS = new Person(veraRBINSDTO);
		veraUGent = new Person(veraUGentDTO);

		veraNoMailRBINS.setOrganisation(rbins);
		veraNoMailUGent.setOrganisation(ugent);
		veraRBINS.setOrganisation(rbins);
		veraUGent.setOrganisation(ugent);

	}

	@InjectMocks
	private PersonService personService;

	@Mock
	private PersonRepository personRepository;

	@Mock
	private OrganisationRepository organisationRepository;

	@Test
	@Disabled 
	public void findOrCreate() {


		when(personRepository.findByNameAndOrganisation("Vera", "Van Lancker", rbins))
				.thenReturn(veraNoMailRBINS);
		when(personRepository.findByNameAndOrganisation("Vera", "Van Lancker", ugent))
				.thenReturn(veraNoMailUGent);
		when(organisationRepository.findByIdentifier( "SDN:EDMO::3327")).thenReturn(rbins);
		when(organisationRepository.findByIdentifier( "SDN:EDMO::230")).thenReturn(ugent);


		//Person expected = new Person(new PersonDTO("abc", "abc", "abc"));
		Person actual = personService.findOrCreate(veraNoMailRBINS);
		Person actual2 = personService.findOrCreate(veraNoMailUGent);
		//	assertEquals(expected, actual);
	}
}
