package org.boomzin.votehub.to;

import lombok.Getter;
import lombok.Setter;
import org.boomzin.votehub.model.MenuItem;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class Menu {
//    https://stackoverflow.com/questions/17207766/spring-mvc-valid-on-list-of-beans-in-rest-service
    @Valid
    List<MenuItem> list;
}
