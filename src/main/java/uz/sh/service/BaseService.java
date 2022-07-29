package uz.sh.service;

import org.springframework.stereotype.Service;
import uz.sh.entity.InputAmounts;
import uz.sh.entity.OutPutAmounts;
import uz.sh.entity.Payment;
import uz.sh.enums.CreditType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Bekpulatov Shoxruh
 * Date: 27/07/22
 * Time: 00:11
 */
@Service
public class BaseService {
    private static final DecimalFormat dfSharp = new DecimalFormat("#.##");


    public OutPutAmounts calculate(InputAmounts inputAmounts) {
        CreditType type = inputAmounts.getType();
        switch (type) {
            case ANNUTED -> {
                return calculateAnnuted(inputAmounts);
            }
            case DIFFERENSIAL -> {
                return calculateDifferensial(inputAmounts);
            }
            default -> {
                return new OutPutAmounts();
            }
        }
    }

    private OutPutAmounts calculateDifferensial(InputAmounts inputAmounts) {

        Long qolganQarz = inputAmounts.getCreditAmount();
        OutPutAmounts outPutAmounts = new OutPutAmounts();
        outPutAmounts.setPercentage(inputAmounts.getPercentage());
        outPutAmounts.setMonthlyPayment((qolganQarz / inputAmounts.getMonths()) + Math.round((qolganQarz * inputAmounts.getPercentage()) / 100 / 12));
        Long toliqFoizTulovi = 0L;
        Long qolganTulov = qolganQarz;
        for (int i = 0; i < inputAmounts.getMonths(); i++) {
            toliqFoizTulovi += Math.round((qolganTulov * inputAmounts.getPercentage()) / 100 / 12);
            qolganTulov = qolganTulov - qolganQarz / inputAmounts.getMonths();
        }
        outPutAmounts.setFullPercentagePayment(toliqFoizTulovi);
        outPutAmounts.setOverAllCreditAmount(qolganQarz + toliqFoizTulovi);
        return outPutAmounts;
    }

    private OutPutAmounts calculateAnnuted(InputAmounts inputAmounts) {

        OutPutAmounts outPutAmounts = new OutPutAmounts();

        Double koeffitsient = inputAmounts.getPercentage() / 12 / 100;

        Long qolganQarz = inputAmounts.getCreditAmount();

        Double oylikTulov = qolganQarz.doubleValue() * (koeffitsient + (koeffitsient / (Math.pow(koeffitsient + 1, inputAmounts.getMonths()) - 1)));

        outPutAmounts.setMonthlyPayment(Math.round(oylikTulov));
        outPutAmounts.setPercentage(inputAmounts.getPercentage());
        outPutAmounts.setOverAllCreditAmount(Math.round(inputAmounts.getMonths() * oylikTulov));
        outPutAmounts.setFullPercentagePayment(Math.round(inputAmounts.getMonths() * oylikTulov - qolganQarz));

        return outPutAmounts;
    }

    public List<Payment> getPaymentList(InputAmounts inputAmounts) {

        CreditType type = inputAmounts.getType();
        switch (type) {
            case ANNUTED -> {
                return getPaymentListAnnuted(inputAmounts);
            }
            case DIFFERENSIAL -> {
                return getPaymentListDifferensial(inputAmounts);
            }
            default -> {
                return new ArrayList<>();
            }
        }

    }

    private List<Payment> getPaymentListDifferensial(InputAmounts inputAmounts) {
        List<Payment> list = new ArrayList<>();
        Double asosiyQarz = inputAmounts.getCreditAmount().doubleValue();
        Double asosiyQarzBuyichaTolov = asosiyQarz / inputAmounts.getMonths();

        for (Integer i = 0; i < inputAmounts.getMonths(); i++) {
            Payment payment = new Payment();

            double foizPuli = (asosiyQarz * inputAmounts.getMonths()) / 100 / 12;

            payment.setMonth((i + 1) + "");
            payment.setAsosiyQarzQoldiqi(dfSharp.format(asosiyQarz));
            payment.setFoizTulovi(dfSharp.format(foizPuli));
            payment.setAsosiyQarzBuyichaTolov(dfSharp.format(asosiyQarzBuyichaTolov));
            payment.setJamiOylikTolov(dfSharp.format(asosiyQarzBuyichaTolov + foizPuli));

            asosiyQarz = asosiyQarz - asosiyQarzBuyichaTolov;
            list.add(payment);
        }
        return list;
    }

    private List<Payment> getPaymentListAnnuted(InputAmounts inputAmounts) {
        List<Payment> list = new ArrayList<>();
        Double asosiyQarz = inputAmounts.getCreditAmount().doubleValue();
        Double koeffitsient = inputAmounts.getPercentage() / 12 / 100;
        Double oylikTulov = asosiyQarz * (koeffitsient + (koeffitsient / (Math.pow(koeffitsient + 1, inputAmounts.getMonths()) - 1)));

        for (Integer i = 0; i < inputAmounts.getMonths(); i++) {
            Payment payment = new Payment();
            Double foizPuli = asosiyQarz * koeffitsient;
            payment.setMonth((i + 1) + "");
            payment.setJamiOylikTolov(dfSharp.format(oylikTulov));
            payment.setFoizTulovi(dfSharp.format(foizPuli));
            payment.setAsosiyQarzBuyichaTolov(dfSharp.format(oylikTulov - foizPuli));
            payment.setAsosiyQarzQoldiqi(dfSharp.format(asosiyQarz));

            asosiyQarz = asosiyQarz - oylikTulov + foizPuli;
            list.add(payment);
        }

        return list;
    }
}
