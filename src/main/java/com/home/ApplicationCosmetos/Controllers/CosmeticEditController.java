package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.CosmeticProduct;
import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.CosmeticProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/app")
public class CosmeticEditController {
    private String url=""; //хранится url страницы с косметикой для сохранения всех фильтров при переходе туда
    @Autowired
    private CosmeticProductRepo cosmeticProductRepo;

    @GetMapping("edit/{cosmeticProduct}")
    public String viewCosmeticEdit(@AuthenticationPrincipal User user, @PathVariable CosmeticProduct cosmeticProduct, Model model,HttpServletRequest request) {
        url = request.getHeader("Referer");
        model.addAttribute("listOfProducts", cosmeticProductRepo.distinctName(user.getId()));
        model.addAttribute("listOfBrands", cosmeticProductRepo.distinctBrand(user.getId()));
        model.addAttribute("cosmeticProduct", cosmeticProduct);
        model.addAttribute("user", user);
        return "CosmeticEdit";
    }

    @PostMapping("{id}")
    public String editCosmeticProduct(@AuthenticationPrincipal User user,
                                      @Valid CosmeticProduct editCosmeticProduct,
                                      BindingResult bindingResult,
                                      Model model
                                      ) {

        LocalDate date_death;
        if (editCosmeticProduct.getAutopsy_date()!=null && editCosmeticProduct.getAutopsy_date()
                .plusMonths(editCosmeticProduct.getTime_after_opening())
                .isBefore(editCosmeticProduct.getShelf_life()))
            date_death = editCosmeticProduct.getAutopsy_date()
                    .plusMonths(editCosmeticProduct.getTime_after_opening());
        else date_death = editCosmeticProduct.getShelf_life();

        editCosmeticProduct.setDate_death(date_death);
        editCosmeticProduct.setOwner(user);


        if (bindingResult.hasErrors()){
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("editCosmeticProduct", editCosmeticProduct);
        }
        else {
            model.addAttribute("editCosmeticProduct", null);
            cosmeticProductRepo.save(editCosmeticProduct);
            return "redirect:"+url;
        }
        model.addAttribute("listOfProducts", cosmeticProductRepo.distinctName(user.getId()));
        model.addAttribute("listOfBrands", cosmeticProductRepo.distinctBrand(user.getId()));
        model.addAttribute("user", user);
        return "CosmeticEdit";
    }

    //метод удаления записи со средством из БД, из шаблона CosmeticProduct  по нажатию на кнопку
    @PostMapping("/delete/{id}")
    public String deleteCosmeticProduct(@PathVariable Long id, HttpServletRequest request) {
        cosmeticProductRepo.deleteById(id);


        return "redirect:"+request.getHeader("Referer");
    }

    //метод для копирования полностью, выбранного средства
    @PostMapping("/copy/{id}")
    public String copyCosmeticProduct(@AuthenticationPrincipal User user, @PathVariable Long id, HttpServletRequest request) {
        CosmeticProduct currentCosPr = null;
        boolean is_open = false;
        if (cosmeticProductRepo.findById(id).isPresent()) currentCosPr = cosmeticProductRepo.findById(id).get();

        CosmeticProduct newCosPr = new CosmeticProduct(currentCosPr.getName(), currentCosPr.getBrand(), currentCosPr.getVolume(), currentCosPr.getTime_after_opening(),
                currentCosPr.getShelf_life(), currentCosPr.getAutopsy_date(), currentCosPr.getNote(), currentCosPr.getDate_death(), user);

        cosmeticProductRepo.save(newCosPr);
        return "redirect:"+request.getHeader("Referer");
    }
}
