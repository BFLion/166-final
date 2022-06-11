COPY MENU
FROM '/extra/amuno034/needed_files/166-final/data/menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM '/extra/amuno034/needed_files/166-final/data/users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM '/extra/amuno034/needed_files/166-final/data/orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM '/extra/amuno034/needed_files/166-final/data/itemStatus.csv'
WITH DELIMITER ';';

