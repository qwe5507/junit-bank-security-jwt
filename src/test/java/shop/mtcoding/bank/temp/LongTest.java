package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;

public class LongTest {


    @Test
    public void long_test() throws Exception {
        // given
        Long number1 = 1111L;
        Long number2 = 1111L;

        // when
        if (number1.longValue() == number2.longValue()) {
            System.out.println("동일");
        } else {
            System.out.println("동일하지 않음");
        }

        // Long타입을 비교할때는 기본형인 long타입으로 변경 후 비교
    }
}
