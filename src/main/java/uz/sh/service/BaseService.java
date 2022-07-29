package uz.sh.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import uz.sh.entity.InputAmounts;
import uz.sh.entity.OutPutAmounts;
import uz.sh.entity.Payment;
import uz.sh.enums.CreditType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

    public byte[] generateFile(InputAmounts inputAmounts) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
//
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 3, 3, 5, 3});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

            PdfPCell cell1 = PdfPCell("Oy", headFont);
            table.addCell(cell1);

            PdfPCell cell2 = PdfPCell("Asosiy Qarzning Qoldiqi", headFont);
            table.addCell(cell2);

            PdfPCell cell3 = PdfPCell("Asosiy Qarz bo'yicha to'lov", headFont);
            table.addCell(cell3);

            PdfPCell cell4 = PdfPCell("Foizlarni to'lash", headFont);
            table.addCell(cell4);

            PdfPCell cell5 = PdfPCell("To'lovning umumiy miqdori", headFont);
            table.addCell(cell5);
            List<Payment> paymentList = this.getPaymentList(inputAmounts);
            for (int i = 0; i < paymentList.size(); i++) {
                Payment payment = paymentList.get(i);

//                String orgName = organizationService.getName(payment.getOrganizationId());
//                String compName = companyService.getName(payment.getCompanyId());
//                int year = payment.getDateTime().getYear();
//                int month = payment.getDateTime().getMonthValue();
//                int day = payment.getDateTime().getDayOfMonth();
//                String date = year + "-" + month + "-" + day;

                PdfPCell(table, payment.getMonth());
                PdfPCell(table, payment.getAsosiyQarzQoldiqi());
                PdfPCell(table, payment.getAsosiyQarzBuyichaTolov());
                PdfPCell(table, payment.getFoizTulovi());
                PdfPCell(table, payment.getJamiOylikTolov());
            }

//
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph(Element.ALIGN_JUSTIFIED_ALL, "To'lov tartibi \n\n\n"));
            document.add(table);
            document.close();

        } catch (DocumentException ex) {
            System.out.println("ex = " + ex);
        }

        return out.toByteArray();
    }

    private static PdfPCell PdfPCell(String name, Font headFont) {
        PdfPCell hcell;
        hcell = new PdfPCell(new Phrase(name, headFont));
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hcell.setBackgroundColor(BaseColor.CYAN);
        return hcell;
    }

    private static void PdfPCell(PdfPTable table, String book) {
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(book));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingRight(10);
        table.addCell(cell);
    }
}
