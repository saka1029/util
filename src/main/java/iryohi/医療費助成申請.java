package iryohi;

import java.time.LocalDate;

public record 医療費助成申請(
    LocalDate 申請日,
    String 医療機関,
    LocalDate 受診日,
    String 診療科,
    int 金額
    ) {
}
