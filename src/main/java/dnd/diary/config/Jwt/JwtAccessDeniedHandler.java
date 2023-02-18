package dnd.diary.config.Jwt;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

   @Override
   public void handle(HttpServletRequest request,
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
   }
}
