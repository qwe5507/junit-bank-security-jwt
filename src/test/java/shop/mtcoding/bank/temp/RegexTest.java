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
}
