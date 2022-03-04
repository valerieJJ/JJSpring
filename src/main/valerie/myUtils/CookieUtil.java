package valerie.myUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static String getCookie(HttpServletRequest request, String cookieName){
        Cookie[] cookies =  request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(cookieName)){
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String value){
        Cookie cookie = new Cookie(cookieName,value);
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(30 * 24 * 60 * 60);

        response.addCookie(cookie); // 向客户端发送 Cookie
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName){
        setCookie(request, response,cookieName,null);
    }
}
