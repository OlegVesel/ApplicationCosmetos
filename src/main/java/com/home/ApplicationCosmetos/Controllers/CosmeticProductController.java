package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.CosmeticProduct;
import com.home.ApplicationCosmetos.Model.Role;
import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.CosmeticProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;


@Controller
public class CosmeticProductController {
    @Autowired
    private CosmeticProductRepo cosmeticProductRepo;

    @GetMapping("/app")
    public String viewCosmeticProduct(@AuthenticationPrincipal User user,
                                      @RequestParam(required = false, defaultValue = "") String filter_name,
                                      @RequestParam(required = false, defaultValue = "") String filter_brand,
                                      Model model,
                                      @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {

        Iterable<CosmeticProduct> filterCosmetic;  //отфильтрованный список средств, которые принадлежат пользователю
        Iterable<String> listOfProducts;    //список названий средств
        Iterable<String> listOfBrands;  //список названий брендов
        Page<CosmeticProduct> page = null;


        listOfProducts = cosmeticProductRepo.distinctName(user.getId());
        listOfBrands = cosmeticProductRepo.distinctBrand(user.getId());
        if (!filter_name.isEmpty() & !filter_brand.isEmpty()) {
            filterCosmetic = cosmeticProductRepo.findByNameAndBrandAndOwner(filter_name, filter_brand, user);
        } else if (!filter_name.isEmpty() & filter_brand.isEmpty()) {
            filterCosmetic = cosmeticProductRepo.findByNameAndOwner(filter_name, user);
        } else if (!filter_brand.isEmpty()) {
            filterCosmetic = cosmeticProductRepo.findByBrandAndOwner(filter_brand, user);
        } else {
            page = cosmeticProductRepo.findByOwner(user, pageable);
        }


        model.addAttribute("listOfBrands", listOfBrands);
        model.addAttribute("listOfProducts", listOfProducts);
        model.addAttribute("filter_name", filter_name);
        model.addAttribute("filter_brand", filter_brand);
        model.addAttribute("allCosmetic", page);
        model.addAttribute("user", user);
        model.addAttribute("url", "/app");
        return "CosmeticProduct";
    }

    @PostMapping("/app")
    public String addCosmeticProduct(@AuthenticationPrincipal User user,
                                     @Valid CosmeticProduct cosmeticProduct,
                                     BindingResult bindingResult,
                                     Model model,
                                     @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {

        LocalDate date_death;
        if (cosmeticProduct.getAutopsy_date() != null && cosmeticProduct.getAutopsy_date()
                .plusMonths(cosmeticProduct.getTime_after_opening())
                .isBefore(cosmeticProduct.getShelf_life()))
            date_death = cosmeticProduct.getAutopsy_date()
                    .plusMonths(cosmeticProduct.getTime_after_opening());
        else date_death = cosmeticProduct.getShelf_life();

        cosmeticProduct.setOwner(user);
        cosmeticProduct.setDate_death(date_death);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("cosmeticProduct", cosmeticProduct);
        } else {
            model.addAttribute("cosmeticProduct", null);
            model.addAttribute("messageSuccess", "Средство успешно добавлено");
            cosmeticProductRepo.save(cosmeticProduct);
        }

        Iterable<CosmeticProduct> allCosmetic;
        if (user.getRoles().contains(Role.ADMIN)) allCosmetic = cosmeticProductRepo.findAll();
        else allCosmetic = cosmeticProductRepo.findByOwner(user, pageable);
        model.addAttribute("listOfBrands", cosmeticProductRepo.distinctBrand(user.getId()));
        model.addAttribute("listOfProducts", cosmeticProductRepo.distinctName(user.getId()));
        model.addAttribute("allCosmetic", allCosmetic);
        model.addAttribute("user", user);

        return "CosmeticProduct";
    }

}
