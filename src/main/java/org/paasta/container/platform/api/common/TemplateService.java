package org.paasta.container.platform.api.common;

import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

/**
 * Template Service 클래스
 * 
 * Template 파일의 변수 부분을 치환 후 내용을 완성하여, 해당 내용을 특정한 프로세스에 의해 실행하거나
 * 해당 내용 자체를 반환하는 클래스에 대한 인터페이스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Service
public class TemplateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateService.class);

    private Configuration configuration;

    @Lazy
    @Autowired
    public TemplateService(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.info( "Freemarker's Configuration : {}", CommonUtils.loggerReplace(this.configuration.toString()));
    }


    /**
     * Template 내용 중 일부를 입력받은 모델의 내용으로 치환하여 반환하는 클래스(Return Template that is converted)
     *
     * @param templateName the template name
     * @param model the model
     * @return the String
     */
    public String convert(String templateName, Map<String, Object> model) {
        String yml;
        try {
            yml = FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate(templateName), model);
        } catch (Exception e) {
            return "Occur unexpected exception...";
        }
        return yml;
    }
}
