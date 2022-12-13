package mustodo.backend.auth.ui.interceptor;

import mustodo.backend.user.domain.User;
import mustodo.backend.exception.auth.NotAuthorizedException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static mustodo.backend.auth.ui.AuthController.LOGIN_SESSION_ID;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();

        if (session != null) {
            User user = (User) session.getAttribute(LOGIN_SESSION_ID);
            if (user != null && user.isAuthorizedUser()) {
                return true;
            }
        }
        throw new NotAuthorizedException();
    }
}
