INSERT INTO USERS (EMAIL, USERNAME, PASSWORD)
VALUES ('user1@gmail.com', 'User1', 'user1pass'),
       ('user2@gmail.com', 'User2', 'user2pass'),
       ('user3@gmail.com', 'User3', 'user3pass'),
       ('admin@gmail.com', 'Admin', 'admin');

INSERT INTO USER_ROLE (ROLE, USER_ID)
VALUES ('ROLE_USER', 1),
       ('ROLE_USER', 2),
       ('ROLE_USER', 3),
       ('ROLE_USER', 4),
       ('ROLE_ADMIN', 4);

INSERT INTO RESTAURANT (RESTAURANT_NAME, ADDRESS)
VALUES ('Restaurant1', 'Restaurant1Address'),
       ('Restaurant2', 'Restaurant2Address'),
       ('Restaurant3', 'Restaurant3Address');

INSERT INTO MENU_ITEM (DATE, DESCRIPTION, PRICE, RESTAURANT_ID)
VALUES ('2021-09-01', 'First day rest1 dish1', 10, 1),
       ('2021-09-01', 'First day rest1 dish2', 15, 1),
       ('2021-09-01', 'First day rest1 dish3', 17, 1),
       ('2021-09-01', 'First day rest2 dish1', 12, 2),
       ('2021-09-01', 'First day rest2 dish2', 17, 2),
       ('2021-09-01', 'First day rest2 dish3', 17, 2),
       ('2021-09-01', 'First day rest2 dish4', 22, 2),
       ('2021-09-01', 'First day rest3 dish1', 17, 3),
       ('2021-09-01', 'First day rest3 dish2', 15, 3),
       ('2021-09-01', 'First day rest3 dish3', 17, 3),
       ('2021-09-02', 'Second day rest1 dish1', 10, 1),
       ('2021-09-02', 'Second day rest1 dish2', 15, 1),
       ('2021-09-02', 'Second day rest2 dish1', 13, 2),
       ('2021-09-02', 'Second day rest2 dish2', 17, 2),
       ('2021-09-02', 'Second day rest2 dish3', 17, 2),
       ('2021-09-02', 'Second day rest3 dish1', 14, 3),
       ('2021-09-02', 'Second day rest3 dish2', 17, 3),
       ('2021-09-02', 'Second day rest3 dish3', 17, 3),
       ('2021-09-02', 'Second day rest3 dish4', 17, 3),
       ('2021-09-02', 'Second day rest3 dish5', 17, 3);
