package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongTest {
    // Long타입의 객체는 Heap영역의 -128 ~ +127 까지 캐싱한다.
    // 128이후의 값을 == 비교 시 주소가 다르기 때문에 false
    // 동등 == 연산 시에는 equalse()로 연산
    // 대소 >= 연산 시에는 longValue() 또는 compareTo()로 연산

    @Test
    public void long_test4() throws Exception {
        // given
        Long v1 = 1270L;
        Long v2 = 1280L;

        // when
        if (v1 >= v2) {
            System.out.println("v1이 크다");
        }
        // then
    }

    @Test
    public void long_test3() throws Exception {
        // given
        Long v1 = 1280L;
        Long v2 = 1280L;

        // when
        if (v1.equals(v2)) {
            System.out.println("asd");
        }
        // then
        assertThat(v1).isEqualTo(v2);
        // Eqauls()비교는 문제 없음
    }

    @Test
    public void long_test2() throws Exception {
        // given (2의 8승 - 256 범위 (-128 +127))
        Long v1 = 127L;
        Long v2 = 127L;

        // when
        if (v1 == v2) {
            System.out.println("테스트 : v1이 작습니다.");
        }

        // then
    }

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
