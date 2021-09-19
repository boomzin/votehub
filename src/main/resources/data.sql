INSERT INTO users (email, username, password)
VALUES ('user1@gmail.com', 'User1', '{noop}user1pass'),
       ('user2@gmail.com', 'User2', '{noop}user2pass'),
       ('user3@gmail.com', 'User3', '{noop}user3pass'),
       ('admin@gmail.com', 'Admin', '{noop}admin');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 1),
       ('USER', 2),
       ('USER', 3),
       ('USER', 4),
       ('ADMIN', 4);

INSERT INTO restaurant (restaurant_name, address)
VALUES ('Restaurant1', 'Restaurant1Address'),
       ('Restaurant2', 'Restaurant2Address'),
       ('Restaurant3', 'Restaurant3Address');

INSERT INTO menu_item (menu_date, description, price, restaurant_id)
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
       ('2021-09-02', 'Second day rest3 dish5', 17, 3),
       (now(), 'Today rest1 dish1', 10, 1),
       (now(), 'Today rest1 dish2', 15, 1),
       (now(), 'Today rest1 dish3', 15, 1),
       (now(), 'Today rest2 dish1', 15, 2),
       (now(), 'Today rest2 dish2', 15, 2),
       (now(), 'Today rest2 dish3', 15, 2),
       (now(), 'Today rest3 dish1', 15, 3),
       (now(), 'Today rest3 dish2', 15, 3),
       (now(), 'Today rest3 dish3', 15, 3),
       (now(), 'Today rest3 dish4', 17, 3);

INSERT INTO vote (vote_date, description, restaurant_id, user_id)
VALUES ( '2021-09-01', 'day1 user1 rest2', 2, 1),
       ( '2021-09-01', 'day1 user2 rest2', 2, 2),
       ( '2021-09-01', 'day1 user3 rest1', 1, 3),
       ( '2021-09-01', 'day1 admin rest3', 3, 4),
       ( '2021-09-02', 'day2 user1 rest2', 2, 1),
       ( '2021-09-02', 'day2 user2 rest1', 1, 2),
       ( '2021-09-02', 'day2 user3 rest2', 2, 3),
       ( now(), 'today user1 rest1', 1, 1),
       ( now(), 'today user2 rest2', 2, 2),
       ( now(), 'today admin rest1', 1, 4);

