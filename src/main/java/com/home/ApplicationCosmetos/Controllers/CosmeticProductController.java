package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.CosmeticProduct;
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
                                      @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, size = 15) Pageable pageable) {

        Page<CosmeticProduct> filterCosmetic;  //отфильтрованный список средств, которые принадлежат пользователю
        Iterable<String> listOfProducts;    //список названий средств
        Iterable<String> listOfBrands;  //список названий брендов
        Page<CosmeticProduct> pageWithCosmetic = null;


        listOfProducts = cosmeticProductRepo.distinctName(user.getId());
        listOfBrands = cosmeticProductRepo.distinctBrand(user.getId());
        if (!filter_name.isEmpty() & !filter_brand.isEmpty()) {
            pageWithCosmetic = cosmeticProductRepo.findByNameAndBrandAndOwner(filter_name, filter_brand, user, pageable);
        } else if (!filter_name.isEmpty() & filter_brand.isEmpty()) {
            pageWithCosmetic = cosmeticProductRepo.findByNameAndOwner(filter_name, user, pageable);
        } else if (!filter_brand.isEmpty()) {
            pageWithCosmetic = cosmeticProductRepo.findByBrandAndOwner(filter_brand, user, pageable);
        } else {
            pageWithCosmetic = cosmeticProductRepo.findByOwner(user, pageable);
        }


        model.addAttribute("listOfBrands", listOfBrands);       //список брендов для выпадающего списка
        model.addAttribute("listOfProducts", listOfProducts);   //список средств для выпадающего списка
        model.addAttribute("filter_name", filter_name);         // фильтр по средству для отображения
        model.addAttribute("filter_brand", filter_brand);       // фильтр по бренду
        model.addAttribute("allCosmetic", pageWithCosmetic);    //полный список продуктов, разбитый по страницам
        model.addAttribute("user", user);                       //авторизованный пользователь
        model.addAttribute("url", "/app");                   //url на котороый делается запрос для изменения отображаемых страниц
        model.addAttribute("badSoon", ControllerUtils.amountBadSoon(cosmeticProductRepo.findByOwner(user)));
        return "CosmeticProduct";
    }

    /**
     * @param user            - пользователь под которым зашли
     * @param cosmeticProduct - один продукт
     * @param bindingResult   - для валидации
     * @param model           - мапа спринга
     * @param pageable        - выводим постранично с помощью этой структуры
     * @return возвращаем название странички
     */
    @PostMapping("/app")
    public String addCosmeticProduct(@AuthenticationPrincipal User user,
                                     @Valid CosmeticProduct cosmeticProduct,
                                     BindingResult bindingResult,
                                     Model model,
                                     @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        //вычисляем дату выброса продукта
        LocalDate dateDeath =
                ControllerUtils.getDateDeath(cosmeticProduct.getTime_after_opening(), cosmeticProduct.getAutopsyDate(), cosmeticProduct.getShelfLife());

        //установим владельца продукта
        cosmeticProduct.setOwner(user);
        //установим дату выброса
        cosmeticProduct.setDateDeath(dateDeath);

        //если есть ошибки при валидации класса CosmeticProduct то возвращаем его обратно, не сохранив
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("cosmeticProduct", cosmeticProduct);
        } else { //в обратном случае обнуляем, и сохраняем
            model.addAttribute("cosmeticProduct", null);
            model.addAttribute("messageSuccess", "Средство успешно добавлено");
            cosmeticProductRepo.save(cosmeticProduct);
        }

        //созлаем структуру со страничками и заполнем ее
        Page<CosmeticProduct> pageWithCosmetic;
        pageWithCosmetic = cosmeticProductRepo.findByOwner(user, pageable);
        //выкатываем список брендов и средств для подсказок при заполнении
        model.addAttribute("listOfBrands", cosmeticProductRepo.distinctBrand(user.getId()));
        model.addAttribute("listOfProducts", cosmeticProductRepo.distinctName(user.getId()));
        model.addAttribute("allCosmetic", pageWithCosmetic);
        model.addAttribute("user", user);
        //передаем url для обновления страницы (костыль)
        model.addAttribute("url", "/app");
        //посчитаем сколько средств испортится в течении месяца
        model.addAttribute("badSoon", ControllerUtils.amountBadSoon(cosmeticProductRepo.findByOwner(user)));

        return "CosmeticProduct";
    }

}
