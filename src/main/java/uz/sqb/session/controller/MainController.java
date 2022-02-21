package uz.sqb.session.controller;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.sqb.session.model.SMS;
import uz.sqb.session.service.IpAddressService;
import uz.sqb.session.service.SMSService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/api")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MainController {

    private static final String GET_AGENT = "/ip";
    private static final String SEND_SMS = "/sms";
    private static final String GET_LOCATION  = "/{ip}";

    final IpAddressService service;
    final SMSService smsService;

    @GetMapping(GET_AGENT)
    public ResponseEntity getAgent(HttpServletRequest request) throws IOException, GeoIp2Exception {
        String remoteAddr = service.extractIp(request);
        String user_agent = request.getHeader("user-agent");
        System.out.println("--------------------");
        System.out.print("USER_AGENT:");
        System.out.println(user_agent);
        System.out.println("--------------------");
        System.out.print("IP_ADDRESS:");
        System.out.println(remoteAddr);
        System.out.println("--------------------");
        System.out.print("LOCATION:");
        InetAddress inetAddress = InetAddress.getByName(remoteAddr);
        String location = service.getLocationByIp(inetAddress.getHostAddress());
        return ResponseEntity.ok("USER_AGENT:" + user_agent + "\n"
                                + "IP_ADDRESS:" + remoteAddr + "\n"
                                + "LOCATION:" + location
        );
    }

    @GetMapping(GET_LOCATION)
    public ResponseEntity getLocation(@PathVariable String ip) throws IOException, GeoIp2Exception {
        String location = service.getLocationByIp(ip);
        return ResponseEntity.ok(location);
    }

    @PostMapping(SEND_SMS)
    public ResponseEntity sendSMS(@RequestBody SMS sms){
        smsService.send(sms);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/")
    public ResponseEntity getLocalIpAndMac() throws UnknownHostException, SocketException {
        InetAddress localhost  = InetAddress.getLocalHost();
        String macAddressDevice = service.macAddressDevice(localhost);
        return ResponseEntity.ok("IP:" + localhost + " MAC:" + macAddressDevice);
    }

    @GetMapping("/test")
    public ResponseEntity send(){
        return ResponseEntity.ok("OK");
    }
}
