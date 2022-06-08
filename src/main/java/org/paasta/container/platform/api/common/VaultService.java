package org.paasta.container.platform.api.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.HashMap;
import java.util.Map;

@Service
public class VaultService {

    Logger logger = LoggerFactory.getLogger(VaultService.class);

    @Autowired
    VaultTemplate vaultTemplate;

    /**
     * Vault read를 위한 method
     *
     * @param path the path
     * @return the object
     */
    public Object read(String path) {
        VaultResponse vaultResponse;

        path = setPath(path);

        try {
            vaultResponse = vaultTemplate.read(path);
        }
        catch (Exception e){
            logger.info("Invalid path");
            return null;
        }
        return vaultResponse.getData().get("data");
    }

    /**
     * Vault write를 위한 method
     *
     * @param path the path
     * @return the object
     */
    public Object write(String path, Object body){
        path = setPath(path);

        Map<String, Object> data = new HashMap<>();
        data.put("data", body);

        return vaultTemplate.write(path, data);
    }

    /**
     * Vault delete를 위한 method
     *
     * @param path the path
     * @return the object
     */
    public void delete(String path){
        path = setPath(path);
        vaultTemplate.delete(path);
    }

    /**
     * Vault path 처리 를 위한 method
     *
     * @param path the path
     * @return the String
     */
    String setPath(String path){
        return new StringBuilder(path).insert(path.indexOf("/") + 1, "data/").toString();
    }
}
