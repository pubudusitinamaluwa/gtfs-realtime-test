import com.google.transit.realtime.GtfsRealtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

public class GTFSRealTimeTest {
    public static void main(String[] args) throws Exception {
        String columns = "stop_sequence,stop_id,schedule_relationship,arrival_delay,arrival_time,arrival_uncertainty,departure_delay,departure_time,departure_uncertainty";

        // Start reading trip updates feed
        URL url = new URL("https://gtfsrt.api.translink.com.au/api/realtime/SEQ/TripUpdates");
        GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream());

        // Initialize PrintWriter to write to file and write columns as the first line
        PrintWriter pr = new PrintWriter(new FileWriter("out/trip_update_" + System.currentTimeMillis() + ".csv", true));
        pr.write(columns);

        // Read each entity in the feed (Process feed)
        for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
            // Check if entity has a trip update. If ture, proceed
            if (entity.hasTripUpdate()) {
                // Extract trip update from the message
                GtfsRealtime.TripUpdate tripUpdate = entity.getTripUpdate();
                // Get all stop time updates in the trip update
                List<GtfsRealtime.TripUpdate.StopTimeUpdate> stopTimeUpdates = tripUpdate.getStopTimeUpdateList();
                // For each stop time update (stu) convert to csv and write to file
                for (GtfsRealtime.TripUpdate.StopTimeUpdate stu : stopTimeUpdates) {
                    GtfsRealtime.TripUpdate.StopTimeEvent arrival = stu.getArrival();
                    GtfsRealtime.TripUpdate.StopTimeEvent departure = stu.getDeparture();
                    String record = stu.getStopSequence() + "," +
                            stu.getStopId() + "," +
                            stu.getScheduleRelationship() + "," +
                            arrival.getDelay() + "," +
                            arrival.getTime() + "," +
                            arrival.getUncertainty() + "," +
                            departure.getDelay() + "," +
                            arrival.getTime() + "," +
                            arrival.getUncertainty();
                    pr.println(record);
                }
            }
        }

        pr.flush();
        pr.close();
    }
}
