package uz.sqb.session.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IpAddressService {

    public String extractIp(HttpServletRequest request){
        String clientIp;
        String clientXForwardedForIp = request.getHeader("x-forwarded-for");
        if (clientXForwardedForIp != null){
            clientIp = clientXForwardedForIp;
        } else {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }


    public String getLocationByIp(String ip) throws IOException, GeoIp2Exception {
        File database = new File("src/main/resources/geo/geo-data.mmdb");
        DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

        InetAddress inetAddress = InetAddress.getByName(ip);
        CityResponse response = dbReader.city(inetAddress);
        System.out.println("Country:" + response.getCountry().getName());
        System.out.println("City:" + response.getCity().getName());
        System.out.println("Postal:" + response.getPostal().getCode());
        System.out.println("SPEC:" + response.getLeastSpecificSubdivision().getName());
        System.out.println("Longitude:" + response.getLocation().getLongitude());
        System.out.println("Latitude:" + response.getLocation().getLatitude());
        return "Country:" + response.getCountry().getName() + "\n" +
                "City:" + response.getCity().getName() + "\n" +
                "Postal:" + response.getPostal().getCode() + "\n" +
                "SPEC:" + response.getLeastSpecificSubdivision().getName() + "\n" +
                "Longitude:" + response.getLocation().getLongitude() + "\n" +
                "Latitude:" + response.getLocation().getLatitude();
    }

    public String macAddressDevice(InetAddress inetAddress) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
        return byteConvertToMac(networkInterface);
    }

    public String byteConvertToMac(NetworkInterface networkInterface) throws SocketException {
        byte[] hardwareAddress = networkInterface.getHardwareAddress();
        String[] hexadecimal = new String[hardwareAddress.length];
        for (int i = 0; i < hardwareAddress.length; i++) {
            hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
        }
        return String.join("-", hexadecimal);
    }

}
