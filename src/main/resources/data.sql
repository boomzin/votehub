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