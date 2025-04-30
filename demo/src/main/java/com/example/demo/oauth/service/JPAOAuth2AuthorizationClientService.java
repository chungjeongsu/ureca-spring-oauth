package com.example.demo.oauth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

/**
 * 쥐엔~~장 이건 또 뭘까?
 * JPAOAuth2AuthorizationClientService이다.
 * => AccessToken을 받아오고, 로드하고 등등을 하는 클라쓰인겨~
 */

@Service
public class JPAOAuth2AuthorizationClientService implements OAuth2AuthorizedClientService {

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId,
                                                                     String principalName) {

    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {

    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {

    }
}
