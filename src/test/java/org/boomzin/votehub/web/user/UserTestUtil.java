package org.boomzin.votehub.web.user;

import org.boomzin.votehub.model.Role;
import org.boomzin.votehub.model.User;
import org.boomzin.votehub.web.MatcherFactory;

public class UserTestUtil {
    public static final MatcherFactory.Matcher<User> MATCHER = MatcherFactory.usingIgnoringFieldsComparator(User.class, "votes", "password");
    public static final int USER_ID = 1;
    public static final int ADMIN_ID = 4;
    public static final String USER_MAIL = "user1@gmail.com";
    public static final String ADMIN_MAIL = "admin@gmail.com";

    public static final User user = new User(USER_ID, "User", USER_MAIL, "password", Role.USER);
    public static final User user2 = new User(2, "User2", "user2@gmail.com", "user2pass", Role.USER);
    public static final User user3 = new User(3, "User3", "user3@gmail.com", "user3pass", Role.USER);
    public static final User admin = new User(ADMIN_ID, "Admin", ADMIN_MAIL, "admin",Role.ADMIN, Role.USER);
}
