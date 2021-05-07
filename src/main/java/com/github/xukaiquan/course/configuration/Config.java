package com.github.xukaiquan.course.configuration;

import com.github.xukaiquan.course.dao.SessionDao;
import com.github.xukaiquan.course.model.HttpException;
import com.github.xukaiquan.course.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.xukaiquan.course.configuration.UserInterceptor.COOKIE_NAME;

@Configuration
public class Config implements WebMvcConfigurer {
    @Autowired
    SessionDao sessionDao;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor(sessionDao));
    }

    public static class UserContext {
        private static ThreadLocal<User> currentUser = new ThreadLocal<>();

        // 获取当前线程上下文的用户，null代表没有登录
        public static User getCurrentUser() {
            return currentUser.get();
        }

        public static User getCurrentUserOr401() {
            User user = currentUser.get();
            if (user == null) {
                throw new HttpException(401, "Not login");
            }
            return user;
        }

        // 为当前线程上下文设置用户，null代表清空当前的用户
        public static void setCurrentUser(User currentUser) {
            UserContext.currentUser.set(currentUser);
        }
    }

    public static Optional<String> getCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        Cookie[] cookies = request.getCookies();

        return Stream.of(cookies).filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst();
    }

}
