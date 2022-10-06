package com.oxcentra.warranty.config.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.ws.rs.client.Client;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

@Log4j2
public class ClientConfiguration {
    @Value("${admin.user.username}")
    private String adminUsername;

    @Value("${oauth.refresh.token.expire-time-in-ms}")
    private Long refreshTokenExpireTime;

//    private CacheHandler cacheHandler;
//
//    private AdminTokenService adminTokenService;

//
//    @Autowired
//    public ClientConfiguration(CacheHandler cacheHandler, AdminTokenService adminTokenService)
//    {
//        this.cacheHandler = cacheHandler;
//        this.adminTokenService = adminTokenService;
//    }

    /*@Bean
    public Client feignClient()
    {
        Client trustSSLSockets = new Client.Default(getSSLSocketFactory(), new NoopHostnameVerifier());
        return trustSSLSockets;
    }

    private SSLSocketFactory getSSLSocketFactory()
    {
        try {
            TrustStrategy acceptingTrustStrategy = new TrustStrategy()
            {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException
                {
                    return true;
                }
            };

            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            return sslContext.getSocketFactory();
        } catch (Exception exception) {
        }
        return null;
    }

    @Bean
    AuthInterceptor authFeign()
    {
        return new AuthInterceptor();
    }

    class AuthInterceptor implements RequestInterceptor
    {
        private static final String AUTHORIZATION_HEADER = "Authorization";

        private static final String TOKEN_TYPE = "Bearer";

        @Override
        public void apply(RequestTemplate template)
        {
            template.header("x-api-key", "yX8e4IujIwuWXGYvposamglY0p5H3pq95iq6zyId");

            Date systemDate = Utility.getSystemDate();
            AdminTokenBean tokenBean = cacheHandler.getAdminTokenBean(adminUsername);
            try {
                if (tokenBean != null && tokenBean.getAccessToken() != null) {
                    if (tokenBean.getRefreshTokenExpireDate() != null && systemDate
                            .after(tokenBean.getRefreshTokenExpireDate()))
                    {
                        tokenBean.setAccessToken(adminTokenService.getAccessTokenByLogin());
                    }

                    if (tokenBean.getRefreshTokenExpireDate() != null && tokenBean.getAccessTokenExpireDate() !=
                            null && systemDate.before(tokenBean.getRefreshTokenExpireDate()) && systemDate
                            .after(tokenBean.getAccessTokenExpireDate()))
                    {
                        tokenBean.setAccessToken(adminTokenService.handleRefreshToken(tokenBean.getRefreshToken()));
                    }

                    template.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, tokenBean.getAccessToken()));
                }
            } catch (IOException ex) {
                log.error("Exception  :  ", ex);
            }
        }
    }*/

    /*@Bean
    public ErrorDecoder errorDecoder()
    {
        return new CustomErrorDecoder();
    }*/
}
