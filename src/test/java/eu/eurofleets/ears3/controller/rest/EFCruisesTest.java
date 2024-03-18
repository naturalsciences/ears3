/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import eu.eurofleets.ears3.utilities.DateUtilities;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Thomas Vandenberghe
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
@WebAppConfiguration
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) //reset the database to base state before each test method
public class EFCruisesTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private ObjectMapper objectMapper;

    public static CruiseDTO createCruise(String identifier, String objectives, String start, String stop, String platform, String departureHarbour, String arrivalHarbour, String collateCentre, String PIFirst, String PILast, String PIEDMO, String seaArea) {
        CruiseDTO c = new CruiseDTO();
        c.arrivalHarbour = arrivalHarbour;
        c.departureHarbour = departureHarbour;
        c.identifier = identifier;
        c.name = identifier;
        c.startDate = DateUtilities.toOffsetDate(start);
        c.endDate = DateUtilities.toOffsetDate(stop);
        c.objectives = objectives;
        c.collateCentre = collateCentre;
        c.platform = platform;
        List<String> p02 = Arrays.asList(new String[]{});
        List<PersonDTO> chiefScientists = Arrays.asList(new PersonDTO[]{new PersonDTO(PIFirst, PILast, PIEDMO)});

        List<String> seaAreas = Arrays.asList(new String[]{seaArea});
        c.P02 = p02;
        c.chiefScientists = chiefScientists;
        c.isCancelled = false;
        c.seaAreas = seaAreas;
        //c.programs = programs;
        return c;
    }

    public static ProgramDTO createProgramFromCruise(CruiseDTO cruise) {
        ProgramDTO p = new ProgramDTO();
        p.description = cruise.objectives;
        p.identifier = cruise.identifier;
        p.name = cruise.name;
        p.principalInvestigators = cruise.chiefScientists;
        return p;
    }

    public static ProgramDTO createProgram(String identifier, String description, String PIFirst, String PILast, String PIEDMO) {
        ProgramDTO p = new ProgramDTO();
        p.description = description;
        p.identifier = identifier;
        p.name = identifier;
        p.principalInvestigators = Arrays.asList(new PersonDTO[]{new PersonDTO(PIFirst, PILast, PIEDMO)});
        return p;
    }

    public static void postProgram(MockMvc mockMvc, ProgramDTO program, ObjectMapper objectMapper) throws Exception {
        String json = objectMapper.writeValueAsString(program);
     //   System.out.println("curl -X POST http://localhost/ears3/api/program -H 'Content-Type: application/json' -d '" + json + "';");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/program").contentType(MediaType.APPLICATION_JSON).content(json))
                // //.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    public static void postCruise(MockMvc mockMvc, CruiseDTO cruise, ObjectMapper objectMapper) throws Exception {
        String json = objectMapper.writeValueAsString(cruise);
     //   System.out.println("curl -X POST http://localhost/ears3/api/cruise -H 'Content-Type: application/json' -d '" + json + "';");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cruise").contentType(MediaType.APPLICATION_JSON).content(json))
                // //.andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void testCreateEFCruises() throws Exception {

        /*
CALYPSO
CABLE
DOMUSe (Co-PI on CABLE)
GRACE
SEAQUAKE (Co-PI on GRACE cruise)
TAlPro2022
IsoMed (RTA TalPro2022)
SENERGY
IOPD
FIGURE
CARING (Co-PI on FIGURE)
CARBO-ACID
GLICE
SINES

VISIT
Hydee-Obs
ERODOTO
SLOGARO-II
         */
        CruiseDTO cable = createCruise("CABLE", "Central Baltic Sea Circulation Experiment (CABLE) addresses the knowledge gap regarding the prevailing subsurface circulation patterns in the Central and Northern Baltic Proper. In order to fill in this knowledge gap, we plan to conduct a comprehensive circulation measurement campaign across the Central Baltic Sea. We aim to capture the current dynamics in the subsurface part of the Baltic Sea Conveyer Belt at the time-scales from hours to months and lateral scales from 10-20 kilometers to basin-wide with lateral mooring array of acoustic current profilers. Secondly, we aim to capture the connections between circulation dynamics in the Central Baltic Sea and changes in the water properties and selected phytoplankton species in the Northeastern Baltic Sea. There are two main activities of the fields work planned in CABLE: 1) Deployment and recovery of moorings in the Central Baltic Sea; 2) Profiling and sampling at stations. Twelve moorings carrying current measurement devices are planned to deploy for six months. RV is planned to apply from Eurofleets for two cruises. We apply ship time of four + four days from the Eurofleets. Finnish Meteorological Institute has promised to contribute one more day., which will be used in the first cruise. First cruise, envisioned in April 2021 is dedicated to the deployment of moorings. Recovery of the equipment is planned on the second cruise in October 2021. Water column profiling (CTD+) and sampling is planned in both cruises. Sediment cores (up to 50 cm) by Gemax sediment samplers will be collected. Water/sediment samples will be used for phytoplankton/cyst analysis and for the extraction of environmental DNA to confirm the presence of species belonging to the Scrippsiella complex by metabarcoding/ qPCR and for establishing cultures of each species for comparing the population genetic structure between cysts and plankton from different stations in relation to the prevailing current patterns.", "2022-04-22 06:00:00", "2022-04-29 23:00:00", "SDN:C17::34A3", "SDN:C38::BSH92", "SDN:C38::BSH92", "SDN:EDMO::120", "Taavi ", "Liblik", "SDN:EDMO::713", "SDN:C19::2_3");
        CruiseDTO carbo_acid = createCruise("CARBO-ACID", "Recent studies based on live and fossil planktonic foraminifera assemblages and Mg/Ca ratios in G. bulloides collected close to the NW Iberian coast, suggest that the Iberian margin is already showing the effects of ocean acidification, especially in areas of stronger seasonal coastal upwelling. This cruise proposal establishes the base for the investigation of potential effects of ocean acidification on carbonate marine organisms (coccolithophores, pteropods, planktonic and benthic foraminifera, and corals) along the Iberian margin. For that, we will collect oceanographic data and water, plankton, cold-water corals, and sediment samples during an upwelling season along two transects coinciding with the two prominent upwelling filaments of the Iberia Margin: the Cape Finisterra and Estremadura Spur filaments. This study will focus on seawater pH trends in this seasonal coastal upwelling region during the pre- and post-industrial transition (centennial-to-decadal time resolution) and interglacial/ glacial climatic cycles. These new results will allow to compare variations at different time-scales and under different forcings (natural vs. anthropogenic), and to estimate the amplitude of future changes in the ocean in terms of CO2 biogeochemistry and biota response. The retrieved samples will contribute to three post-doctoral proposals and one PhD proposal. The data obtained will also be available for the GEOTRACES Program.", "2022-08-03 06:00:00", "2022-08-12 23:00:00", "SDN:C17::29RM", "SDN:C38::BSH263", "SDN:C38::BSH118", "SDN:EDMO::3330", "Emilia ", "Salgueiro", "SDN:EDMO::3288", "SDN:C19::SVX00015");
        CruiseDTO erodoto = createCruise("ERODOTO", "ERODOTO proposes to analyse and quantify the active dynamics of a shelf-incising, close-to-shore submarine canyon and provide model for geohazard assessment and risk management in equivalent coastal settings. The slope instabilities associated with submarine canyons that reach to within a kilometre from shore pose a serious threat to coastal communities worldwide. The pilot study of the Squillace amphitheatre-headed Canyon offshore southern Italy (Ionian Calabria), which has a close connection to ephemeral river systems regionally known as ‘fiumare'', will be used quantify the effect of retrogressive erosion along the canyon tributaries and headwalls, induced by repeated sediment flows. A specifically designed programme of AUV mapping, combined with in situ ROV observations and sampling, geotechnical measurements on sediment cores, and the deployment of a moored ADCP will enable to disclose the canyon erosive dynamics and to assess its geohazards in areas where the headwalls are closest to the coast (<400m). The programme will demonstrate an innovative and holistic survey approach that can be transferred to other canyons showing similar geohazards in order to define precise monitoring programmes and efficient risk management. The ERODOTO onboard and onshore Teams include European top experts in the relevant fields to ensure that operations during the cruise, and the resulting science, reach their maximum potential. Training will be provided onboard to early career scientists as well as scientists who have less access to marine infrastructure to give them exposure to new technologies, the operational planning and data analysis related to observational marine science. Close links to national (Italian Civil Protection) and international programmes (e.g. INCISE network) will be guaranteed through the members of the ERODOTO team. A strong component of outreach and stakeholder interaction will ensure the societal impact of the project.", "2023-06-01 06:00:00", "2023-06-01 23:00:00", "SDN:C17::36AE", "SDN:C38::BSH155", "SDN:C38::BSH155", "SDN:EDMO::120", "Silvia ", "Ceramicola", "SDN:EDMO::150", "SDN:C19::3_1_2_3");
        CruiseDTO figure = createCruise("FIGURE", "The biological fixation of dinitrogen (N2) by marine microbes called ''diazotrophs'' sustains ~50% of primary production in the ocean, boosting CO2 absorption and mitigating climate change. Our knowledge of diazotroph diversity and activity (diazotrophy) derives from studies conducted at very distant spatiotemporal scales: i) discrete and short duration measurements in small seawater volumes isolated from the environment, and ii) spatial extrapolations and global models of diazotrophy projected over decades to centuries. The knowledge gap between these spatiotemporal scales impedes constraining nitrogen inputs and thus quantify and predict the ocean''s potential to withdraw CO2. This gap lies at the fine scales: dynamic seawater structures <200 Km wide and <2 months lifetime. The poor spatiotemporal resolution of oceanographic in situ sampling is incapable of resolving fine scales. FIGURE will bridge this gap by implementing on-cruise high-resolution diazotrophy measurements >10-50 times faster than those available today, focusing on the Gulf Stream. Fine scales will be characterized by underway sensors of current speed, temperature and salinity, vertical nutrient fluxes and satellite altimetry data. The community composition will be examined by molecular biology methods. Diazotroph activity will be measured using high sensitivity trace gas analysis. Physical and biological data will be correlated to elucidate the effect of fine scales on diazotrophy and to assess their impact on nitrogen inputs to the ocean. The achievements of FIGURE will imply a break-through advance in oceanography and stimulate applications in biotechnology and environmental science, providing new tools, approaches and knowledge for climate change adaptation and mitigation.", "2022-07-21 06:00:00", "2022-07-28 23:00:00", "SDN:C17::33H4", "SDN:C38::BSH196", "SDN:C38::BSH196", "SDN:EDMO::3330", "Mar ", "Benavides", "SDN:EDMO::3078", "SDN:C19::SVX00017");
        CruiseDTO glice = createCruise("GLICE", "The temporal and spatial scale over which iceberg melt occurs is a key consideration in determining the relative importance of icebergs for marine primary production. In moderately-productive inner-fjord environments, the summertime release of lithogenic particles from icebergs may act as ballast for sinking organic carbon and thus enhance the marine carbon sink in these environments. Conversely, in less-productive offshore waters the primary effect may be fertilisation from the release of iceberg-derived Fe and upwelling of other nutrients (NO3, PO4, Si) around large icebergs. The goal of this project will be to investigate the spatial scale over which these processes operate; both laterally with distance from an iceberg source, and over the depth of meltwater release in the water column.", "2022-08-10 06:00:00", "2022-08-24 23:00:00", "SDN:C17::GLSW", "SDN:C38::BSH5434", "SDN:C38::BSH5434", "SDN:EDMO::3330", "Mark ", "Hopwood", "SDN:EDMO::5501", "SDN:C19::9");
        CruiseDTO grace = createCruise("GRACE", "The Alboran Sea (SW Mediterranean) lies at an active tectonic setting, and the seabed is constantly affected by chronic and catastrophic geological hazards. This proposal focuses on the Ceuta Canyon, a large-scale downslope feature overlooked as a mere boundary geological feature of the adjacent and much larger Ceuta Drift. However, a) its location is tectonically controlled and seems to be related to onshore active structures; b) its head (located close to the coast) and the eastern margin are affected by arcuate-shaped scars; and c) it is deeply influenced by vigorous bottom currents that cause erosion on its margins and the rapid growth of the Ceuta Drift. This context in which tectonism, bottom currents and sedimentary instabilities show a complex interplay raises concerns regarding the high exposure of coastal populations to geohazards. In addition, new projects to place electrical and telecommunications connections in between Spain and Morocco are endangered. This proposal aims to study the geological risks in the Ceuta Canyon and its annex areas with a multidisciplinary lens, focusing on the sedimentary processes, chrono-stratigraphy, and oceanography. To properly tackle this task, ultra-high resolution imaging of the seafloor must be carried out. For that, the incorporation of an AUV will allow geohazard mapping with unprecedented precision, providing higher resolution datasets than those achieved from surface vessels with traditional techniques.", "2022-05-01 06:00:00", "2022-05-01 23:00:00", "SDN:C17::11BU", "SDN:C38::BSH3042", "SDN:C38::BSH3042", "SDN:EDMO::3051", "Carmen ", "Juan Valenzuela", "SDN:EDMO::280", "SDN:C19::3_1_1_2");
        CruiseDTO hydee_obs = createCruise("Hydee-Obs", "Despite numerous investigations formation of hydrates and especially estimates on saturations of hydrate deposits are still not completely understood. Project HYDEE acquired a multi-parameter study of hydrate indicators along the Hikurangi margin of North Island, New Zealand. Multichannel seismic application provides structural details and Vp velocity distribution, marine controlled source electromagnetic provides resistivity distributions and geologic sampling added geochemical parameters. A well developed volume of hydrate formation could be identified, events of gas injection into the hydrate stability zone were observed next to it. The estimate of hydrate saturation requires a description of the hydrate formation process (pore filling or cementation). Changes in shear strength across the hydrate occurrence can help to specify such processes. Avoiding costly drilling such information can be provided by converted shear wave velocity-depth models. High frequent GI airgun sources will enable to distinguish between hydrate and gas injection events. Here we propose to acquire such additional data by deployment of four component ocean-bottom seismometers (OBS). Closely spaced instruments and densely generated seismic signals will enable the required coverage and add this missing element to the yet incomplete data base. Parallel deployment of a high-resolution multichannel seismic streamer will provide the necessary details in structural imaging to assign the OBS events.", "2023-04-01 06:00:00", "2023-04-01 23:00:00", "SDN:C17::61TG", "SDN:C38::BSH226", "SDN:C38::BSH226", "SDN:EDMO::3051", "Joerg ", "Bialas", "SDN:EDMO::5501", "SDN:C19::SVX00024");
        CruiseDTO icon = createCruise("ICON", "We aim to investigate the contemporary and paleo-ice-ocean-sedimentary dynamics of the Hillary Canyon and adjacent Eastern Ross Sea Trough Mouth Fan (TMF), Ross Sea, Antarctica, to improve understanding of the sensitivity of the Antarctic Ice Sheet to future climatic changes. This will be achieved through comparing contemporary and paleo-slope processes to past changes in climate. Understanding past ice sheet behavior in response to past climatic conditions is important as increased ocean heat supply to Antarctic continental shelves is projected to cause accelerated ice sheet loss and contribute significantly to global sea-level rise over the coming decades. On reaching grounded ice, warm water incursions can trigger ice sheet instability leading to ice discharge and sea level rise. Incursions vary depending on ice shelf geometry and seafloor bathymetry making predictions of ice sheet and oceanic changes difficult. The Hillary Canyon and adjacent flanks are of particular importance due to their location in one of the greatest regions of cold, dense water export in the world. Changes in temperature or salinity of these cascading waters, due to increased glacial meltwater input, can impact the strength of the meridional overturning circulation with large-scale effects to global atmospheric and oceanic circulation. This region has very poor seafloor data coverage which is one of the major factors limiting the ability of numerical modelling studies to constrain past and future ice and sea-level change estimates. This project will provide geophysical mapping and assessment of the seafloor and sub-surface of the Eastern Ross Sea TMF, integration of contemporary oceanographic processes and paleo-processes to reconstruct how the Ross Sea TMF responded to past climatic changes. Better constraints on slope dynamics will assist numerical modelling studies in predicting changes in oceanic drivers and responses to ice shelf melting, ice-sheet collapse and sea level change.", "2023-04-01 06:00:00", "2023-04-01 23:00:00", "SDN:C17::487A", "SDN:C38::BSH226", "SDN:C38::BSH226", "SDN:EDMO::3051", "Jenny ", "Gales", "SDN:EDMO::5364", "SDN:C19::10_10");
        CruiseDTO iopd = createCruise("IOPD", "", "2022-06-28 06:00:00", "2022-07-10 23:00:00", "SDN:C17::GLSW", "SDN:C38::BSH5588", "SDN:C38::BSH5588", "SDN:EDMO::3330", "Wieter ", "Boone", "SDN:EDMO::422", "SDN:C19::9_6");

        CruiseDTO rosebud = createCruise("ROSEBUD", "Dense shelf-water (DSW) cascading is an oceanic phenomenon that occurs at various locations around the globe. The process is particularly important in Antarctica, where DSW flows off the continental shelf as a density-driven current that feeds the Antarctic Bottom Water (AABW), a major component of Earth''s deep-ocean circulation. Recent work has shown a reduction in DSW export in some Antarctic areas, highlighting the sensitivity of this process to climate change. Antarctic DSW generation occurs in areas with enhanced sea-ice production and at the base of large ice shelves. In this project we aim to study the hydrodynamics of DSW cascades in the western Ross Sea - one of the primary source areas for Antarctic DSW - and the effect of cascading flows on sediment transport and seafloor morphology. The high salinity DSW produced on the Ross shelf is exported to the slope through the Drygalski Trough. Previous in situ measurements show DSW outflow events lasting several months during the austral summer/fall season with flow velocities up to 1.4 m s-1 and thicknesses ranging from 100 to 250 m at 1400 m water depth. It has been suggested that DSW currents can reshape the seafloor, especially in submarine canyons, but this has never been demonstrated in Polar Regions. The effect of flow confinement (due to coastal capes, cross-shelf troughs, and submarine valleys, for example) on DSW flow dynamics is not well understood, nor is their capacity to transport sediment and organic matter into deep water. Moreover, the importance of DSW cascading and its interplay with AABW during Quaternary glacial periods is largely unknown. The summer-to-fall season in the western Ross Sea is an ideal time to collect the observations and measurements that are needed to address these questions, so adding a DSW component to the itinerary of the RV Laura Bassi is an extraordinary opportunity that should not be missed.", "2023-04-01 06:00:00", "2023-04-01 23:00:00", "SDN:C17::487A", "SDN:C38::BSH226", "SDN:C38::BSH226", "SDN:EDMO::3330", "David ", "Amblas", "SDN:EDMO::5506", "SDN:C19::10_10");
        CruiseDTO senergy = createCruise("SENERGY", "Understanding species responses to physical conditions help predict impacts of environmental changes (greenhouse effect, offshore constructions) in marine habitats. Most studies estimate impacts by quantifying associations between animals and physical conditions, and using these associations to predict changes in abundances and distributions under different scenarios. These approaches are arguably most appropriate for ectothermic and/or sessile species that respond directly to these changes, with particular physical conditions needed for survival and growth. However, they are probably less appropriate for endothermic and dynamic species that respond indirectly to the consequence of these changes, with physical conditions promoting prey abundance and availability. As it provides a more relevant measurement for endothermic and mobile species, estimations of responses to environmental change may be improved by quantifying associations with measurements of energetic gains rather than physical conditions. This study uses the ‘energy landscape'' concept to quantify energetic gains in different habitats for a piscivorous seabird (common guillemots Uria aalge) in Iceland. To do so, echosounders will measure the depth and prevalence of prey across gradients in physical conditions, quantifying the energetic cost of locating foraging opportunities. Trawls will then be used to estimate abundance and energetic content across gradients in physical conditions, quantifying the energetic benefits of consuming prey. These outputs will then be combined to identify physical conditions that maximise energetic gains. Observational surveys of seabirds will then be used to ground-truth these predictions. This project will advance the concept of energy landscapes by including energetic gains alongside costs, and increase our knowledge of habitat-selection in foraging seabirds.", "2022-06-18 06:00:00", "2022-06-25 23:00:00", "SDN:C17::46AS", "SDN:C38::BSH178", "SDN:C38::BSH178", "SDN:EDMO::120", "James ", "Waggitt", "SDN:EDMO::1468", "SDN:C19::9_8");
        CruiseDTO sines = createCruise("SINES", "Synergistic interactions between climate change and anthropogenic stressors will impact key ecosystem services at Eastern Boundary Upwelling Systems: ocean acidification, warming and deoxygenation are current threats with expected negative impacts in few decades. Even when they are between the world''s most productive ecosystems, the exchanges between the productive waters of coastal upwelling systems and the adjacent ocean is far from being understood. Current biogeochemical models do not include upwelling systems with enough resolution and there is a lack of in situ regional proxies to allow an evaluation of present and past conditions in the framework of a changing ocean. Only within an integrated approach, joining forefront remote technologies to in situ physicochemical characterization of water and sediments can the climatic stressors acting over ecosystem services on upwelling fronts be identified. The oceanographic conditions along the Western Iberian Peninsula and the socioeconomic importance of the Iberian Upwelling stimulates this multidisciplinary expedition that will be carried out along a section located across the expected upwelling front. The cooperation of all involved partners will provide novel and fundamental calibration proxies for: automatic sensors vs in situ measurements and genomic derived vs classically determined ecosystem structure. Such a combined approach will provide a baseline for the identification, quantification and validation of modern ecosystem changes with respect to pre-industrial status. This project will bring together a critical mass of oceanographers from 18 different research laboratories (6 EU countries) and an independent science communication company. No national project has the capacity to get together such a diverse team of experts aiming to understand the same oceanographic process. The research team, especially the on board participants, has a large proportion of early career scientist and young students in formation.", "2022-09-11 06:00:00", "2022-09-20 23:00:00", "SDN:C17::29AH", "SDN:C38::BSH263", "SDN:C38::BSH118", "SDN:EDMO::120", "Marcos ", "Fontela", "SDN:EDMO::2516", "SDN:C19::SVX00015");
        CruiseDTO slogaro_ii = createCruise("SLOGARO-II", "EUROFLEETS cruise AK211 with RV AKADEMIC served in 2012 to execute geophysical investigations at gas release sites along the Bulgarian margin of the Black Sea. In the course of the project seafloor depressions aligned with such seep sites were interpreted to be caused by weakening of the sediment due to gas migration. Proximity of the depressions to headwalls and slumps lead to the question if gas migration might cause slope failure. Cruise MSM-34 with the RV MARAIA S MERIAN showed that mass transport deposits occurred with up to 600 km3 in the Danube Fan. 3D seismic data images showed that deposition sequences may prevent vertical gas migration pathways from active gas release. Similar depressions like those investigated along the Bulgarian margin were mapped during following cruises M-142 & M-143 with RV METEOR along the Romanian margin in the area where massive slope failure has been documented before. PARASOUND images identified extensional seafloor depressions above faults at various stages of development. Due to limited signal penetration the base of the faults and connection to the gas plumbing system could not be revealed. This proposal sets out to collect new seismic data, gravity cores and cone-penetrometer measures across the observed seafloor depressions. Data analyses should help to evaluate if the development of these structures represents a new mechanism of slope failure development. During the last glacial maxima limnic conditions caused reduced salinity in the sediments. Since the equilibration is still underway and will cause a deepening of the top of the hydrate stability zone. This process will release further free gas in the margins sediments and may accelerate this process. Therefore, heatflow measures will be undertaken as well to support new calculations on hydrate distribution.", "2022-05-10 06:00:00", "2022-05-17 23:00:00", "SDN:C17::73AA", "SDN:C38::BSH46", "SDN:C38::BSH46", "SDN:EDMO::3051", "Joerg ", "Bialas", "SDN:EDMO::5501", "SDN:C19::3_3");
        CruiseDTO talpro2022 = createCruise("TAlPro2022", "The Mediterranean ocean observing community is committed to conduct regular repeat hydrological surveys through the MedSHIP program, thereby sampling the entire Mediterranean Sea in a systematic way (see Schroeder et al., 2015, Oceanography). Here we propose to repeat the two western Mediterranean meridional sections in an 11-day expedition on board RV BELGICA II during summer of 2022 (cruise TAlPro2022). The last occupation of these transects was conducted in 2016 during the EF cruise TAlPro2016 (Jullion, 2016), to which most participants to this proposal contributed. The primary purpose of these regular repeats is to quantify and characterize changes in the Mediterranean sea on a sub-decadal time frame. We propose to do so by conducting high-accuracy observations of all “level 1” variables defined in the GO-SHIP program (www.goship.org) and compare those to historical data. In addition we will quantify transport and properties of the flow through the straits between western Mediterranean, Tyrrhenian Sea and Eastern Mediterranean Sea. This program complements the ship-based component of the MOOSE program in that the geographical range is larger and we plan to measure additional variables normally not included in the MOOSE program.", "2022-05-17 06:00:00", "2022-05-26 23:00:00", "SDN:C17::11BU", "SDN:C38::BSH209", "SDN:C38::BSH3870", "SDN:EDMO::3330", "Katrin ", "Schroeder", "SDN:EDMO::227", "SDN:C19::3_1_1_2");
        CruiseDTO visit = createCruise("VISIT", "Subduction plate boundary faults are capable of generating some of the largest earthquakes and tsunami on Earth, such as the magnitude 9.0, 2011 Tōhoku earthquake, Japan. However, over the last two decades it has been discovered that some subduction plate boundary faults slip slowly and aseismically in slow slip events. Resolving what controls whether a plate boundary fault ruptures in large earthquakes or slips slowly is one of the most important challenges in seismology today. The Hikurangi subduction margin, New Zealand is special in that it shows a profound change in the depth of interseismic locking on the plate boundary fault (often used as a proxy for earthquake potential) and seismic behaviour along strike. One of the leading hypotheses to explain the change in seismic behaviour is that the material properties of the incoming sedimentary section, specifically a sequence of pelagic sediments that the plate interface is hosted within, vary along the margin. Currently, insufficient seismic data exist to characterise the thickness and seismic facies character to test this hypothesis. We propose to collect a suite of new multi-channel seismic reflection profiles using the R/V Tangaroa to (i) allow us to map the thickness of the pelagic section along-strike and see how it relates to patterns of interseismic locking, (ii) correlate the unit which hosts the subduction interface in the south to the north where IODP drilling data (core and logs) exist, to constrain the physical properties of the unit in the north where the interface is largely decoupled and shallow slow slip events occur and (iii) provide site-survey data for future IODP drilling to target the unit which hosts the plate interface in the south, where locking depths are deep and the potential for large earthquakes is high. The combination of the new VISIT-Hikurangi seismic data and IODP drilling will allow us to test the hypothesis that material properties of subducting sediments control seismic behaviour at subduction zones.", "2023-04-01 06:00:00", "2023-04-01 23:00:00", "SDN:C17::61TG", "SDN:C38::BSH226", "SDN:C38::BSH226", "SDN:EDMO::3051", "Rebecca ", "Bell", "SDN:EDMO::2658", "SDN:C19::SVX00024");

        ProgramDTO cableP = createProgramFromCruise(cable);
        ProgramDTO carbo_acidP = createProgramFromCruise(carbo_acid);
        ProgramDTO erodotoP = createProgramFromCruise(erodoto);
        ProgramDTO figureP = createProgramFromCruise(figure);
        ProgramDTO gliceP = createProgramFromCruise(glice);
        ProgramDTO graceP = createProgramFromCruise(grace);
        ProgramDTO hydee_obsP = createProgramFromCruise(hydee_obs);
        ProgramDTO iconP = createProgramFromCruise(icon);
        ProgramDTO iopdP = createProgramFromCruise(iopd);
        ProgramDTO rosebudP = createProgramFromCruise(rosebud);
        ProgramDTO senergyP = createProgramFromCruise(senergy);
        ProgramDTO sinesP = createProgramFromCruise(sines);
        ProgramDTO slogaro_iiP = createProgramFromCruise(slogaro_ii);
        ProgramDTO talpro2022P = createProgramFromCruise(talpro2022);
        ProgramDTO visitP = createProgramFromCruise(visit);
        // , String identifier, String description, String PIFirst, String PILast, String PIEDMO
        ProgramDTO caringP = createProgram("CARING", "Over the past 200 years human activities have emitted large amounts of CO2 into the atmosphere (namely anthropogenic carbon, Cant) increasing the atmospheric CO2 content to unprecedented levels. The ocean absorbs about 30% of these emissions, acting as a net sink. Of the ocean basins, the North Atlantic is the one with the highest storage of Cant per area. Yet, it is still uncertain how much of the Cant uptake occurs (locally) at subpolar latitudes or (remotely) in the subtropics; or what are the driving mechanisms ultimately regulating its storage at different temporal scales. CARING will provide a contemporary novel assessment of the downstream Gulf Stream carbon and nutrient transport and carbon uptake capacity conveyed by Gulf Stream intermediate waters poleward, so as to elucidate its role as first-order far field control to the nutrient and carbon irrigation to the North Atlantic. The sampling strategy comprises CTD and discrete sampling of the first 2000 dbar of the water-column, and continuous high-resolution underway sampling, the latter targeted at assessing the impact of the fine scale. The achievements of CARING will imply a small but significant step-forwards into narrowing down the current gap of knowledge about the Cant sink and storage variability, drivers, and related timescales.", "Lidia", "Carracedo", "SDN:EDMO::848");

        cable.programs.add(cableP.identifier);
        carbo_acid.programs.add(carbo_acidP.identifier);
        erodoto.programs.add(erodotoP.identifier);
        figure.programs.add(figureP.identifier);
        figure.programs.add(caringP.identifier);
        glice.programs.add(gliceP.identifier);
        grace.programs.add(graceP.identifier);
        hydee_obs.programs.add(hydee_obsP.identifier);
        icon.programs.add(iconP.identifier);
        iopd.programs.add(iopdP.identifier);
        rosebud.programs.add(rosebudP.identifier);
        senergy.programs.add(senergyP.identifier);
        sines.programs.add(sinesP.identifier);
        slogaro_ii.programs.add(slogaro_iiP.identifier);
        talpro2022.programs.add(talpro2022P.identifier);
        visit.programs.add(visitP.identifier);

        postProgram(mockMvc, cableP, objectMapper);
        postProgram(mockMvc, carbo_acidP, objectMapper);
        postProgram(mockMvc, erodotoP, objectMapper);
    //    postProgram(mockMvc, figureP, objectMapper); //TODO removed for the time being
        postProgram(mockMvc, caringP, objectMapper);
        postProgram(mockMvc, gliceP, objectMapper);
        postProgram(mockMvc, graceP, objectMapper);
        postProgram(mockMvc, hydee_obsP, objectMapper);
        postProgram(mockMvc, iconP, objectMapper);
        postProgram(mockMvc, iopdP, objectMapper);
        postProgram(mockMvc, rosebudP, objectMapper);
        postProgram(mockMvc, senergyP, objectMapper);
        postProgram(mockMvc, sinesP, objectMapper);
        postProgram(mockMvc, slogaro_iiP, objectMapper);
        postProgram(mockMvc, talpro2022P, objectMapper);
        postProgram(mockMvc, visitP, objectMapper);

        postCruise(mockMvc, cable, objectMapper);
        postCruise(mockMvc, carbo_acid, objectMapper);
        postCruise(mockMvc, erodoto, objectMapper);
    //    postCruise(mockMvc, figure, objectMapper); //TODO removed for the time being
        postCruise(mockMvc, glice, objectMapper);
        postCruise(mockMvc, grace, objectMapper);
        postCruise(mockMvc, hydee_obs, objectMapper);
        postCruise(mockMvc, icon, objectMapper);
        postCruise(mockMvc, iopd, objectMapper);
        postCruise(mockMvc, rosebud, objectMapper);
        postCruise(mockMvc, senergy, objectMapper);
        postCruise(mockMvc, sines, objectMapper);
        postCruise(mockMvc, slogaro_ii, objectMapper);
        postCruise(mockMvc, talpro2022, objectMapper);
        postCruise(mockMvc, visit, objectMapper);

    }

}
