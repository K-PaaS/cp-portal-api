package org.paasta.container.platform.api.users.support;

import lombok.Data;

import java.util.List;

/**
 * User Meta Data Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.30
 */
@Data
public class userMetaData {

private String cluster;
private List<userMetaDataItem> items;
}

@Data
class userMetaDataItem {
    private String userType;
    private List<String> namespace;
}