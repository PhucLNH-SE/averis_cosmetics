/* =====================================================
   CosmeticShopDB_v3 - Schema reference for coding
   (Entity/DAO align with these tables)
===================================================== */

/* CUSTOMERS */
-- customer_id (PK), username, full_name, email, password
-- gender (MALE|FEMALE|OTHER), date_of_birth, status, email_verified
-- auth_token, auth_token_type (EMAIL_VERIFY|PASSWORD_RESET), auth_token_expired_at, auth_token_used

/* ADDRESS */
-- address_id (PK), customer_id (FK), receiver_name, phone
-- province, district, ward, street_address, is_default

/* MANAGER */
-- manager_id (PK), full_name, email, password, manager_role (STAFF|ADMIN), status

/* BRAND */
-- brand_id (PK), name, status

/* CATEGORY */
-- category_id (PK), name, status

/* PRODUCT */
-- product_id (PK), name, description, brand_id (FK), category_id (FK), status

/* PRODUCT_IMAGE */
-- image_id (PK), product_id (FK), image_url, is_main

/* PRODUCT_VARIANT */
-- variant_id (PK), product_id (FK), variant_name, price, stock, status

/* CART_DETAIL */
-- cart_detail_id (PK), customer_id (FK), variant_id (FK), quantity

/* VOUCHER */
-- voucher_id (PK), code, discount_type (PERCENT|FIXED), discount_value, quantity, expired_at, status

/* ORDERS */
-- order_id (PK), customer_id (FK), address_id (FK), handled_by (FK), voucher_id (FK)
-- discount_amount, payment_method (COD|QR), payment_status (PENDING|SUCCESS|FAILED)
-- order_status (CREATED|PROCESSING|SHIPPING|COMPLETED|CANCELLED), total_amount, created_at, paid_at

/* ORDER_DETAIL */
-- order_detail_id (PK), order_id (FK), variant_id (FK), quantity, price_at_order

/* ORDER_STATUS_HISTORY */
-- history_id (PK), order_id (FK), old_status, new_status, changed_at, changed_by (FK)

/* REVIEW */
-- review_id (PK), customer_id (FK), product_id (FK), rating (1-5), comment, created_at
