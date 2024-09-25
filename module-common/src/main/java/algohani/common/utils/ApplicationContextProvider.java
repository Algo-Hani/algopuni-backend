package algohani.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * <h2>ApplicationContextProvider</h2>
 *
 * <p>ApplicationContext를 제공하는 클래스</p>
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    
    private static class ApplicationContextHolder {

        private static final InnerContextResource CONTEXT_PROV = new InnerContextResource();

        private ApplicationContextHolder() {
            super();
        }
    }

    private static final class InnerContextResource {

        private ApplicationContext context;

        private InnerContextResource() {
            super();
        }

        private void setContext(ApplicationContext context) {
            this.context = context;
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.CONTEXT_PROV.setContext(applicationContext);
    }

    /**
     * ApplicationContext를 반환한다.
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return ApplicationContextHolder.CONTEXT_PROV.context;
    }

    /**
     * 현재 활성화된 프로필을 반환한다.
     *
     * @return 활성화된 프로필(dev, prod)
     */
    public static String getActiveProfile() {
        String[] activeProfiles = getApplicationContext().getEnvironment().getActiveProfiles();
        return activeProfiles.length > 0 ? activeProfiles[0] : "dev";
    }
}
