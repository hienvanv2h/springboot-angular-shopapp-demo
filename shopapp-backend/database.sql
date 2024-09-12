CREATE DATABASE `shopapp`;
USE `shopapp`;

-- Bảng người dùng (User)
CREATE TABLE `users`(
	`id` INT PRIMARY KEY AUTO_INCREMENT,
    `fullname` VARCHAR(100) DEFAULT "",
    `phone_number` VARCHAR(20) UNIQUE NOT NULL,
    `address` VARCHAR(200) DEFAULT "",
    `password`VARCHAR(100) NOT NULL DEFAULT "",
    `created_at` DATETIME,
    `updated_at` DATETIME,
    `is_active` TINYINT DEFAULT 1,
    `date_of_birth` DATE,
    `facebook_account_id` INT DEFAULT 0,
    `google_account_id` INT DEFAULT 0
);

-- Phân quyền (Role)
CREATE TABLE `roles`(
	`id` INT PRIMARY KEY,
    `name` VARCHAR(20) NOT NULL
);

ALTER TABLE `users` ADD COLUMN `role_id` INT;
ALTER TABLE `users` ADD FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`);

-- Bảng Token
CREATE TABLE `tokens`(
	`id` INT PRIMARY KEY AUTO_INCREMENT,
    `token` VARCHAR(255) UNIQUE NOT NULL,
    `token_type` VARCHAR(50) NOT NULL,
    `expiration_date` DATETIME,
    `revoked` TINYINT NOT NULL,
    `expired` TINYINT NOT NULL,
    `user_id` INT,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);
 
 -- Hỗ trợ đăng nhập Facebook và Google
 CREATE TABLE `social_accounts`(
	`id` INT PRIMARY KEY AUTO_INCREMENT,
    `provider` VARCHAR(20) NOT NULL COMMENT "Tên nhà cung cấp social network",
    `provider_id` VARCHAR(50) NOT NULL,
    `email` VARCHAR(150) NOT NULL COMMENT "Email tài khoản",
    `name` VARCHAR(100) NOT NULL COMMENT "Tên người dùng",
    `user_id` INT,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
 );
 
 -- Bảng danh mục sản phẩm (Category)
 CREATE TABLE `categories`(
	`id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL DEFAULT "" COMMENT "Tên danh mục, VD: Đồ điện tử"
 );

-- Bảng sản phẩm (Product)
CREATE TABLE `products`(
	`id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(350) NOT NULL DEFAULT "" COMMENT "Tên sản phẩm",
    `price` FLOAT NOT NULL CHECK (`price` >= 0),
    `thumbnail_url` VARCHAR(300) DEFAULT "",
    `description` LONGTEXT,
    `created_at` TIMESTAMP,
    `updated_at` TIMESTAMP,
    `category_id` INT,
    FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`)
);
-- ALTER TABLE `products`
-- ADD COLUMN `quantity` INT NOT NULL;

-- UPDATE `products` SET `products.quantity` = 20;


-- Bảng lưu URL ảnh của sản phẩm (Product Images)
CREATE TABLE `product_images`(
	`id` INT PRIMARY KEY AUTO_INCREMENT,
    `image_url` VARCHAR(300) DEFAULT "",
    `product_id` INT,
    CONSTRAINT `fk_product_images_product_id`
		FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE
);

-- Bảng đặt hàng (Order)
CREATE TABLE `orders`(
	`id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT,
    `fullname` VARCHAR(100) NOT NULL DEFAULT "" COMMENT "Tên người đặt hàng",
    `email` VARCHAR(100) DEFAULT "",
    `phone_number` VARCHAR(20) NOT NULL,
    `address` VARCHAR(200) NOT NULL,
    `note` VARCHAR(100) DEFAULT "",
    `order_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `status` VARCHAR(20),
    `total_money` FLOAT CHECK(`total_money` >= 0)
);
ALTER TABLE `orders` ADD COLUMN `shipping_method` VARCHAR(100);
ALTER TABLE `orders` ADD COLUMN `shipping_address` VARCHAR(200);
ALTER TABLE `orders` ADD COLUMN `shipping_date` DATE;
ALTER TABLE `orders` ADD COLUMN `tracking_number` VARCHAR(100);
ALTER TABLE `orders` ADD COLUMN `payment_method` VARCHAR(100);
-- Khi xóa 1 đơn hàng thì "active" là false
ALTER TABLE `orders` ADD COLUMN `active` TINYINT;
-- Ràng buộc giá trị của "status"
ALTER TABLE `orders`
MODIFY COLUMN `status` ENUM("pending", "processing", "shipped", "delivered", "canceled")
COMMENT "Trạng thái đơn hàng";

-- Bảng chi tiết đơn hàng (Order Detail)
CREATE TABLE `order_details`(
	`id` INT PRIMARY KEY AUTO_INCREMENT,
    `order_id` INT,
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
	`product_id` INT,
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`),
    `price` FLOAT CHECK(`price` >= 0),
    `number_of_products` INT CHECK(`number_of_products` > 0),
    `total_money` FLOAT CHECK(`total_money` >= 0),
    `color` VARCHAR(20) DEFAULT ""
);



