package org.paasta.container.platform.api.login;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.users.Users;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.paasta.container.platform.api.common.Constants.TARGET_COMMON_API;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class CustomUserDetailsServiceTest {

    private static Params gParams = null;
    private static UserDetails gUserDetails = null;
    private static Users gUsers = null;
    private static UsersList gUsersList = null;

    @Mock
    UsersService usersService;
    @Mock
    RestTemplateService restTemplateService;
    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Before
    public void setUp() throws Exception {
        gParams = new Params();
        gUserDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return null;
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

            @Override
            public boolean isEnabled() {
                return false;
            }
        };
        gUsers = new Users();
        gUsersList = new UsersList();
        gUsersList.setItems(new ArrayList<>());
    }

    @Test
    public void loadUserByUsername() {
        String userId = "test";
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USER_DETAIL_LOGIN.replace("{userId:.+}", userId)
                , HttpMethod.GET, null, Users.class, new Params())).thenReturn(gUsers);

        customUserDetailsService.loadUserByUsername("test");
    }

    @Test
    public void createAuthenticationResponse() {


        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return new UserDetails() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return new Collection<GrantedAuthority>() {
                            @Override
                            public int size() {
                                return 0;
                            }

                            @Override
                            public boolean isEmpty() {
                                return false;
                            }

                            @Override
                            public boolean contains(Object o) {
                                return false;
                            }

                            @Override
                            public Iterator<GrantedAuthority> iterator() {
                                return null;
                            }

                            @Override
                            public Object[] toArray() {
                                return new Object[0];
                            }

                            @Override
                            public <T> T[] toArray(T[] a) {
                                return null;
                            }

                            @Override
                            public boolean add(GrantedAuthority grantedAuthority) {
                                return false;
                            }

                            @Override
                            public boolean remove(Object o) {
                                return false;
                            }

                            @Override
                            public boolean containsAll(Collection<?> c) {
                                return false;
                            }

                            @Override
                            public boolean addAll(Collection<? extends GrantedAuthority> c) {
                                return false;
                            }

                            @Override
                            public boolean removeAll(Collection<?> c) {
                                return false;
                            }

                            @Override
                            public boolean retainAll(Collection<?> c) {
                                return false;
                            }

                            @Override
                            public void clear() {

                            }
                        };
                    }

                    @Override
                    public String getPassword() {
                        return null;
                    }

                    @Override
                    public String getUsername() {
                        return null;
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

                    @Override
                    public boolean isEnabled() {
                        return false;
                    }
                };
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };

        when(usersService.getMappingClustersListByUser(gParams)).thenReturn(gUsersList);

        customUserDetailsService.createAuthenticationResponse(authentication, gParams);
    }

    @Test
    public void getUsersDetailsForLogin() {
    }
}