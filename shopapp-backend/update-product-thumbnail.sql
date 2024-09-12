SELECT * FROM shopapp.products;

SET SQL_SAFE_UPDATES = 0;

UPDATE products
SET products.thumbnail_url = COALESCE(
	(
		SELECT image_url
		FROM product_images
		WHERE product_images.product_id = products.id
		LIMIT 1
	), "");

SET SQL_SAFE_UPDATES = 1;