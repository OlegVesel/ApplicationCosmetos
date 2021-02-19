package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.CosmeticProduct;
import com.home.ApplicationCosmetos.Model.Role;
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
public class CosmeticProductController {
    @Autowired
    private CosmeticProductRepo cosmeticProductRepo;

    @GetMapping("/app")
    public String viewCosmeticProduct(@AuthenticationPrincipal User user,
                                      @RequestParam(required = false, defaultValue = "") String filter_name,
                                      @RequestParam(required = false, defaultValue = "") String filter_brand,
                                      Model model) {

        Iterable<CosmeticProduct> filterCosmetic;  //отфильтрованный список средств, которые принадлежат пользователю
        Iterable<String> listOfProducts;    //список названий средств
        Iterable<String> listOfBrands;  //список названий брендов

        if (user.getRoles().contains(Role.ADMIN)) {
            listOfProducts = cosmeticProductRepo.distinctName(user.getId());
            listOfBrands = cosmeticProductRepo.distinctBrand(user.getId());
            if (!filter_name.isEmpty() & !filter_brand.isEmpty()) {
                filterCosmetic = cosmeticProductRepo.findByNameLikeAndBrandLike(filter_name, filter_brand);
            } else if (!filter_name.isEmpty() & filter_brand.isEmpty()) {
                filterCosmetic = cosmeticProductRepo.findByName(filter_name);
            } else if (!filter_brand.isEmpty()) {
                filterCosmetic = cosmeticProductRepo.findByBrand(filter_brand);
            } else {
                filterCosmetic = cosmeticProductRepo.findAll();
            }
        } else {
            listOfProducts = cosmeticProductRepo.distinctName(user.getId());
            listOfBrands = cosmeticProductRepo.distinctBrand(user.getId());
            if (!filter_name.isEmpty() & !filter_brand.isEmpty()) {
                filterCosmetic = cosmeticProductRepo.findByNameAndBrandAndOwner(filter_name, filter_brand, user);
            } else if (!filter_name.isEmpty() & filter_brand.isEmpty()) {
                filterCosmetic = cosmeticProductRepo.findByNameAndOwner(filter_name, user);
            } else if (!filter_brand.isEmpty()) {
                filterCosmetic = cosmeticProductRepo.findByBrandAndOwner(filter_brand, user);
            } else {
                filterCosmetic = cosmeticProductRepo.findByOwner(user);
            }
        }
        model.addAttribute("listOfBrands", listOfBrands);
        model.addAttribute("listOfProducts", listOfProducts);
        model.addAttribute("filter_name", filter_name);
        model.addAttribute("filter_brand", filter_brand);
        model.addAttribute("allCosmetic", filterCosmetic);
        model.addAttribute("user", user);
        return "CosmeticProduct";
    }

    @PostMapping("/app")
    public String addCosmeticProduct(@AuthenticationPrincipal User user,
                                     String name,
                                     String brand,
                                     String volume,
                                     int time_after_opening,
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shelf_life,
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate autopsy_date,
                                     String note,
                                     Model model) {

        LocalDate date_death;
        if (autopsy_date.plusMonths(time_after_opening).isBefore(shelf_life))
            date_death = autopsy_date.plusMonths(time_after_opening);
        else date_death = shelf_life;


        CosmeticProduct adding_cosmeticProduct = new CosmeticProduct(name, brand, volume, time_after_opening, shelf_life, autopsy_date, note, date_death, user);

        if (StringUtils.isEmpty(name) | StringUtils.isEmpty(brand)) {
            model.addAttribute("message", "Поля Название и Бренд должны быть заполнены");
        } else {
            // model.addAttribute("message", "Успешно добавлено");

            cosmeticProductRepo.save(adding_cosmeticProduct);

        }

        Iterable<CosmeticProduct> allCosmetic;
        if (user.getRoles().contains(Role.ADMIN)) allCosmetic = cosmeticProductRepo.findAll();
        else allCosmetic = cosmeticProductRepo.findByOwner(user);
        model.addAttribute("listOfBrands", cosmeticProductRepo.distinctBrand(user.getId()));
        model.addAttribute("listOfProducts", cosmeticProductRepo.distinctName(user.getId()));
        model.addAttribute("allCosmetic", allCosmetic);
        model.addAttribute("user", user);

        return "CosmeticProduct";
    }

}