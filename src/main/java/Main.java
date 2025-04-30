import library.Aircraft;
import library.Airport;
import library.Flight;
import library.Terminal;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String model = "Boeing";
        Airport airport = Airport.getInstance();
        System.out.println("Count all aircraft witch found model \"" + model + "\": " +
                findCountAircraftWithModelAirbus(airport, model));

        System.out.println("\nList flights, which through two hours: ");
        for (Flight currentFlight : findFlightsLeavingInTheNextHours(airport, 2)) {
            System.out.println("\"" + currentFlight + "\"");
        }

        System.out.println("\nThe number of parked planes in each terminal: " +
                findMapCountParkedAircraftByTerminalName(airport));

        System.out.println("\nFind the nearest flight to the specified terminal - A: " +
                findFirstFlightArriveToTerminal(airport, "A"));


        System.out.println("\nПоиск доступных полос: "); findRunway(airport);


    }

    public static void findRunway(Airport airport) {
        Aircraft aircraftToPark = airport.getAllAircrafts().get(0);
        Terminal terminalA = airport.getTerminals().stream()
                .filter(t -> t.getName().equals("A"))
                .findFirst()
                .orElse(null);
        if (terminalA != null) {
            boolean parked = parkAircraftOnRunway(terminalA, aircraftToPark, "Runway-1");
            System.out.println("Parking aircraft " + aircraftToPark.getModel() + " on runway 'Runway-1': " + (parked ? "Success" : "Failed"));
        }
    }

    public static long findCountAircraftWithModelAirbus(Airport airport, String model) {
        int count = 0;

        for (Aircraft currentAircraft : airport.getAllAircrafts()) {
            if (currentAircraft.getModel().contains(model)) {
                count++;
            }
        }

        return count;
    }

    public static List<Flight> findFlightsLeavingInTheNextHours(Airport airport, int hours) {
        ZonedDateTime zonedTimeNow = Instant.now().atZone(ZoneId.of("Europe/Moscow"));
        ZonedDateTime zonedTimePlusHours = zonedTimeNow.plusHours(hours);

        List<Flight> listFlightsLeavingInTheNextHours = new ArrayList<>();

        for (Terminal currentTerminal : airport.getTerminals()) {
            for (Flight currentFlight : currentTerminal.getFlights()) {
                ZonedDateTime zonedTimeCurrentFlight = currentFlight.getDate()
                        .atZone(ZoneId.of("Europe/Moscow"));
                if (currentFlight.getType().equals(Flight.Type.DEPARTURE) &&
                        !zonedTimeCurrentFlight.isBefore(zonedTimeNow) &&
                        !zonedTimeCurrentFlight.isAfter(zonedTimePlusHours)) {
                    listFlightsLeavingInTheNextHours.add(currentFlight);
                }
            }
        }

        return listFlightsLeavingInTheNextHours;
    }

    public static Map<String, Integer> findMapCountParkedAircraftByTerminalName(Airport airport) {
        Map<String, Integer> mapCountParkedAircraftByTerminalName = new HashMap<>();

        for (Terminal currentTerminal : airport.getTerminals()) {
            mapCountParkedAircraftByTerminalName.put(
                    currentTerminal.getName(), currentTerminal.getParkedAircrafts().size());
        }

        return mapCountParkedAircraftByTerminalName;
    }

    public static Flight findFirstFlightArriveToTerminal(Airport airport, String terminalName) {
        ZonedDateTime zonedDateTimeNow = Instant.now().atZone(ZoneId.of("Europe/Moscow"));

        Set<Flight> sortedFlights = new TreeSet<>((f1, f2) -> {
            int cmp = f1.getDate().compareTo(f2.getDate());
            return cmp;
        });

        for (Terminal currentTerminal : airport.getTerminals()) {
            if (currentTerminal.getName().equals(terminalName)) {

                for (Flight currentFlight : currentTerminal.getFlights()) {
                    ZonedDateTime zonedTimeFlight = currentFlight.getDate()
                            .atZone(ZoneId.of("Europe/Moscow"));
                    if (currentFlight.getType().equals(Flight.Type.ARRIVAL) &&
                            zonedTimeFlight.isAfter(zonedDateTimeNow)) {
                        sortedFlights.add(currentFlight);
                    }
                }
            }
        }

        if (!sortedFlights.isEmpty()) {
            return sortedFlights.iterator().next();
        }

        return null;
    }

    public static boolean parkAircraftOnRunway(Terminal terminal, Aircraft aircraft, String runwayName) {
        Map<String, Aircraft> runwayParking = getRunwayParkingMap(terminal);
        if (runwayParking == null) {
            System.out.println("Runway parking data is not available.");
            return false;
        }

        Aircraft parkedAircraft = runwayParking.get(runwayName);
        if (parkedAircraft != null) {
            return false;
        }

        runwayParking.put(runwayName, aircraft);

        terminal.getParkedAircrafts().add(aircraft);

        return true;
    }

    private static Map<String, Aircraft> getRunwayParkingMap(Terminal terminal) {
        return new HashMap<>();
    }
}
