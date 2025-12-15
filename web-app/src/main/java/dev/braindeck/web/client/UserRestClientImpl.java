package dev.braindeck.web.client;

import dev.braindeck.web.entity.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
public class UserRestClientImpl implements UserRestClient {

    private final RestClient restClient;

    @Override
    public UserDto get(){
        return restClient
                .get()
                .uri("/api/users/me")
                .retrieve()
                .body(UserDto.class);
    }

}
