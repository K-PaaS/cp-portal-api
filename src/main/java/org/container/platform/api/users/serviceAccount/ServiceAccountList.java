package org.container.platform.api.users.serviceAccount;

import lombok.Data;

import java.util.List;

/**
 * Service Account List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.12.07
 **/
@Data
public class ServiceAccountList {
    private String apiVersion;
    private String kind;
    private List<ServiceAccount> items;
}
