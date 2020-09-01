package com.security.jwt.util;

import com.security.jwt.exception.TokenException;
import com.security.jwt.generator.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Utils {

    private final JwtTokenGenerator jwtTokenUtil;

    public String extractJwtTokenFromBearerHeader(String header){

        if(header != null) {
            if(header.startsWith(Keys.BEARER_PREFIX)){
                header = header.substring(7);
                if(!jwtTokenUtil.tokenIsValid(header)){
                    log.error("The token in invalid or expired");
                    throw  new TokenException("error.token.invalid");
                }
                return header;
            }
        }
        log.error("The request do not contain a token");
        throw new TokenException("error.token.notfound");
    }

}
