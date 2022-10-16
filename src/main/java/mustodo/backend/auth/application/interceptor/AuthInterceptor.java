package mustodo.backend.auth.application.interceptor;

import mustodo.backend.auth.domain.User;
import mustodo.backend.enums.error.BasicErrorCode;
import mustodo.backend.enums.response.AuthResponseMsg;
import mustodo.backend.exception.AuthException2;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static mustodo.backend.auth.ui.AuthController.LOGIN_SESSION_ID;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        if (session != null) {
            User user = (User) session.getAttribute(LOGIN_SESSION_ID);
            if (user != null && user.isAuthorizedUser()) {
                return true;
            }
        }
        throw new AuthException2(AuthResponseMsg.NOT_AUTHORIZED_USER_ACCESS, BasicErrorCode.NOT_AUTHORIZED_USER_ACCESS);
    }
}
