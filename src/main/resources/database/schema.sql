DROP TABLE IF EXISTS tz_products
DROP TABLE IF EXISTS tz_categories
CREATE TABLE tz_categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(120) NOT NULL)
CREATE TABLE tz_products (id INTEGER PRIMARY KEY AUTOINCREMENT, id_category INTEGER NOT NULL, name VARCHAR(120) NOT NULL)