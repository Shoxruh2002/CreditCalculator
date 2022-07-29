package uz.sh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uz.sh.entity.InputAmounts;
import uz.sh.entity.OutPutAmounts;
import uz.sh.entity.Payment;
import uz.sh.enums.CreditType;
import uz.sh.service.BaseService;

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
    public String index(Model model) {
        InputAmounts inputAmounts = new InputAmounts(100000000L, 12, 20D, CreditType.ANNUTED);
        model.addAttribute("inputAmount", inputAmounts);
        List<Payment> payments = service.getPaymentList(inputAmounts);
        model.addAttribute("payments", payments);
        OutPutAmounts outputAmounts = service.calculate(inputAmounts);
        model.addAttribute("outputAmounts", outputAmounts);
        return "index";
    }

    @PostMapping("/")
    public String index(Model model, @ModelAttribute InputAmounts inputAmounts) {
        model.addAttribute("inputAmount", inputAmounts);
        OutPutAmounts outputAmounts = service.calculate(inputAmounts);
        List<Payment> payments = service.getPaymentList(inputAmounts);
        model.addAttribute("outputAmounts", outputAmounts);
        model.addAttribute("payments", payments);
        return "index";
    }

}
