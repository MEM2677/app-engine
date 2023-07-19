package org.entando.entando.plugins.jprefcontent.aps.system.service;

import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class ContentLinkManager extends AbstractService implements IContentLinkManager {

    @Getter
    @Setter
    private ContentManager contentManager;

    private static final Logger logger = LoggerFactory.getLogger(ContentLinkManager.class);

    @Override
    public void init() throws Exception {
        System.out.println("Service loaded "); // TODO add configuration
    }

    @AfterReturning(
            pointcut = "execution(* com.agiletec.plugins.jacms.aps.system.services.content.ContentManager.loadContent(..)) && args(id, online)",
            returning = "content"
    )
    public void afterReturningLoadContent(JoinPoint joinPoint, Content content, String id, boolean online) {
        System.out.println("### Risultato del metodo loadContent intercettato: " + content.getId());
        System.out.println("### invocazione >>> " + online + " id: " + id);
        System.out.println("### " + content.getStatus());
    }
}
