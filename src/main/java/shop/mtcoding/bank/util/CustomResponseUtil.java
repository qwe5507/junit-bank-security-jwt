package shop.mtcoding.bank.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shop.mtcoding.bank.dto.ResponseDto;

import javax.servlet.http.HttpServletResponse;

public class CustomResponseUtil {
    private static final Logger log = LoggerFactory.getLogger(CustomResponseUtil.class);

    public static void success(HttpServletResponse response, Object dto) {
        try {
            ObjectMapper om = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(1, "로그인성공", dto);
            String repsonseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(200);
            response.getWriter().println(repsonseBody);
        } catch (Exception exception) {
            log.error("서버 파싱 에러");
        }
    }

    public static void unAuthentication(HttpServletResponse response, String msg) {
        try {
            ObjectMapper om = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(-1, msg, null);
            String repsonseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(401);
            response.getWriter().println(repsonseBody);
        } catch (Exception exception) {
            log.error("서버 파싱 에러");
        }
    }
}
