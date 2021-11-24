package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.CosmeticProduct;
import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.CosmeticProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    /**
     * @param user      -пользователь под которым зашли
     * @param find      - что ищет пользователь
     * @param model     - структура Spring
     * @param size      - параметр из url для задания размера страницы
     * @param sortBy    - параметр из url для задания сортировки по столбцу
     * @param direction - параметр из url для задания направления сортировки
     * @return - возвращает название шаблона страницы
     */
    @GetMapping("/app")
    public String viewCosmeticProduct(@AuthenticationPrincipal User user,
                                      @RequestParam(required = false, defaultValue = "") String find,
                                      Model model,
                                      @RequestParam(required = false, defaultValue = "15") Integer size,
                                      @RequestParam(required = false, defaultValue = "name") String sortBy,
                                      @RequestParam(required = false, defaultValue = "true") boolean direction
    ) {

        Iterable<String> listOfProducts;    //список названий средств
        Iterable<String> listOfBrands;  //список названий брендов
        Page<CosmeticProduct> pageWithCosmetic = null;


        listOfProducts = cosmeticProductRepo.distinctName(user.getId());
        listOfBrands = cosmeticProductRepo.distinctBrand(user.getId());
        if (!find.isEmpty()) {
            pageWithCosmetic = cosmeticProductRepo.findBySearch(user.getId(), find, PageRequest.of(0, Integer.MAX_VALUE, direction ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy)));
        } else {
            pageWithCosmetic = cosmeticProductRepo.findByOwner(user, PageRequest.of(0, size, direction ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy)));
        }


        model.addAttribute("listOfBrands", listOfBrands);       //список брендов для выпадающего списка
        model.addAttribute("listOfProducts", listOfProducts);   //список средств для выпадающего списка
        model.addAttribute("find", find);                       // то, что искал пользователь, кидаем ему обратно
        model.addAttribute("allCosmetic", pageWithCosmetic);    //полный список продуктов, разбитый по страницам
        model.addAttribute("user", user);                       //авторизованный пользователь
        model.addAttribute("url", "/app");                   //url на который делается запрос для изменения отображаемых страниц
        model.addAttribute("badSoon", ControllerUtils.amountBadSoon(cosmeticProductRepo.findByOwner(user))); //поле содержит количество средств которые испортятся в течение месяца
        return "CosmeticProduct";
    }

    /**
     * @param user            - пользователь под которым зашли
     * @param cosmeticProduct - один продукт
     * @param bindingResult   - для валидации
     * @param model           - структура Spring
     * @param size            - параметр из url для задания размера страницы
     * @param sortBy          - параметр из url для задания сортировки по столбцу
     * @param direction       - параметр из url для задания направления сортировки
     * @return возвращаем название странички
     */
    @PostMapping("/app")
    public String addCosmeticProduct(@AuthenticationPrincipal User user,
                                     @Valid CosmeticProduct cosmeticProduct,
                                     BindingResult bindingResult,
                                     Model model,
                                     @RequestParam(required = false, defaultValue = "15") Integer size,
                                     @RequestParam(required = false, defaultValue = "name") String sortBy,
                                     @RequestParam(required = false, defaultValue = "true") boolean direction) {
        //вычисляем дату выброса продукта
        LocalDate dateDeath =
                ControllerUtils.getDateDeath(cosmeticProduct.getTime_after_opening(), cosmeticProduct.getAutopsyDate(), cosmeticProduct.getShelfLife());

        //установим владельца продукта
        cosmeticProduct.setOwner(user);
        //установим дату выброса
        cosmeticProduct.setDateDeath(dateDeath);

        //если есть ошибки при валидации класса CosmeticProduct, то возвращаем его обратно, не сохранив
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("cosmeticProduct", cosmeticProduct);
        } else { //в обратном случае обнуляем, и сохраняем
            model.addAttribute("cosmeticProduct", null);
            model.addAttribute("messageSuccess", "Средство успешно добавлено");
            cosmeticProductRepo.save(cosmeticProduct);
        }


        //создаем структуру со страничками и заполняем ее
        Page<CosmeticProduct> pageWithCosmetic;
        pageWithCosmetic = cosmeticProductRepo.findByOwner(user, PageRequest.of(0, size, direction ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy)));
        //выкатываем список брендов и средств для подсказок при заполнении
        model.addAttribute("listOfBrands", cosmeticProductRepo.distinctBrand(user.getId()));
        model.addAttribute("listOfProducts", cosmeticProductRepo.distinctName(user.getId()));
        model.addAttribute("allCosmetic", pageWithCosmetic);
        model.addAttribute("user", user);
        //передаем url для обновления страницы (костыль)
        model.addAttribute("url", "/app");
        //посчитаем сколько средств испортится в течение месяца
        model.addAttribute("badSoon", ControllerUtils.amountBadSoon(cosmeticProductRepo.findByOwner(user)));

        return "CosmeticProduct";
    }

}
