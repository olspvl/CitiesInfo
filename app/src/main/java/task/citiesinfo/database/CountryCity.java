package task.citiesinfo.database;

public class CountryCity {
    private String country;
    private String city;

    public CountryCity(String country, String city) {
        this.country = country;
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

}