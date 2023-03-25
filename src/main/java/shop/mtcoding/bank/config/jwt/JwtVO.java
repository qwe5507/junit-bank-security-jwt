package shop.mtcoding.bank.config.jwt;

/**
 * SECRET은 노출 X (AWS, 환경변수, 파일에 저장 해야 함)
 * 리플래시 토큰(X)
 */
public class JwtVO {
    public static final String SECRET = "메타코딩"; //HS256(대칭키)

    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //만료시간, 1주일

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER = "Authorization";
}
