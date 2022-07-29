package uz.sh.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.sh.enums.CreditType;

/**
 * Author: Bekpulatov Shoxruh
 * Date: 27/07/22
 * Time: 00:03
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InputAmounts {

    private Long creditAmount;

    private Integer months;

    private Double percentage;

    private CreditType type;
}
