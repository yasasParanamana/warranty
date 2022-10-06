package com.oxcentra.warranty.client;


import com.oxcentra.warranty.config.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Service
@FeignClient(value = "email-service", url = "localhost:8082/v1.0/service/supplier",
        configuration = ClientConfiguration.class, fallbackFactory = EmailClientFallbackFactory.class)
public interface EmailClient {

    @PostMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    response getLegalStatusFilteredList(@RequestBody request legalStatusDTO);
}

@Component
class EmailClientFallbackFactory implements FallbackFactory<EmailClient> {

    @Override
    public EmailClient create(Throwable throwable) {
        return new EmailClient() {

            @Override
            public response getLegalStatusFilteredList(request legalStatusDTO) {
                return null;
            }

        };
    }
}
