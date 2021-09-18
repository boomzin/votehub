package org.boomzin.votehub.util;

import org.boomzin.votehub.error.IllegalRequestDataException;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalTime;

public class TimeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String theMethod = request.getMethod();
        LocalTime now = LocalTime.now();
        boolean isNotAllowedTime = now.isAfter(LocalTime.of(11, 0));

        if (isNotAllowedTime & HttpMethod.PUT.matches(theMethod)) {
            throw new IllegalRequestDataException("change of the voting result is possible only up to 11:00 server time" +
                    ", now " + now.getHour() + ":" + now.getMinute());
        }
        else {
            return true;
        }
    }
}