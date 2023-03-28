package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

// java.util.regex.Pattern
public class RegexTest {
    @Test
    public void 한글만된다_test() throws Exception {
        String value ="가나";
        // +는 공백안됨(최소 1번이상 반복), *는 공백허용
        boolean result = Pattern.matches("^[가-힣]+$", value); // 한글로 시작하고 한글로 끝
        Assertions.assertTrue(result);
    }

    @Test
    public void 한글은안된다_test() throws Exception {
        String value ="D%$";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]+$", value);
        Assertions.assertTrue(result);
    }

    @Test
    public void 영어만된다_test() throws Exception {
        String value ="asd";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);
        Assertions.assertTrue(result);
    }

    @Test
    public void 영어는안된다_test() throws Exception {
        String value ="ㄱ%$";
        boolean result = Pattern.matches("^[^a-zA-Z]+$", value);
        Assertions.assertTrue(result);
    }

    @Test
    public void 영어와숫자만된다_test() throws Exception {
        String value ="asd123a123";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
        Assertions.assertTrue(result);
    }

    @Test
    public void 영어만되고_길이는최소2최대4이다_test() throws Exception {
        String value ="asda";
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value); // 최소 1번 이상인 + 대신, + 자리에 {}안에 최소, 최대 길이 지정
        Assertions.assertTrue(result);
    }

    // username, email, fullname
    @Test
    public void user_username_test() throws Exception {
        String username ="ssar";
        // 영문 숫자 2~20자 이내
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username);
        Assertions.assertTrue(result);
    }

    @Test
    public void user_fullname_test() throws Exception {
        String username ="ssa가r";
        // 영문, 한글 1~20
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", username);
        Assertions.assertTrue(result);
    }

    @Test
    public void user_email_test() throws Exception {
        String email ="ssar@nate.com";
        // 연습겸 직접 썻지만, 이메일은 validation은 라이브러리 사용하는거 권장
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", email);
        // 영어, 숫자가 2글자에서 10글자 후 골뱅이(@), @ 후에 영어, 숫자 2글자 에서 6글자 후 점(.)후에 영어 2,3글자
        // com만 있다고 가정, 완벽한 이메일정규표현식은 아님
        Assertions.assertTrue(result);
    }

    //----- 계좌 입금 Req 객체 테스트
    @Test
    public void account_gubun_test1() throws Exception {
        String gubun = "DEPOSIT";
        boolean result = Pattern.matches("^(DEPOSIT)$", gubun);
        Assertions.assertTrue(result);
    }

    @Test
    public void account_gubun_test2() throws Exception {
        String gubun = "TRANSFER";
        boolean result = Pattern.matches("^(DEPOSIT|TRANSFER)$", gubun);
        Assertions.assertTrue(result);
    }

    @Test
    public void account_tel_test() throws Exception {
        String tel = "010-2729-3256";
        boolean result = Pattern.matches("^[0-9]{3}-[0-9]{4}-[0-9]{4}", tel);
        Assertions.assertTrue(result);
    }

    @Test
    public void account_tel_test2() throws Exception {
        String tel = "01027293256";
        boolean result = Pattern.matches("^[0-9]{11}", tel);
        Assertions.assertTrue(result);
    }

}
