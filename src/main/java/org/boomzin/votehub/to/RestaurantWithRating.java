package org.boomzin.votehub.to;

import org.boomzin.votehub.model.Restaurant;

public interface RestaurantWithRating {
    Restaurant getRestaurantInRating();
    Integer getRating();
}
