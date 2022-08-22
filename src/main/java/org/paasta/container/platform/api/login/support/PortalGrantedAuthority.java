package org.paasta.container.platform.api.login.support;

import org.paasta.container.platform.api.common.Constants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class PortalGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 21301223123L;
    private final String id;
    private final String type;
    private final String role;
    private final String token;
    private final String url;
    private final String parentId;


    public PortalGrantedAuthority(String id, String type, String role, String token, String url, String parentId) {
        Assert.hasText(id, "A granted authority id is required");
        Assert.hasText(type, "A granted authority type is required");
        Assert.hasText(role, "A granted authority role is required");
        Assert.hasText(token, "A granted authority token is required");
        this.id = id;
        this.type = type;
        this.role = role;
        this.token = token;
        this.url = url;
        this.parentId = parentId;
    }


    public PortalGrantedAuthority(String id, String type, String role, String token, String url) {
        Assert.hasText(id, "A granted authority id is required");
        Assert.hasText(type, "A granted authority type is required");
        Assert.hasText(role, "A granted authority role is required");
        Assert.hasText(token, "A granted authority token is required");
        this.id = id;
        this.type = type;
        this.role = role;
        this.token = token;
        this.url = url;
        this.parentId = Constants.EMPTY_STRING;
    }

    public PortalGrantedAuthority(String id) {
        Assert.hasText(id, "A granted authority id is required");
        this.id = id;
        this.type = Constants.ContextType.CLUSTER.name();
        this.role = Constants.EMPTY_STRING;
        this.token = Constants.EMPTY_STRING;
        this.url = Constants.EMPTY_STRING;
        this.parentId = Constants.EMPTY_STRING;
    }

    public PortalGrantedAuthority(String id, String type, String role) {
        Assert.hasText(type, "A granted authority type is required");
        Assert.hasText(id, "A granted authority id is required");
        Assert.hasText(role, "A granted authority role is required");
        this.id = id;
        this.type = type;
        this.role = role;
        this.token = Constants.EMPTY_STRING;
        this.url = Constants.EMPTY_STRING;
        this.parentId = Constants.EMPTY_STRING;
    }

    public String getId() {
        return this.id;
    }

    public String getAuthority() {
        return this.role;
    }

    public String getToken() {
        return this.token;
    }

    public String geturl() {
        return this.url;
    }

//    public String getToken(String id) {
//        if(this.id.equals(id))  return this.token;
//        return null;
//    }

    public String getAuthority(String id) {
        if(this.id.equals(id))  return this.role;
        return null;
    }

    public boolean equals(String id, String type) {
        return this.id.equals(id) && this.type.equals(type);
    }

    public boolean equals(String id) {
        return this.id.equals(id);
    }

    public boolean equals(String id, String parentId, String type) {
        return this.id.equals(id) && this.parentId.equals(parentId) && this.type.equals(type);
    }

    public int hashCode() {
        return this.role.hashCode();
    }

    public String toString() {
        return "id: " + this.id
                + ", role: " + this.role
                + ", type: " + this.type
                + ", token: " + this.token
                + ", url: " + this.url
                + ", parentId: " + this.parentId ;
    }
}
