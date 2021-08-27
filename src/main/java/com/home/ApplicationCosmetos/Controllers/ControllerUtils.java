package com.home.ApplicationCosmetos.Controllers;

import com.home.ApplicationCosmetos.Model.CosmeticProduct;
import com.home.ApplicationCosmetos.Model.User;
import com.home.ApplicationCosmetos.Repo.CosmeticProductRepo;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ControllerUtils {
    static Map<String, String> getErrors(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );
        return bindingResult.getFieldErrors().stream().collect(collector);
    }

    static LocalDate getDateDeath(int timeAfterOpening, LocalDate autopsyDate, LocalDate shelfLife){
        LocalDate dateDeath;
        if (timeAfterOpening != 0 && autopsyDate != null &&
                (autopsyDate.plusMonths(timeAfterOpening)).isBefore(shelfLife))
            dateDeath = autopsyDate
                    .plusMonths(timeAfterOpening);
        else dateDeath = shelfLife;
        return dateDeath;
    }

    //найдем сколько средств испортится в ближайший месяц
    static int amountBadSoon(List<CosmeticProduct> list){
        int amountBadSoon = 0;
        for (CosmeticProduct product: list) {
            if (LocalDate.now().plusMonths(1L).isAfter(product.getDateDeath())) {
                amountBadSoon++;
            }
        }
        return amountBadSoon;

    }

}
