package com.bookingWebAppApi.Utility;

import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bookingWebAppApi.Model.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JWTTokenProvider {
	
	@Value("${jwt.secret}")
    private String secret;

    /* method to generate the token */

    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(SecurityConstant.VALENCE_DIRECT_BOOKING_RENTALS)
                .withAudience(SecurityConstant.VALENCE_DIRECT_BOOKING_RENTALS_ADMIN)
                .withIssuedAt(new Date()).withSubject(userPrincipal.getUsername())
                .withArrayClaim(SecurityConstant.AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret));
    }



    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        List<String> authorities = new ArrayList<>();
        for(GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }

        return authorities.toArray(new String[0]);
    }

    /* End of generate the token */



    /** To get the authorities from the token **/

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token)
                .getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);

    }


    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm)
                    .withIssuer(SecurityConstant.VALENCE_DIRECT_BOOKING_RENTALS).build();
        }catch(JWTVerificationException exception) {
            throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);
        }

        return verifier;

    }



    /** End of getting authorities from the token **/



    /** Returning the authentication of the user **/

    public Authentication getAuthentication(String username,
                                            List<GrantedAuthority> authorities, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return usernamePasswordAuthToken;

    }


    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return !username.isEmpty() && !username.equals(" ") && !isTokenExpired(verifier, token);
    }


    public boolean isTokenExpired(JWTVerifier verifier, String token) {

        Date expiration = verifier.verify(token).getExpiresAt();

        return expiration.before(new Date());
    }


    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

}
