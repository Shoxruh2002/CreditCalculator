package uz.sh.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uz.sh.entity.InputAmounts;
import uz.sh.entity.OutPutAmounts;
import uz.sh.entity.Payment;
import uz.sh.enums.CreditType;
import uz.sh.service.BaseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

/**
 * Author: Bekpulatov Shoxruh
 * Date: 27/07/22
 * Time: 00:27
 */
@Controller
public class BaseController {

    private final BaseService service;

    public BaseController(BaseService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ModelAndView index(Model model) {
        ModelAndView mov = new ModelAndView("index");
        InputAmounts inputAmounts = new InputAmounts(100000000L, 12, 20D, CreditType.ANNUTED);
        mov.addObject("inputAmount", inputAmounts);
        List<Payment> payments = service.getPaymentList(inputAmounts);
        mov.addObject("payments", payments);
        OutPutAmounts outputAmounts = service.calculate(inputAmounts);
        mov.addObject("outputAmounts", outputAmounts);
        return mov;
    }

    @PostMapping("/")
    public ModelAndView index(Model model, @ModelAttribute InputAmounts inputAmounts) {
        ModelAndView mov = new ModelAndView("index");
        mov.addObject("inputAmount", inputAmounts);
        OutPutAmounts outputAmounts = service.calculate(inputAmounts);
        List<Payment> payments = service.getPaymentList(inputAmounts);
        mov.addObject("outputAmounts", outputAmounts);
        mov.addObject("payments", payments);
        return mov;
    }

    @PostMapping("/getPdf")
    public void downloadPDFResource(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @ModelAttribute InputAmounts inputAmount) {

        byte[] byteArray = service.generateFile(inputAmount);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "filename=\"THE FILE NAME\"");
        response.setContentLength(byteArray.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(byteArray, 0, byteArray.length);
        } catch (Exception excp) {
            excp.printStackTrace();
        }

    }


}
