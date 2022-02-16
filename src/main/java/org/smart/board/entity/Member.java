package org.smart.board.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member implements UserDetails {
    private String usrid;
    private String usrname;
    private String usrpwd;
    private String email;
    private boolean enabled;
    private String rolename;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.usrpwd;
    }
    /*
    시큐리티에서는 usrname이 id와 같은 역할을 수행하도록 설계돼있다
     */
    @Override
    public String getUsername() {
        return this.usrid;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
}
