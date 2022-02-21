package uz.sqb.session.service;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.sqb.session.model.SMS;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SMSService {

    @Value("${spring.twilio.account-sid}")
    String ACCOUNT_SID;

    @Value("${spring.twilio.account-token}")
    String AUTH_TOKEN;

    @Value("${spring.twilio.phone-number}")
    String FROM_NUMBER;

    public void send(SMS sms){

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new PhoneNumber(sms.getTo()),
                new PhoneNumber(FROM_NUMBER),
                sms.getBody()
        ).create();

        System.out.println("Here is my id: " + message.getSid());
    }

    public void checkingSMSStatus(String messageSid){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        ResourceSet<Message> messages = Message.reader().read();
        for (Message message : messages){
            System.out.println(message.getSid() + " : " + message.getStatus());
        }
    }


}
