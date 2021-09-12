[Voting system for deciding where to have lunch](https://github.com/boomzin/votehub)
===============================

Spring/JPA application with authorization and role-based access rights without frontend. Using the  Java tools and technologies: Maven, Spring-Boot, Spring MVC, Security, JPA(Hibernate), REST(Jackson), H2 database.


### Application capabilities:
- 3 types of users: 
    - admin;
    - regular users;
    - anonymous user.
- Admins are allowed:
  - create, delete, view and assign role for users; 
  - create, delete, change name and address for restaurants;
  - create and delete actual menu (list of menu items), view menu archives;
  - create, delete, and change price for single actual menu item, view menu items archives;
  - view vote history.
- Regular users are allowed:
  - create, delete, view and change their own account;
  - create, delete, view and change their own voting result for current day.
- Anonymous user are allowed:
  - register in order to create their own account;
  - view list of restaurants with menu and rating including archive.
- Menu changes each day (admins do the updates). Each restaurant provides a new menu each day.
- Users can vote on which restaurant they want to have lunch at, and view the today rating
- Only one vote counted per user, if user votes again the same day:
  - If it is before 11:00 we assume that he changed his mind
  - If it is after 11:00 then it is too late, vote can't be changed

-------------------

### REST API documentation prepared by Swagger is available on started application
> - Run Application `RestaurantVotingApplication`
> - Open API documentation by link: <a href="localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/">localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/</a>

-------------------

### Implementation features

#### H2 Database in memory with tables:
- **USERS** - contains user entity
  - corresponds to the `User.class`
- **USER_ROLES** - contains user roles
  - corresponds to the `Role.enum`
- **RESTAURANT** - contains restaurant entity
  - corresponds to the `Restaurant.class`
  - unique constraints by fields `name` and `address`
- **MENU_ITEM** - contains menu items entity
  - corresponds to the `MenuItem.class`
  - unique constraints by fields `restaurant_id`, `menu_date`, `description`
- **VOTE** - contains vote entity
  - corresponds to the `Vote.class`
  - unique constraints by fields `vote_date` and `user_id`

#### 3 caches configured:
- users
- restaurants
- menu

#### Voting process by Users
- Authorized user vote for the restaurant using POST method. If there is a vote of this user for the current date, then an exception is thrown;
- Class `TimeInterceptor implements HandlerInterceptor` checks time. If it's before 11-00 methods PUT and DELETE method are allowed, and user is available to change their mind. Else throw exception.


-------------------

> 2021. Ernest Aidinov. <a href="mailto:ernest.aidinov@gmail.com">ernest.aidinov@gmail.com</a>

-------------------