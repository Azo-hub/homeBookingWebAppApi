package com.homeBookingWebApp.Model;


import static com.homeBookingWebApp.Model.Authority.ADMIN_AUTHORITIES;
import static com.homeBookingWebApp.Model.Authority.OWNER_USER_AUTHORITIES;
import static com.homeBookingWebApp.Model.Authority.TRAVELLER_USER_AUTHORITIES;

public enum Role {
    ROLE_TRAVELLER_USER(TRAVELLER_USER_AUTHORITIES),
    ROLE_OWNER_USER(OWNER_USER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES);
    

    private String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }


    public String[] getAuthorities() {

        return authorities;
    }

}
