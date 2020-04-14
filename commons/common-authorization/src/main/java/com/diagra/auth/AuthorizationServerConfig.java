package com.diagra.auth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.KeyPair;

@Configuration
@EnableAuthorizationServer
@PropertySource("classpath:application-auth.properties")
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final int accessTokenSeconds;
    private final int refreshTokenSeconds;
    private final String clientId;
    private final String clientSecret;
    private final AuthenticationManager authenticationManager;
    private final KeyPair keyPair;

    public AuthorizationServerConfig(
            @Value("${accessTokenSeconds}") int accessTokenSeconds,
            @Value("${refreshTokenSeconds}") int refreshTokenSeconds,
            @Value("${clientId}") String clientId,
            @Value("${clientSecret}") String clientSecret,
            @Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager,
            KeyPair keyPair
    ) {
        this.accessTokenSeconds = accessTokenSeconds;
        this.refreshTokenSeconds = refreshTokenSeconds;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authenticationManager = authenticationManager;
        this.keyPair = keyPair;
    }

    @Bean
    public JwtAccessTokenConverter tokenEnhancer() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair);
        return converter;
    }

    @Bean
    public JwtTokenStore tokenStore(JwtAccessTokenConverter tokenEnhancer) {
        return new JwtTokenStore(tokenEnhancer);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore(tokenEnhancer()))
                .accessTokenConverter(tokenEnhancer());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient(clientId).secret(clientSecret).scopes("read", "write")
                .authorizedGrantTypes("password", "refresh_token").accessTokenValiditySeconds(accessTokenSeconds)
                .refreshTokenValiditySeconds(refreshTokenSeconds);

    }

}