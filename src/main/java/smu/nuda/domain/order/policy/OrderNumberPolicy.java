package smu.nuda.domain.order.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OrderNumberPolicy {

    private final JdbcTemplate jdbcTemplate;

    public Long generate() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD

        Long seq = jdbcTemplate.queryForObject(
                "SELECT nextval('seq_order_id')",
                Long.class
        );
        long fixedSeq = seq % 100000; // 5자리를 넘어가면 다시 0부터 시작하도록 보정

        return Long.parseLong(today + String.format("%05d", fixedSeq));
    }
}
