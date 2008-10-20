package playground.wrashid.PHEV.Triangle;

import java.util.ArrayList;
import java.util.Iterator;

import org.matsim.basic.v01.BasicLeg;
import org.matsim.basic.v01.IdImpl;
import org.matsim.facilities.Activity;
import org.matsim.facilities.Facilities;
import org.matsim.facilities.Facility;
import org.matsim.facilities.MatsimFacilitiesReader;
import org.matsim.gbl.Gbl;
import org.matsim.population.Knowledge;
import org.matsim.population.Leg;
import org.matsim.population.Person;
import org.matsim.population.Plan;
import org.matsim.population.Population;
import org.matsim.population.PopulationWriter;
import org.matsim.world.World;

public class CreatePlans1 {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO: am schluss alle meiste pfade in config.xml reintun...
		Population plans = new Population(false);
		Gbl.reset();
		args=new String[1];
		args[0]="C:/data/SandboxCVS/ivt/studies/triangle/config/config.xml";
		Gbl.createConfig(args);
		Gbl.getConfig().plans().setOutputFile("C:/data/SandboxCVS/ivt/studies/triangle/plans/500plans/plans_hwsh.xml");
		final World world = Gbl.getWorld();

		// read facilities
		Facilities facilities = (Facilities)world.createLayer(Facilities.LAYER_TYPE,null);
		new MatsimFacilitiesReader(facilities).readFile("C:/data/SandboxCVS/ivt/studies/triangle/facilities/facilities.xml");


		// get home and work activity
		Activity home=null;
		Activity work=null;
		Activity shop=null;
		for (Facility f : facilities.getFacilities().values()) {
			Iterator<Activity> a_it = f.getActivities().values().iterator();
			while (a_it.hasNext()) {
				Activity a = a_it.next();
				//System.out.println(a.getType());
				if (a.getType().equals("home")) {
					home=a;
				} else if (a.getType().equals("work")){
					work=a;
				} else if (a.getType().equals("shop")){
					shop=a;
				}
			}
		}






		// create persons
		for (int i=0;i<500;i++){
			Person person = new Person(new IdImpl(i));
			plans.addPerson(person);


			Knowledge k = person.createKnowledge("");
			k.addActivity(home,false);
			k.addActivity(work,false);
			k.addActivity(shop,false);

			Plan plan = person.createPlan(true);
			Facility home_facility = person.getKnowledge().getActivities("home").get(0).getFacility();
			Facility work_facility = person.getKnowledge().getActivities("work").get(0).getFacility();
			Facility shop_facility = person.getKnowledge().getActivities("shop").get(0).getFacility();
			ArrayList<Activity> acts = person.getKnowledge().getActivities();

			double depTimeHome=3600*8;
			double depTimeWork=3600*16;
			double depTimeShop=3600*17.5;
			double mitterNacht=3600*24;
			double duration=3600*8;

			
			// home: 0:00-8:00
			// work: 8-16
			// shop: 16-17.30
			// home: 17.30-0:00
			
			plan.createAct("home",home_facility.getCenter().getX(),home_facility.getCenter().getY(),home_facility.getLink(),0.0,depTimeHome,depTimeHome,false);
			plan.createLeg(BasicLeg.Mode.car,depTimeHome,0.0,depTimeHome);
			plan.createAct("work",work_facility.getCenter().getX(),work_facility.getCenter().getY(),work_facility.getLink(),depTimeHome,depTimeWork,depTimeWork-depTimeHome,false);
			plan.createLeg(BasicLeg.Mode.car,depTimeWork,0.0,depTimeWork);
			plan.createAct("shop",shop_facility.getCenter().getX(),shop_facility.getCenter().getY(),shop_facility.getLink(),depTimeWork,depTimeShop,depTimeShop-depTimeWork,false);
			plan.createLeg(BasicLeg.Mode.car,depTimeShop,0.0,depTimeShop);
			plan.createAct("home",home_facility.getCenter().getX(),home_facility.getCenter().getY(),home_facility.getLink(),depTimeShop,mitterNacht,mitterNacht-depTimeShop,false);
			// assign home-work-home activities to each person


			Leg l=null;

		}



		new PopulationWriter(plans).write();
	}

}
