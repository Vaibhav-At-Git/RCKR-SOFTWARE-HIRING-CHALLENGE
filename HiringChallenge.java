import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/****
 * This is the solution to the hiring challenge of RCKR softwares.
 * 
 * Question: Find latitude and longitude of first 20 countries with a population
 * greater than or equal to the population limit given below. Use the country
 * details from this dataset. Your task is to find the sum of the length of all
 * lines (in kms) that can be drawn between co-ordinates of these countries.
 * Assume radius of earth: 6371 km Round length of each line and final result to
 * 2 decimal points If co-ordinates are missing for any country use 0.000 N
 * 0.000 E.
 * 
 * Population limit: Input by the user.
 * 
 */

class HiringChallenge {

    // function to return the shortest distance b/w 2 points on the sphere using
    // haversine formulae

    static double getDistance(double lat1, double lon1, double lat2, double lon2) {

        // Latitude and longitude for 2 places.

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));

        // distance return is in kilometers
        return rad * c;
    }

    public static void main(String[] args) throws Exception {

        // READ THE JASON DATA.
        String baseUrl = "https://cdn.jsdelivr.net/gh/apilayer/restcountries@3dc0fb110cd97bce9ddf27b3e8e1f7fbe115dc3c/src/main/resources/countriesV2.json";

        JSONArray json = new JSONArray(IOUtils.toString(new URL(baseUrl), Charset.forName("UTF-8")));

        /*** Process the jsonObject ***/

        // TotalDistance in kilometers
        double totalDistance = 0;

        // Limit of the Population
        long populationLimit;
        Scanner scanner = new Scanner(System.in);
        populationLimit = scanner.nextLong();
        scanner.close();

        // Make a list of those countries that have population greater than the
        // population limit and append them in a list.
        ArrayList<CountryInfo> countriesList = new ArrayList<>();
        int totalCountries = 0;

        for (int i = 0; i < json.length(); i++) {

            JSONObject countryInfo = json.getJSONObject(i);

            int countryPopulation = countryInfo.getInt("population");

            if (countryPopulation >= populationLimit) {

                String countryName = countryInfo.getString("name");

                JSONArray lat_lng_Info = countryInfo.getJSONArray("latlng");

                double countryLatitude, countryLongitude;

                if (lat_lng_Info != null) {

                    countryLatitude = Double.parseDouble(lat_lng_Info.get(0).toString());

                    countryLatitude = Math.round(countryLatitude * 100.0) / 100.0;

                    countryLongitude = Double.parseDouble(lat_lng_Info.get(1).toString());

                    countryLongitude = Math.round(countryLongitude * 100.0) / 100.0;
                } else {

                    countryLatitude = 0.00;
                    countryLongitude = 0.00;
                }

                countriesList.add(new CountryInfo(countryName, countryPopulation, countryLatitude, countryLongitude));

                totalCountries++;

            }

        }

        System.out.println("total countries that are sorted by population limit : " + totalCountries);

        /***
         * Process the list according to the constraints in the challenge.
         * 
         * 1) Sort the selected countries in ascending order as per their population.
         * 
         * 2) Apply the haversine algorithm to calculate the distance for each pair of
         * the country.
         * 
         * 3) Add the distance to the totalDistance.
         */

        /** SORTING */
        Collections.sort(countriesList, new Comparator<CountryInfo>() {

            @Override
            public int compare(CountryInfo c1, CountryInfo c2) {

                return c1.population - c2.population;
            }

        });

        // Top 20 countries that are sorted according to their population in ascending
        // order.
        int topCountries = 20;

        /**
         * CALCULATE DISTANCE FOR EACH PAIR OF THE COUNTRY USING THEIR LOCATION
         * COORDINATES AND ADD THEM TO THE TOTAL DISTANCE
         */

        for (int i = 0; i < topCountries; i++) {

            double C1_latitude = countriesList.get(i).getLatitude();
            double C1_longitude = countriesList.get(i).getLongitude();

            for (int j = i + 1; j < topCountries; j++) {

                double C2_latitude = countriesList.get(j).getLatitude();
                double C2_longitude = countriesList.get(j).getLongitude();

                totalDistance += getDistance(C1_latitude, C1_longitude, C2_latitude, C2_longitude);

            }

        }

        // Round the total distance upto 2 decimal places

        totalDistance = Math.round(totalDistance * 100.0) / 100.0;

        System.out.println("TotalDistance in KM: " + totalDistance);

    }

}

/****
 * Data that has been processed according to the constraints are templated as
 * the below class.
 */
class CountryInfo {

    String countryName;
    int population;
    double latitude;
    double longitude;

    CountryInfo(String countryName, int population, double latitude, double longitude) {
        this.countryName = countryName;
        this.population = population;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}