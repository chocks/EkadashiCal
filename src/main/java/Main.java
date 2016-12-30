import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Chocks on 12/30/16.
 */
public class Main {
    /**
     * Get integer value of the month
     * @param month
     * @return
     */
    public int getMonth(String month) {
        String[] months = {"January",
                            "February",
                            "March",
                            "April",
                            "May",
                            "June",
                            "July",
                            "August",
                            "September",
                            "October",
                            "November",
                            "December"
                        };

        for(int i = 0; i < months.length; i++) {
            if(months[i].equalsIgnoreCase(month) == true)
                return i;
        }

        return -1;
    }

    /**
     * Process dates file and create iCal events
     * @param filename
     */
    public void ReadNParse(String filename) {
        List<VEvent> events = new ArrayList<VEvent>();
        try {
            FileInputStream fstream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String line;
            int year = Integer.parseInt(br.readLine());
            System.out.println("Year is : " + year);
            int i = 1;
            while((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                Calendar calendar = java.util.Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, getMonth(tokens[1]));
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tokens[0]));

                // initialize a all-day event..
                VEvent ekadashi = new VEvent(new Date(calendar.getTime()), tokens[3]);

                // Generate a UID for the event.
                ekadashi.getProperties().add(new Uid(Integer.toString(i)));
                i++;
                events.add(ekadashi);
            }

            net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
            cal.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
            cal.getProperties().add(CalScale.GREGORIAN);
            cal.getProperties().add(Version.VERSION_2_0);

            for(VEvent ev : events) {
                cal.getComponents().add(ev);
            }

            CalendarOutputter outputter = new CalendarOutputter();
            FileOutputStream fout = new FileOutputStream("out/ekadashi-"+year+".ics");

            outputter.output(cal, fout);
            fstream.close();
            br.close();
            fout.close();
        } catch (FileNotFoundException exp) {
            System.out.println("File Not found! Quitting." + exp.getMessage());
            return;
        } catch (IOException exp) {
            System.out.println("Error reading file..Quitting." + exp.getMessage());
            return;
        } catch (ValidationException e) {
            System.out.println("Error writting ical file..Quitting." + e.getMessage());
            return;
        }
        System.out.println("iCal Generation success!");
    }

    /**
     * Main method
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{

        Main m = new Main();
        m.ReadNParse("resources/2017-dates.txt");
    }
}
