package org.boomzin.votehub.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalTime;

public class TimeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String theMethod = request.getMethod();
        boolean isNotAllowedTime = LocalTime.now().isAfter(LocalTime.of(11, 0));

        if (isNotAllowedTime & (HttpMethod.PUT.matches(theMethod) || HttpMethod.DELETE.matches(theMethod))) {
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value());
            return false;
        }
        else {
            return true;
        }
    }
}