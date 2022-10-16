package mustodo.backend.auth.application.resolver;

import mustodo.backend.auth.domain.User;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static mustodo.backend.auth.ui.AuthController.LOGIN_SESSION_ID;

public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginParameter = parameter.hasParameterAnnotation(Login.class);
        boolean isAssignableType = User.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginParameter && isAssignableType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession();

        if (session == null) {
            return null;
        }
        return session.getAttribute(LOGIN_SESSION_ID);
    }
}
