package org.container.platform.api.clusters.sshKeys;

import org.container.platform.api.common.*;
import org.container.platform.api.common.model.Params;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class SshKeysServiceTest {

    private static SshKeys gResultModel = null;
    private static SshKeys gFinalResultModel = null;

    private static SshKeysList gResultListModel = null;
    private static SshKeysList gFinalResultListModel = null;
    private static String gskPath = null;
    private static Params gParams = null;

    @Mock
    private RestTemplateService restTemplateService;
    @Mock
    private PropertyService propertyService;
    @Mock
    private CommonService commonService;
    @Mock
    private VaultService vaultService;
    @InjectMocks
    private SshKeysService sshKeysService;


    @Before
    public void setUp() {
        gParams = new Params();
        gParams.setProviderType(Constants.ProviderType.AWS);
        gParams.setResourceName("resourceName");
        gParams.setResourceUid("resourceUid");
        gParams.setPrivateKey("-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEowIBAAKCAQEAk2bosnrqbJcwlXc84UXHSmwci+5H/dqDGADaI0WYEGJdupeD\n" +
                "P4Wgmx5XMmCWUYc+hEvU8gho5Bx1MrEiOzx131HeoHX1dAJXqg4LAuktHTrV/1RX\n" +
                "LW6PQwZa6pA+Jroew/gR+tMkb56U4IkVjnh5fdos/OUpjCreBkBlr3ugrdlc2ZIx\n" +
                "u8Q1fyPonSxaXbENWdXBpg0wx95UuD4haRqgaosFsBS+RmtIEVTDY07QnNF04NOV\n" +
                "K2zq6k1/Ek9Y30OsBI56CbubUjK4VPiD+wPkkTz6MkubMSDcifuTtHTRpPkbDRjI\n" +
                "/PP6jTfd092a2TC9eivZSptSPoBGIOypxr4hqQIDAQABAoIBAEBdrbtSZU+p7bM2\n" +
                "6N2wB7jxPhj8/8WKwyKo6QKBgQCU6FUvf35nj3iSZfTZAfs69y6XaOBk12GbZzYx\n" +
                "hgoYW3S2HMjOoWga+GxJk33RN1pVRsu3X/N1dqdOiGrDPZY0DOXC7hQPUECA8Xmt\n" +
                "7t37YskgmmLoF9Oefi+zAC6osPaJJ0UU2p0UW6ErM0uwKXcOLL9Me3/aQOyZoY6I\n" +
                "lARB/QKBgHSl8OkMGjFw6LUVIDqbld62o299gNrBvSTEtYCnyaHTjslOR74KEMGA\n" +
                "6hNVrfbG3BHcnpXW+6M6xJqv9SiktnFeAyMAZMZJ03O2+1kNOR1mtCx4d8syXftj\n" +
                "CYSg7Rcy5cDc2JRXdAHFwdiBwpDX12ZveVZOhakzbsyVdVz9FPbc\n" +
                "-----END RSA PRIVATE KEY-----\n");

        gResultModel = new SshKeys();
        gFinalResultModel = new SshKeys();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gskPath = "gskPath";

        List<SshKeys> listItemModel = new ArrayList<>();
        listItemModel.add(gResultModel);

        gResultListModel = new SshKeysList();
        gResultListModel.setItems(listItemModel);
        gFinalResultListModel = new SshKeysList();
        gFinalResultListModel.setItems(listItemModel);
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
    }
    @Test
    public void getSshKeys() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/{id:.+}"
                .replace("{id:.+}", gParams.getResourceUid()), HttpMethod.GET, null, SshKeys.class, gParams)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        SshKeys result = sshKeysService.getSshKeys(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getSshKeysList() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys", HttpMethod.GET, null, SshKeysList.class, gParams)).thenReturn(gResultListModel);
        when(commonService.globalListProcessing(gResultListModel, gParams, SshKeysList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        SshKeysList result = sshKeysService.getSshKeysList(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getSshKeysListByProvider() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/provider/{providerType:.+}"
                .replace("{providerType:.+}", gParams.getProviderType().name()), HttpMethod.GET, null, SshKeysList.class, gParams)).thenReturn(gResultListModel);
        when(commonService.globalListProcessing(gResultListModel, gParams, SshKeysList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        SshKeysList result = sshKeysService.getSshKeysListByProvider(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void createSshKeys() {
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys", HttpMethod.POST, new SshKeys(), SshKeys.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);
    }

    @Test
    public void modifyInitSshKeys() {
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys", HttpMethod.PUT, new SshKeys(), SshKeys.class, gParams), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);
    }

    @Test
    public void deleteSshKeys() {
        when(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/{id:.+}"
                .replace("{id:.+}", gParams.getResourceUid()), HttpMethod.GET, null, SshKeys.class, gParams)).thenReturn(gFinalResultModel);
        vaultService.delete("/secret/ssh-key/1");
        when(commonService.setResultModel(restTemplateService.sendGlobal(Constants.TARGET_COMMON_API, "/sshKeys/{id:.+}"
                        .replace("{id:.+}", gParams.getResourceUid()), HttpMethod.DELETE, null, SshKeys.class, gParams),
                Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);
    }

}