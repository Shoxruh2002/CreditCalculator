package uz.sh.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Bekpulatov Shoxruh
 * Date: 27/07/22
 * Time: 00:07
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutPutAmounts {

    private Long monthlyPayment;

    private Double percentage;

    private Long fullPercentagePayment;

    private Long overAllCreditAmount;

}
