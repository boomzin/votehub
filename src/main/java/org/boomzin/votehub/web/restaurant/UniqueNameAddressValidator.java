package org.boomzin.votehub.web.restaurant;

import lombok.AllArgsConstructor;
import org.boomzin.votehub.error.IllegalRequestDataException;
import org.boomzin.votehub.model.Restaurant;
import org.boomzin.votehub.repository.RestaurantRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

@Component
@AllArgsConstructor
public class UniqueNameAddressValidator implements org.springframework.validation.Validator{

    private final RestaurantRepository repository;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Restaurant.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Restaurant restaurant = ((Restaurant) target);
        if (StringUtils.hasText(restaurant.getName()) && StringUtils.hasText(restaurant.getAddress())) {
            repository.getByNameAndAddress(restaurant.getName(), restaurant.getAddress())
                    .ifPresent(dbRestaurant -> {
                        throw new IllegalRequestDataException("The restaurant with name "
                        + dbRestaurant.getName()
                        + " already exist with address "
                        + dbRestaurant.getAddress());
                    });
        }
    }
}
