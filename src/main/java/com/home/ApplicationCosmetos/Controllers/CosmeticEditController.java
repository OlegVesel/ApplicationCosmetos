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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/app")
public class CosmeticEditController {
    @Autowired
    private CosmeticProductRepo cosmeticProductRepo;

    @GetMapping("edit/{cosmeticProduct}")
    public String viewCosmeticEdit(@AuthenticationPrincipal User user, @PathVariable CosmeticProduct cosmeticProduct, Model model) {
        model.addAttribute("listOfProducts", cosmeticProductRepo.distinctName(user.getId()));
        model.addAttribute("listOfBrands", cosmeticProductRepo.distinctBrand(user.getId()));
        model.addAttribute("cosmeticProduct", cosmeticProduct);
        model.addAttribute("user", user);
        return "CosmeticEdit";
    }

    @PostMapping("{id}")
    public String editCosmeticProduct(@AuthenticationPrincipal User user,
                                      @PathVariable Long id,
                                      String name,
                                      String brand,
                                      String volume,
                                      int time_after_opening,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shelf_life,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate autopsy_date,
                                      String note,
                                      Model model) {

        CosmeticProduct productById = null;
        if (cosmeticProductRepo.findById(id).isPresent()) productById = cosmeticProductRepo.findById(id).get();

        productById.setName(name);
        productById.setBrand(brand);
        productById.setVolume(volume);
        productById.setTime_after_opening(time_after_opening);
        productById.setShelf_life(shelf_life);
        productById.setNote(note);
        if (!StringUtils.isEmpty(autopsy_date)) {
            productById.setAutopsy_date(autopsy_date);
        }
        cosmeticProductRepo.save(productById);
        return "redirect:/app";
    }

    //метод удаления записи со средством из БД, из шаблона CosmeticProduct  по нажатию на кнопку
    @PostMapping("/delete/{id}")
    public String deleteCosmeticProduct(@PathVariable Long id, Model model) {
        cosmeticProductRepo.deleteById(id);
        return "redirect:/app";
    }

    //метод для копирования полностью, выбранного средства
    @PostMapping("/copy/{id}")
    public String copyCosmeticProduct(@AuthenticationPrincipal User user, @PathVariable Long id, Model model) {
        CosmeticProduct currentCosPr = null;
        boolean is_open = false;
        if (cosmeticProductRepo.findById(id).isPresent()) currentCosPr = cosmeticProductRepo.findById(id).get();

        CosmeticProduct newCosPr = new CosmeticProduct(currentCosPr.getName(), currentCosPr.getBrand(), currentCosPr.getVolume(), currentCosPr.getTime_after_opening(),
                currentCosPr.getShelf_life(), currentCosPr.getAutopsy_date(), currentCosPr.getNote(), currentCosPr.getDate_death(), user);

        cosmeticProductRepo.save(newCosPr);
        return "redirect:/app";
    }
}
