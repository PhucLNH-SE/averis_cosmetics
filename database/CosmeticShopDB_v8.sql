IF DB_ID('CosmeticShopDB_v8') IS NOT NULL
BEGIN
    ALTER DATABASE CosmeticShopDB_v8 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE CosmeticShopDB_v8;
END
GO

CREATE DATABASE CosmeticShopDB_v8;
GO

USE CosmeticShopDB_v8;
GO

/* =====================================================
   CUSTOMERS
===================================================== */
CREATE TABLE Customers (
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    avatar VARCHAR(255) NULL,
    gender VARCHAR(10) NOT NULL
        CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    date_of_birth DATE NOT NULL,
    status BIT NOT NULL DEFAULT 1,
    email_verified BIT NOT NULL DEFAULT 0,
    auth_token NVARCHAR(255) NULL,
    auth_token_type VARCHAR(30) NULL
        CHECK (auth_token_type IN ('EMAIL_VERIFY', 'PASSWORD_RESET')),
    auth_token_expired_at DATETIME NULL,
    auth_token_used BIT NOT NULL DEFAULT 0
);
GO

/* =====================================================
   ADDRESS
===================================================== */
CREATE TABLE Address (
    address_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    receiver_name NVARCHAR(100) NOT NULL,
    phone NVARCHAR(20) NOT NULL,
    province NVARCHAR(100) NOT NULL,
    district NVARCHAR(100) NOT NULL,
    ward NVARCHAR(100) NOT NULL,
    street_address NVARCHAR(255) NOT NULL,
    is_default BIT NOT NULL DEFAULT 0,
    is_deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_Address_Customer
        FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);
GO

/* =====================================================
   MANAGER
===================================================== */
CREATE TABLE Manager (
    manager_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    manager_role VARCHAR(20) NOT NULL
        CHECK (manager_role IN ('STAFF', 'ADMIN')),
    status BIT NOT NULL DEFAULT 1
);
GO

/* =====================================================
   SUPPLIER
===================================================== */
CREATE TABLE Supplier (
    supplier_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(150) NOT NULL UNIQUE,
    phone NVARCHAR(20) NOT NULL,
    address NVARCHAR(255) NOT NULL,
    status BIT NOT NULL DEFAULT 1
);
GO

/* =====================================================
   BRAND
===================================================== */
CREATE TABLE Brand (
    brand_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    status BIT NOT NULL DEFAULT 1
);
GO

/* =====================================================
   CATEGORY
===================================================== */
CREATE TABLE Category (
    category_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    status BIT NOT NULL DEFAULT 1
);
GO

/* =====================================================
   PRODUCT
===================================================== */
CREATE TABLE Product (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(150) NOT NULL,
    description NVARCHAR(MAX) NULL,
    brand_id INT NOT NULL,
    category_id INT NOT NULL,
    status BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_Product_Brand
        FOREIGN KEY (brand_id) REFERENCES Brand(brand_id),
    CONSTRAINT FK_Product_Category
        FOREIGN KEY (category_id) REFERENCES Category(category_id)
);
GO

/* =====================================================
   PRODUCT IMAGE
===================================================== */
CREATE TABLE Product_Image (
    image_id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    image_url NVARCHAR(255) NOT NULL,
    is_main BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_ProductImage_Product
        FOREIGN KEY (product_id) REFERENCES Product(product_id)
        ON DELETE CASCADE
);
GO

/* =====================================================
   PRODUCT VARIANT
===================================================== */
CREATE TABLE Product_Variant (
    variant_id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    variant_name NVARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL CHECK (stock >= 0),
    avg_cost DECIMAL(10,2) NOT NULL DEFAULT 0,
    status BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_ProductVariant_Product
        FOREIGN KEY (product_id) REFERENCES Product(product_id)
);
GO

/* =====================================================
   CART DETAIL
===================================================== */
CREATE TABLE Cart_Detail (
    cart_detail_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    variant_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    CONSTRAINT FK_CartDetail_Customer
        FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    CONSTRAINT FK_CartDetail_Variant
        FOREIGN KEY (variant_id) REFERENCES Product_Variant(variant_id)
);
GO

/* =====================================================
   VOUCHER
===================================================== */
CREATE TABLE Voucher (
    voucher_id INT IDENTITY(1,1) PRIMARY KEY,
    code NVARCHAR(50) NOT NULL UNIQUE,
    discount_type VARCHAR(10) NOT NULL
        CHECK (discount_type IN ('PERCENT', 'FIXED')),
    discount_value DECIMAL(10,2) NOT NULL,
	show_on_free_voucher BIT NOT NULL DEFAULT 0,
    quantity INT NOT NULL CHECK (quantity >= 0),
    expired_at DATETIME NULL,
    status BIT NOT NULL DEFAULT 1,
    voucher_type VARCHAR(20) NOT NULL
        CHECK (voucher_type IN ('FIXED_END_DATE', 'RELATIVE_DAYS')),
    fixed_start_at DATETIME NULL,
    fixed_end_at DATETIME NULL,
    relative_days INT NULL CHECK (relative_days > 0),
    claimed_quantity INT NOT NULL DEFAULT 0 CHECK (claimed_quantity >= 0),
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT CK_Voucher_Type_Fields CHECK (
        (voucher_type = 'FIXED_END_DATE' AND fixed_end_at IS NOT NULL)
        OR
        (voucher_type = 'RELATIVE_DAYS' AND relative_days IS NOT NULL)
    )
);
GO

/* =====================================================
   CUSTOMER VOUCHER
===================================================== */
CREATE TABLE Customer_Voucher (
    customer_voucher_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    voucher_id INT NOT NULL,
    claimed_at DATETIME NOT NULL DEFAULT GETDATE(),
    effective_from DATETIME NOT NULL,
    effective_to DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL
        CHECK (status IN ('ACTIVE', 'USED', 'EXPIRED')),
    used_at DATETIME NULL,
    CONSTRAINT FK_CustomerVoucher_Customer
        FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    CONSTRAINT FK_CustomerVoucher_Voucher
        FOREIGN KEY (voucher_id) REFERENCES Voucher(voucher_id),
    CONSTRAINT UQ_Customer_Voucher UNIQUE (customer_id, voucher_id)
);
GO

/* =====================================================
   IMPORT ORDER
===================================================== */
CREATE TABLE Import_Order (
    import_order_id INT IDENTITY(1,1) PRIMARY KEY,
    import_code NVARCHAR(50) NULL UNIQUE,
    supplier_id INT NULL,
    created_by INT NOT NULL,
    invoice_no NVARCHAR(100) NULL,
    note NVARCHAR(500) NULL,
    total_amount DECIMAL(12,2) NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    status VARCHAR(20) NOT NULL
        CHECK (status IN ('PENDING', 'RECEIVED', 'CANCELLED')),
    received_at DATETIME NULL,
    received_by INT NULL,
    CONSTRAINT FK_ImportOrder_Supplier
        FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id),
    CONSTRAINT FK_ImportOrder_CreatedBy
        FOREIGN KEY (created_by) REFERENCES Manager(manager_id),
    CONSTRAINT FK_ImportOrder_ReceivedBy
        FOREIGN KEY (received_by) REFERENCES Manager(manager_id)
);
GO


/* =====================================================
   IMPORT ORDER DETAIL
===================================================== */
CREATE TABLE Import_Order_Detail (
    import_detail_id INT IDENTITY(1,1) PRIMARY KEY,
    import_order_id INT NOT NULL,
    variant_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    import_price DECIMAL(10,2) NOT NULL,
    received_quantity INT NULL
        CHECK (received_quantity IS NULL OR received_quantity >= 0),
    CONSTRAINT FK_ImportOrderDetail_Order
        FOREIGN KEY (import_order_id) REFERENCES Import_Order(import_order_id)
        ON DELETE CASCADE,
    CONSTRAINT FK_ImportOrderDetail_Variant
        FOREIGN KEY (variant_id) REFERENCES Product_Variant(variant_id)
);
GO

/* =====================================================
   ORDERS
===================================================== */
CREATE TABLE Orders (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    address_id INT NOT NULL,
    handled_by INT NULL,
    voucher_id INT NULL,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    payment_method VARCHAR(20) NOT NULL
        CHECK (payment_method IN ('COD', 'MOMO')),
    payment_status VARCHAR(20) NOT NULL
        CHECK (payment_status IN ('PENDING', 'SUCCESS', 'FAILED')),
    order_status VARCHAR(20) NOT NULL
        CHECK (order_status IN ('CREATED', 'PROCESSING', 'SHIPPING', 'COMPLETED', 'CANCELLED')),
    total_amount DECIMAL(10,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    paid_at DATETIME NULL,
    completed_at DATETIME NULL,
    CONSTRAINT FK_Orders_Customer
        FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    CONSTRAINT FK_Orders_Address
        FOREIGN KEY (address_id) REFERENCES Address(address_id),
    CONSTRAINT FK_Orders_Manager
        FOREIGN KEY (handled_by) REFERENCES Manager(manager_id),
    CONSTRAINT FK_Orders_Voucher
        FOREIGN KEY (voucher_id) REFERENCES Voucher(voucher_id)
);
GO

/* =====================================================
   ORDER DETAIL
===================================================== */
CREATE TABLE Order_Detail (
    order_detail_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    variant_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price_at_order DECIMAL(10,2) NOT NULL,
    cost_price_at_order DECIMAL(10,2) NOT NULL DEFAULT 0,
    rating INT NULL CHECK (rating BETWEEN 1 AND 5),
    review_comment NVARCHAR(500) NULL,
    reviewed_at DATETIME NULL,
    manager_response INT NULL,
    response_content NVARCHAR(500) NULL,
    responded_at DATETIME NULL,
    CONSTRAINT FK_OrderDetail_Order
        FOREIGN KEY (order_id) REFERENCES Orders(order_id)
        ON DELETE CASCADE,
    CONSTRAINT FK_OrderDetail_Variant
        FOREIGN KEY (variant_id) REFERENCES Product_Variant(variant_id),
    CONSTRAINT FK_OrderDetail_ManagerResponse
        FOREIGN KEY (manager_response) REFERENCES Manager(manager_id)
);
GO

/* =====================================================
   STATISTIC REPORT
===================================================== */
CREATE TABLE Statistic_Report (
    report_id INT IDENTITY(1,1) PRIMARY KEY,
    report_name NVARCHAR(200) NOT NULL,
    period_type VARCHAR(10) NOT NULL
        CHECK (period_type IN ('MONTH', 'YEAR')),
    report_month INT NULL,
    report_year INT NOT NULL CHECK (report_year BETWEEN 2000 AND 2100),
    total_revenue DECIMAL(18,2) NOT NULL DEFAULT 0,
    total_profit DECIMAL(18,2) NOT NULL DEFAULT 0,
    total_orders INT NOT NULL DEFAULT 0,
    completed_orders INT NOT NULL DEFAULT 0,
    cancelled_orders INT NOT NULL DEFAULT 0,
    note NVARCHAR(1000) NULL,
    period_start_at DATETIME NULL,
    period_end_at DATETIME NULL,
    created_by INT NOT NULL,
    status BIT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME NULL,
    CONSTRAINT FK_StatisticReport_Manager
        FOREIGN KEY (created_by) REFERENCES Manager(manager_id),
    CONSTRAINT CK_StatisticReport_Period CHECK (
        (period_type = 'MONTH' AND report_month BETWEEN 1 AND 12)
        OR
        (period_type = 'YEAR' AND report_month IS NULL)
    )
);
GO

/* =====================================================
   STATISTIC REPORT ITEM
===================================================== */
CREATE TABLE Statistic_Report_Item (
    item_id INT IDENTITY(1,1) PRIMARY KEY,
    report_id INT NOT NULL,
    item_type VARCHAR(50) NOT NULL
        CHECK (item_type IN (
            'SUMMARY',
            'TOP_SELLING_PRODUCT',
            'SOLD_PRODUCT_DETAIL',
            'LOW_STOCK_PRODUCT',
            'REVENUE_CHART',
            'PROFIT_CHART',
            'ORDER_CHART'
        )),
    item_label NVARCHAR(200) NOT NULL,
    item_value DECIMAL(18,2) NULL,
    item_text NVARCHAR(1000) NULL,
    ref_id INT NULL,
    display_order INT NOT NULL DEFAULT 1,
    CONSTRAINT FK_StatisticReportItem_Report
        FOREIGN KEY (report_id) REFERENCES Statistic_Report(report_id)
        ON DELETE CASCADE
);
GO

/* =====================================================
   INDEXES
===================================================== */
CREATE INDEX IX_Orders_Created_Payment_OrderStatus
ON Orders(created_at, payment_status, order_status);
GO

CREATE INDEX IX_Orders_CompletedAt_OrderStatus
ON Orders(completed_at, order_status);
GO

CREATE INDEX IX_OrderDetail_Order_Variant
ON Order_Detail(order_id, variant_id);
GO

CREATE INDEX IX_ProductVariant_Stock
ON Product_Variant(stock);
GO

CREATE INDEX IX_Voucher_Code_Status
ON Voucher(code, status);
GO

CREATE INDEX IX_CustomerVoucher_Customer_Status_EffectiveTo
ON Customer_Voucher(customer_id, status, effective_to);
GO

CREATE INDEX IX_Customers_Status
ON Customers(status);
GO

CREATE INDEX IX_ImportOrder_Status_CreatedAt
ON Import_Order(status, created_at);
GO

CREATE INDEX IX_ImportOrder_Supplier
ON Import_Order(supplier_id);
GO

CREATE INDEX IX_ImportOrderDetail_Order
ON Import_Order_Detail(import_order_id, variant_id);
GO

CREATE INDEX IX_StatisticReport_Period
ON Statistic_Report(period_type, report_year, report_month);
GO

CREATE INDEX IX_StatisticReport_CreatedBy
ON Statistic_Report(created_by);
GO

CREATE INDEX IX_StatisticReport_Status
ON Statistic_Report(status);
GO

CREATE INDEX IX_StatisticReportItem_Report
ON Statistic_Report_Item(report_id, item_type, display_order);
GO

INSERT INTO Supplier (name, phone, address, status)
VALUES 
(N'Beauty Skin Korea Co., Ltd.', '0901112233', N'Hanoi, Vietnam', 1),
(N'Green Cosmetics Joint Stock Company', '0912223344', N'Ho Chi Minh City, Vietnam', 1),
(N'Sakura Japan Cosmetics Co., Ltd.', '0923334455', N'Da Nang, Vietnam', 1),
(N'Luxury Beauty Distribution Co., Ltd.', '0934445566', N'Hai Phong, Vietnam', 1),
(N'Organic Care Cosmetics JSC', '0945556677', N'Can Tho, Vietnam', 1),
(N'Euro Beauty Cosmetics Co., Ltd.', '0956667788', N'Binh Duong, Vietnam', 1),
(N'K-Beauty Vietnam Co., Ltd.', '0967778899', N'Hanoi, Vietnam', 1),
(N'Rose Cosmetics Trading JSC', '0978889900', N'Quang Ninh, Vietnam', 0),
(N'Dermacos Skincare Co., Ltd.', '0989990011', N'Nghe An, Vietnam', 1),
(N'Glow Up Cosmetics Distribution Co., Ltd.', '0990001122', N'Ho Chi Minh City, Vietnam', 1);

INSERT INTO Brand (name, status) VALUES
(N'L’Oréal', 1),
(N'Innisfree', 1),
(N'Laneige', 1),
(N'La Roche-Posay', 1),
(N'The Ordinary', 1),
(N'3CE', 1),
(N'COSRX', 1),
(N'Vichy', 1);

INSERT INTO Category (name, status) VALUES
(N'Skincare', 1),
(N'Makeup', 1),
(N'Haircare', 1),
(N'Body Care', 1),
(N'Suncare', 1);

INSERT INTO Product (name, description, brand_id, category_id, status) VALUES
(N'L’Oréal Revitalift HA Serum',
 N'An intensive hydrating serum formulated with pure Hyaluronic Acid that deeply penetrates the skin to provide long-lasting moisture, improve skin elasticity, and visibly reduce fine lines for a plumper and smoother complexion.',
 1, 1, 1),

(N'L’Oréal Glycolic Bright Toner',
 N'A brightening facial toner enriched with Glycolic Acid to gently exfoliate dead skin cells, refine skin texture, and enhance overall radiance, leaving the skin looking clearer and more luminous.',
 1, 1, 1),

(N'L’Oréal Micellar Water',
 N'A gentle yet effective micellar cleansing water that removes makeup, dirt, and excess oil without rinsing, suitable for all skin types including sensitive skin.',
 1, 1, 1),

(N'L’Oréal UV Defender SPF50+',
 N'A lightweight sunscreen with very high SPF50+ protection that shields the skin from harmful UVA and UVB rays while preventing premature aging and dark spots.',
 1, 5, 1),

(N'L’Oréal Hydra Fresh Gel',
 N'A refreshing hydrating gel moisturizer that delivers instant cooling hydration, absorbs quickly into the skin, and leaves no sticky residue.',
 1, 1, 1),

(N'L’Oréal Clay Mask Detox',
 N'A detoxifying clay mask infused with natural mineral clays that deeply cleanse pores, absorb excess oil, and help purify the skin for a clearer appearance.',
 1, 1, 1),

(N'L’Oréal Revitalift Eye Cream',
 N'A revitalizing eye cream designed to target fine lines, dark circles, and puffiness while providing intense hydration to the delicate eye area.',
 1, 1, 1),

(N'L’Oréal Infallible Foundation',
 N'A long-wear liquid foundation offering full coverage with a breathable texture that resists sweat and humidity, ensuring a flawless complexion throughout the day.',
 1, 2, 1),

(N'L’Oréal Rouge Signature',
 N'A lightweight matte liquid lipstick with intense pigment payoff that delivers vibrant color while feeling comfortable and non-drying on the lips.',
 1, 2, 1),

(N'L’Oréal Extraordinary Oil',
 N'A nourishing hair oil enriched with precious botanical oils that smooth frizz, enhance shine, and deeply condition dry and damaged hair.',
 1, 3, 1),

(N'L’Oréal Elseve Shampoo',
 N'A daily shampoo formulated to gently cleanse the scalp and hair while restoring softness, strength, and natural shine.',
 1, 3, 1),

(N'L’Oréal Elseve Conditioner',
 N'A complementary hair conditioner that detangles, smooths, and deeply nourishes hair fibers for silky and manageable hair.',
 1, 3, 1),

(N'L’Oréal Men Expert Cleanser',
 N'A facial cleanser specially designed for men that removes impurities and excess oil while refreshing the skin without causing dryness.',
 1, 1, 1),

(N'L’Oréal White Perfect Cream',
 N'A brightening cream that helps reduce the appearance of dark spots and uneven skin tone while providing lasting hydration.',
 1, 1, 1),

 (N'L’Oréal Pure Clay Foam Cleanser',
 N'A foaming facial cleanser enriched with pure clays that deeply cleanses pores while maintaining skin moisture balance.',
 1, 1, 1),

(N'Innisfree Green Tea Serum',
 N'A lightweight hydrating serum formulated with Jeju green tea extract that replenishes moisture, strengthens the skin barrier, and leaves the skin feeling fresh, soft, and balanced.',
 2, 1, 1),

(N'Innisfree Green Tea Toner',
 N'A refreshing toner infused with antioxidant-rich green tea from Jeju Island to hydrate, soothe, and prep the skin for better absorption of subsequent skincare steps.',
 2, 1, 1),

(N'Innisfree Bija Trouble Gel',
 N'A calming gel treatment designed for acne-prone skin that helps reduce breakouts, control excess oil, and soothe irritation without clogging pores.',
 2, 1, 1),

(N'Innisfree Jeju Volcanic Mask',
 N'A deep-cleansing clay mask containing Jeju volcanic clusters that absorb excess sebum, remove impurities from pores, and improve skin clarity.',
 2, 1, 1),

(N'Innisfree Blueberry Cleanser',
 N'A mildly acidic facial cleanser enriched with blueberry extract to gently cleanse, maintain skin balance, and protect the skin from environmental stressors.',
 2, 1, 1),

(N'Innisfree Orchid Cream',
 N'A nourishing anti-aging cream formulated with Jeju orchid extract to improve skin elasticity, smooth fine lines, and deliver long-lasting hydration.',
 2, 1, 1),

(N'Innisfree Sunscreen SPF50+',
 N'A daily sunscreen offering broad-spectrum SPF50+ protection that shields the skin from harmful UV rays while maintaining a lightweight and comfortable finish.',
 2, 5, 1),

(N'Innisfree No Sebum Powder',
 N'A cult-favorite loose powder that effectively controls oil and shine, keeps makeup in place, and leaves a smooth, matte finish throughout the day.',
 2, 2, 1),

(N'Innisfree My Lip Balm',
 N'A nourishing lip balm enriched with natural plant oils that hydrates, softens, and protects lips from dryness.',
 2, 2, 1),

(N'Innisfree Hair Recipe Oil',
 N'A lightweight hair oil that smooths frizz, adds natural shine, and deeply nourishes dry and damaged hair without weighing it down.',
 2, 3, 1),

(N'Innisfree Body Cleanser',
 N'A gentle body wash formulated with natural ingredients that cleanse the skin effectively while maintaining moisture and softness.',
 2, 4, 1),

(N'Innisfree Hand Cream',
 N'A fast-absorbing hand cream that delivers intensive moisture and nourishment, leaving hands soft and lightly scented.',
 2, 4, 1),

(N'Innisfree Aloe Revital Gel',
 N'A multi-purpose soothing gel containing aloe vera extract that hydrates, calms irritated skin, and provides a cooling sensation.',
 2, 1, 1),

(N'Innisfree Pore Clay Mask',
 N'A pore-refining clay mask that helps minimize the appearance of pores, absorb excess oil, and improve overall skin texture.',
 2, 1, 1),

(N'Innisfree Forest For Men',
 N'A refreshing skincare solution for men that hydrates, soothes post-shave irritation, and revitalizes tired-looking skin.',
 2, 1, 1),

(N'Laneige Water Sleeping Mask',
 N'An overnight hydrating mask powered by moisture wrap technology that deeply hydrates the skin while you sleep, helping restore clarity, softness, and a well-rested glow by morning.',
 3, 1, 1),

(N'Laneige Lip Sleeping Mask',
 N'A nourishing overnight lip mask enriched with berry extracts and vitamin C to gently exfoliate and intensely moisturize lips, leaving them smooth, soft, and supple.',
 3, 2, 1),

(N'Laneige Cream Skin Refiner',
 N'A unique toner-moisturizer hybrid that delivers deep hydration and strengthens the skin barrier with a creamy yet lightweight texture suitable for sensitive skin.',
 3, 1, 1),

(N'Laneige Water Bank Cream',
 N'A rich moisturizing cream formulated with hydro-ionized mineral water to provide long-lasting hydration and improve skin moisture retention.',
 3, 1, 1),

(N'Laneige Water Bank Serum',
 N'A lightweight hydrating serum that boosts skin moisture levels, enhances elasticity, and leaves the skin feeling plump and refreshed.',
 3, 1, 1),

(N'Laneige Perfect Renew Cream',
 N'An advanced anti-aging cream designed to improve firmness, smooth fine lines, and restore youthful-looking skin with continuous hydration.',
 3, 1, 1),

(N'Laneige Perfect Renew Eye Cream',
 N'A targeted eye cream that helps reduce the appearance of wrinkles, dark circles, and signs of fatigue around the delicate eye area.',
 3, 1, 1),

(N'Laneige Neo Cushion Matte',
 N'A long-lasting cushion foundation with a soft matte finish that delivers buildable coverage while controlling oil and maintaining a fresh look.',
 3, 2, 1),

(N'Laneige Neo Cushion Glow',
 N'A hydrating cushion foundation that provides radiant coverage and a dewy glow while evening out skin tone.',
 3, 2, 1),

(N'Laneige Layering Lip Bar',
 N'A creamy lipstick with rich pigmentation that glides smoothly onto lips, providing both vibrant color and moisturizing comfort.',
 3, 2, 1),

(N'Laneige Skin Veil Base',
 N'A lightweight makeup base that smooths skin texture, enhances makeup longevity, and creates a natural luminous finish.',
 3, 2, 1),

(N'Laneige Essential Power Skin',
 N'A hydrating toner that revitalizes dull skin, boosts moisture levels, and prepares the skin for subsequent skincare steps.',
 3, 1, 1),

(N'Laneige Water Gel Cleanser',
 N'A gentle gel cleanser that effectively removes impurities and makeup residue while maintaining the skin’s natural moisture balance.',
 3, 1, 1),

(N'Laneige Homme Active Water',
 N'A refreshing toner for men that hydrates, energizes, and soothes skin after cleansing or shaving.',
 3, 1, 1),

(N'Laneige UV Defense SPF50+',
 N'A daily sunscreen offering broad-spectrum SPF50+ protection with a lightweight, non-sticky texture suitable for everyday wear.',
 3, 5, 1),


(N'LRP Effaclar Gel',
 N'A purifying facial cleansing gel specifically formulated for oily and acne-prone skin that gently removes impurities and excess sebum without disrupting the skin’s natural balance.',
 4, 1, 1),

(N'LRP Effaclar Duo+',
 N'A corrective anti-acne treatment that helps reduce blemishes, unclog pores, and prevent the recurrence of breakouts while soothing irritated skin.',
 4, 1, 1),

(N'LRP Toleriane Cleanser',
 N'A gentle, fragrance-free facial cleanser designed for sensitive skin that effectively cleanses while maintaining optimal skin comfort and hydration.',
 4, 1, 1),

(N'LRP Toleriane Ultra Cream',
 N'A soothing moisturizing cream developed for ultra-sensitive and allergy-prone skin to help calm irritation, reduce redness, and restore skin barrier function.',
 4, 1, 1),

(N'LRP Cicaplast Baume B5',
 N'A multi-purpose repairing balm enriched with panthenol (Vitamin B5) that helps soothe, protect, and accelerate skin recovery for irritated or damaged skin.',
 4, 1, 1),

(N'LRP Anthelios SPF50+',
 N'A high-protection sunscreen offering broad-spectrum SPF50+ protection against UVA and UVB rays, specially formulated for sensitive and sun-intolerant skin.',
 4, 5, 1),

(N'LRP Anthelios XL Gel',
 N'A lightweight sunscreen gel with very high sun protection that absorbs quickly without leaving white marks, suitable for oily and combination skin.',
 4, 5, 1),

(N'LRP Pure Vitamin C10',
 N'A powerful antioxidant serum containing pure Vitamin C to brighten the complexion, smooth fine lines, and enhance skin radiance.',
 4, 1, 1),

(N'LRP Hyalu B5 Serum',
 N'A hydrating and repairing serum formulated with hyaluronic acid and vitamin B5 to plump the skin, reduce wrinkles, and improve elasticity.',
 4, 1, 1),

(N'LRP Micellar Water',
 N'A gentle micellar cleansing water that effectively removes makeup and impurities while respecting the skin’s physiological pH balance.',
 4, 1, 1),

(N'LRP Effaclar Toner',
 N'A clarifying toner that helps tighten pores, control excess oil, and refine skin texture for a smoother and clearer appearance.',
 4, 1, 1),

(N'LRP Respectissime Mascara',
 N'A high-tolerance mascara specifically designed for sensitive eyes that provides length and definition without irritation.',
 4, 2, 1),

(N'LRP Lipikar Baume AP+',
 N'A nourishing body balm formulated to relieve very dry and irritated skin, restore lipid balance, and provide long-lasting comfort.',
 4, 4, 1),

(N'LRP Lipikar Syndet AP+',
 N'A gentle cleansing cream for face and body that cleanses without soap, helping to protect the skin barrier and reduce dryness.',
 4, 4, 1),

(N'LRP Kerium Shampoo',
 N'A dermatological shampoo designed to help eliminate dandruff, soothe itchy scalp, and restore scalp comfort.',
 4, 3, 1),


(N'TO Hyaluronic Acid 2% + B5',
 N'A lightweight hydrating serum formulated with multiple forms of hyaluronic acid and vitamin B5 to deeply hydrate, plump the skin, and improve overall skin texture.',
 5, 1, 1),

(N'TO Niacinamide 10% + Zinc',
 N'A high-strength serum designed to reduce excess oil, minimize the appearance of pores, and improve skin clarity while strengthening the skin barrier.',
 5, 1, 1),

(N'TO Alpha Arbutin 2%',
 N'A targeted brightening serum formulated with alpha arbutin to visibly reduce dark spots and promote a more even and radiant skin tone.',
 5, 1, 1),

(N'TO Glycolic Acid 7% Toner',
 N'An exfoliating toning solution with glycolic acid that helps improve skin texture, refine pores, and enhance skin radiance with regular use.',
 5, 1, 1),

(N'TO AHA 30% + BHA 2%',
 N'A powerful exfoliating peeling solution formulated to improve skin texture, clear clogged pores, and enhance overall skin brightness.',
 5, 1, 1),

(N'TO Caffeine Solution 5%',
 N'A lightweight eye serum formulated with caffeine to help reduce the appearance of dark circles, puffiness, and tired-looking eyes.',
 5, 1, 1),

(N'TO Retinol 0.5% in Squalane',
 N'A water-free serum containing retinol to help reduce fine lines, improve skin smoothness, and support skin renewal.',
 5, 1, 1),

(N'TO Vitamin C Suspension 23%',
 N'A potent vitamin C suspension that visibly brightens skin tone, improves signs of aging, and delivers strong antioxidant benefits.',
 5, 1, 1),

(N'TO Natural Moisturizing Factors',
 N'A non-greasy moisturizing cream that supports the skin’s natural hydration system and helps maintain a healthy, resilient skin barrier.',
 5, 1, 1),

(N'TO Squalane Cleanser',
 N'A gentle, soap-free facial cleanser that effectively removes makeup and impurities while leaving the skin soft and hydrated.',
 5, 1, 1),

(N'TO Salicylic Acid 2%',
 N'A targeted treatment formulated to exfoliate inside pores, reduce blemishes, and improve the appearance of congested skin.',
 5, 1, 1),

(N'TO Azelaic Acid Suspension',
 N'A multi-functional brightening cream that helps improve skin texture, reduce redness, and enhance skin clarity.',
 5, 1, 1),

(N'TO Marine Hyaluronics',
 N'An ultra-lightweight hydrating serum inspired by the skin’s natural moisturizing factors to provide instant hydration without heaviness.',
 5, 1, 1),

(N'TO Multi-Peptide Serum',
 N'A comprehensive anti-aging serum formulated with multiple peptide technologies to improve skin firmness and reduce visible signs of aging.',
 5, 1, 1),

(N'TO Sunscreen SPF30',
 N'A daily mineral-based sunscreen that provides broad-spectrum protection against UVA and UVB rays while remaining lightweight and non-greasy.',
 5, 5, 1),

 

(N'3CE Velvet Lip Tint',
 N'A highly pigmented velvet lip tint that delivers rich color payoff with a smooth, lightweight texture for long-lasting comfortable wear.',
 6, 2, 1),

(N'3CE Soft Lip Lacquer',
 N'A glossy lip lacquer that combines vibrant color with a glass-like shine, keeping lips hydrated and visually plump.',
 6, 2, 1),

(N'3CE Mood Recipe Lipstick',
 N'A creamy lipstick collection inspired by soft mood tones, offering a comfortable matte finish with intense pigmentation.',
 6, 2, 1),

(N'3CE Take A Layer Tint',
 N'A buildable lip tint designed for layering, allowing customizable color intensity with a lightweight, non-sticky feel.',
 6, 2, 1),

(N'3CE Blush Cushion',
 N'A cushion-type blush that delivers natural, dewy color to the cheeks while blending seamlessly into the skin.',
 6, 2, 1),

(N'3CE Face Blush',
 N'A finely milled powder blush that provides a soft-focus finish and long-lasting color for a fresh, youthful look.',
 6, 2, 1),

(N'3CE Back To Baby Glow Beam',
 N'A luminous highlighter cream that enhances facial features with a radiant glow and smooth, blendable texture.',
 6, 2, 1),

(N'3CE Eye Switch',
 N'A liquid glitter eyeshadow designed to add instant sparkle and dimension to eye makeup with minimal effort.',
 6, 2, 1),

(N'3CE Multi Eye Color Palette',
 N'A versatile eyeshadow palette featuring a harmonious range of colors for creating both everyday and bold makeup looks.',
 6, 2, 1),

(N'3CE Slim Velvet Lip Color',
 N'A slim lipstick with a soft velvet matte finish that glides effortlessly onto the lips for precise and elegant application.',
 6, 2, 1),

(N'3CE Take A Layer Multi Pot',
 N'A multi-use product suitable for lips, cheeks, and eyes, offering blendable color and a natural, cohesive makeup look.',
 6, 2, 1),

(N'3CE Cover Pot Concealer',
 N'A high-coverage concealer that effectively camouflages imperfections while maintaining a natural skin-like finish.',
 6, 2, 1),

(N'3CE Natural Finish Powder',
 N'A lightweight setting powder that controls shine and smooths skin texture for a soft, matte finish.',
 6, 2, 1),

(N'3CE Makeup Fixer Mist',
 N'A setting mist that locks makeup in place, enhances longevity, and delivers a fresh, natural finish.',
 6, 2, 1),

(N'3CE Body Perfume Mist',
 N'A refreshing body mist infused with a subtle fragrance that leaves the skin delicately scented and revitalized.',
 6, 4, 1),

 

(N'COSRX Low pH Cleanser',
 N'A gentle daily facial cleanser formulated with a low pH level to maintain skin balance while effectively removing impurities.',
 7, 1, 1),

(N'COSRX Advanced Snail 96',
 N'A lightweight essence enriched with 96% snail mucin to deeply hydrate, repair damaged skin, and improve overall elasticity.',
 7, 1, 1),

(N'COSRX Snail 92 Cream',
 N'A nourishing moisturizer containing 92% snail mucin that strengthens the skin barrier and provides long-lasting hydration.',
 7, 1, 1),

(N'COSRX BHA Blackhead Power',
 N'A chemical exfoliant with BHA that helps unclog pores, reduce blackheads, and smooth uneven skin texture.',
 7, 1, 1),

(N'COSRX AHA Whitehead Power',
 N'A gentle AHA toner designed to exfoliate dead skin cells, improve skin clarity, and promote a brighter complexion.',
 7, 1, 1),

(N'COSRX Centella Blemish Cream',
 N'A soothing spot treatment formulated with Centella Asiatica to calm redness and accelerate skin recovery.',
 7, 1, 1),

(N'COSRX Acne Pimple Patch',
 N'Hydrocolloid patches that protect blemishes, absorb impurities, and speed up the healing process overnight.',
 7, 1, 1),

(N'COSRX Propolis Light Ampoule',
 N'A highly concentrated ampoule infused with propolis extract to enhance skin radiance and strengthen moisture retention.',
 7, 1, 1),

(N'COSRX Full Fit Propolis Cream',
 N'A rich yet lightweight cream that deeply nourishes the skin while improving elasticity and natural glow.',
 7, 1, 1),

(N'COSRX Hydrium Toner',
 N'A deeply hydrating toner formulated with vitamin B5 to replenish moisture and soothe dry, sensitive skin.',
 7, 1, 1),

(N'COSRX Hydrium Cream',
 N'A moisture-locking cream that delivers intense hydration while maintaining a breathable, non-greasy finish.',
 7, 1, 1),

(N'COSRX Aloe Soothing Sunscreen',
 N'A lightweight sunscreen enriched with aloe vera that provides broad-spectrum UV protection while soothing the skin.',
 7, 5, 1),

(N'COSRX Pure Fit Cica Serum',
 N'A calming serum formulated with Centella complex to reduce irritation and reinforce the skin’s natural barrier.',
 7, 1, 1),

(N'COSRX Salicylic Cleanser',
 N'A foam cleanser containing salicylic acid to help control excess oil, prevent breakouts, and cleanse pores effectively.',
 7, 1, 1),

(N'COSRX Honey Mask',
 N'A wash-off mask enriched with honey extract that delivers intensive nourishment and restores skin vitality.',
 7, 1, 1),

 

(N'Vichy Mineral 89',
 N'A daily boosting serum formulated with 89% Vichy volcanic mineralizing water and hyaluronic acid to strengthen the skin barrier and boost hydration.',
 8, 1, 1),

(N'Vichy Normaderm Cleanser',
 N'A purifying facial cleanser designed for oily and acne-prone skin, helping to remove excess sebum and prevent breakouts.',
 8, 1, 1),

(N'Vichy Normaderm Phytosolution',
 N'A corrective treatment that targets acne imperfections while providing hydration and improving overall skin texture.',
 8, 1, 1),

(N'Vichy Liftactiv Supreme',
 N'An anti-aging moisturizer that visibly smooths wrinkles, firms the skin, and enhances radiance with continuous use.',
 8, 1, 1),

(N'Vichy Liftactiv Vitamin C',
 N'A brightening serum enriched with pure vitamin C to improve skin tone, reduce dullness, and promote a youthful glow.',
 8, 1, 1),

(N'Vichy Aqualia Thermal Cream',
 N'A deeply hydrating cream infused with Vichy thermal water to restore moisture balance and strengthen the skin barrier.',
 8, 1, 1),

(N'Vichy Eau Thermale',
 N'A soothing thermal water spray that helps calm, refresh, and protect sensitive skin from environmental stressors.',
 8, 1, 1),

(N'Vichy Dercos Shampoo',
 N'A dermatologist-recommended anti-dandruff shampoo that helps eliminate flakes and maintain a healthy scalp.',
 8, 3, 1),

(N'Vichy Dercos Conditioner',
 N'A nourishing conditioner designed to complement anti-dandruff treatments while softening and strengthening hair.',
 8, 3, 1),

(N'Vichy Capital Soleil SPF50+',
 N'A high-protection sunscreen formulated to defend the skin against harmful UVA and UVB rays while maintaining comfort.',
 8, 5, 1),

(N'Vichy Ideal Soleil Milk',
 N'A lightweight sunscreen milk that provides broad-spectrum sun protection with a smooth, non-sticky finish.',
 8, 5, 1),

(N'Vichy Homme Cleanser',
 N'A facial cleanser specifically developed for men to remove impurities while respecting sensitive skin.',
 8, 1, 1),

(N'Vichy Homme Deo Roll-on',
 N'A long-lasting roll-on deodorant that provides effective odor control while being gentle on sensitive skin.',
 8, 4, 1),

(N'Vichy Body Milk',
 N'A nourishing body lotion that delivers long-lasting hydration and leaves the skin soft, smooth, and comfortable.',
 8, 4, 1),

(N'Vichy Hand Cream',
 N'A restorative hand cream formulated to repair dry, damaged hands and protect the skin barrier.',
 8, 4, 1),


 (N'L’Oréal Revitalift Night Cream',
 N'A powerful anti-aging night cream that works overnight to reduce wrinkles and improve skin firmness.',
 1, 1, 1),

(N'L’Oréal Bright Reveal Peel Toner',
 N'A gentle exfoliating toner with acids that helps smooth skin texture and enhance radiance.',
 1, 1, 1),

(N'L’Oréal Voluminous Mascara',
 N'A volumizing mascara that delivers bold lashes with intense color and long-lasting wear.',
 1, 2, 1),

(N'L’Oréal Color Riche Nude Lipstick',
 N'A creamy lipstick infused with nourishing oils for comfortable wear and rich nude shades.',
 1, 2, 1),

(N'L’Oréal Hydra Genius Aloe Water',
 N'A lightweight moisturizer with aloe water designed to provide long-lasting hydration.',
 1, 1, 1),

(N'L’Oréal Paris Brow Artist Pencil',
 N'A precision eyebrow pencil that shapes and defines brows naturally.',
 1, 2, 1),

(N'L’Oréal Dream Lengths Mask',
 N'A repairing hair mask designed to strengthen and protect long hair from breakage.',
 1, 3, 1),

(N'L’Oréal Men Expert Hydra Energetic',
 N'A refreshing moisturizer for men that combats signs of fatigue and dryness.',
 1, 1, 1),

(N'L’Oréal UV Defender Matte SPF50+',
 N'A matte sunscreen formulated to protect skin from UV damage while controlling excess oil.',
 1, 5, 1),

(N'L’Oréal UV Perfect Milk',
 N'A lightweight sunscreen milk that offers high sun protection with a non-greasy finish, ideal for daily use under makeup.',
 1, 5, 1),

  (N'Innisfree Green Tea Seed Cream',
 N'A hydrating cream powered by green tea extracts to lock in moisture and improve skin softness.',
 2, 1, 1),

(N'Innisfree Jeju Cherry Blossom Lotion',
 N'A brightening body lotion that enhances skin radiance while delivering lightweight hydration.',
 2, 4, 1),

(N'Innisfree Black Tea Youth Enhancing Ampoule',
 N'A concentrated ampoule formulated to improve skin elasticity and reduce early signs of aging.',
 2, 1, 1),

(N'Innisfree Volcanic Pore Cleansing Foam',
 N'A deep cleansing foam that removes impurities and excess oil from pores.',
 2, 1, 1),

(N'Innisfree Dewy Tint Lip Balm',
 N'A moisturizing tinted lip balm that provides sheer color and hydration.',
 2, 2, 1),

(N'Innisfree Forest For Men Shaving Foam',
 N'A soothing shaving foam that protects skin from irritation and razor burn.',
 2, 1, 1),

(N'Innisfree Apple Seed Cleansing Oil',
 N'A refreshing cleansing oil that effectively dissolves makeup and sunscreen.',
 2, 1, 1),

(N'Innisfree Bija Cica Balm',
 N'A calming balm designed to soothe acne-prone and irritated skin.',
 2, 1, 1),

(N'Innisfree My Hair Recipe Shampoo',
 N'A nourishing shampoo formulated with botanical ingredients for healthy hair.',
 2, 3, 1),

(N'Innisfree Eco Safety Sun Stick SPF50+',
 N'A convenient sun stick offering strong UV protection with a non-greasy finish.',
 2, 5, 1),

  (N'Laneige Radian-C Cream',
 N'A brightening cream enriched with vitamin C derivatives to improve skin clarity.',
 3, 1, 1),

(N'Laneige Hydro UV Defense SPF50+',
 N'A moisturizing sunscreen that provides strong UV protection while hydrating the skin.',
 3, 5, 1),

(N'Laneige Cream Skin Milk Oil Cleanser',
 N'A gentle cleanser that melts makeup while maintaining skin moisture.',
 3, 1, 1),

(N'Laneige Bouncy & Firm Sleeping Mask',
 N'A firming sleeping mask that helps improve skin elasticity overnight.',
 3, 1, 1),

(N'Laneige Neo Foundation Matte',
 N'A long-lasting matte foundation offering smooth coverage and oil control.',
 3, 2, 1),

(N'Laneige Lip Glowy Balm',
 N'A glossy lip balm that delivers hydration and a natural shine.',
 3, 2, 1),

(N'Laneige Water Bank Eye Gel',
 N'A cooling eye gel that hydrates and refreshes tired-looking eyes.',
 3, 1, 1),

(N'Laneige Homme Blue Energy Lotion',
 N'A lightweight lotion for men that hydrates and energizes the skin.',
 3, 1, 1),

(N'Laneige Skin Veil Tone-Up',
 N'A tone-up base that brightens complexion and preps skin for makeup.',
 3, 2, 1),

(N'Laneige Essential Balancing Emulsion',
 N'A moisturizing emulsion that softens skin and enhances hydration.',
 3, 1, 1),

  (N'LRP Effaclar Astringent Lotion',
 N'A refining toner designed to visibly reduce pores and control oil.',
 4, 1, 1),

(N'LRP Redermic R Retinol',
 N'A retinol treatment that helps reduce wrinkles and refine skin texture.',
 4, 1, 1),

(N'LRP Pure Vitamin C Eyes',
 N'An eye treatment that targets fine lines and dullness.',
 4, 1, 1),

(N'LRP Cicaplast Gel B5',
 N'A soothing gel that accelerates skin repair after irritation.',
 4, 1, 1),

(N'LRP Anthelios Invisible Spray SPF50+',
 N'A lightweight sunscreen spray with high protection for sensitive skin.',
 4, 5, 1),

(N'LRP Physiological Cleansing Gel',
 N'A gentle gel cleanser suitable for sensitive and reactive skin.',
 4, 1, 1),

(N'LRP Lipikar Wash AP+',
 N'A lipid-replenishing body wash designed for dry and eczema-prone skin.',
 4, 4, 1),

(N'LRP Hydraphase Intense Light',
 N'A lightweight moisturizer delivering long-lasting hydration.',
 4, 1, 1),

(N'LRP Effaclar Clay Mask',
 N'A purifying mask that absorbs excess sebum and unclogs pores.',
 4, 1, 1),

(N'LRP Kerium DS Intensive',
 N'An intensive scalp treatment targeting dandruff and itchiness.',
 4, 3, 1),

 (N'TO Mandelic Acid 10% + HA',
 N'A gentle exfoliating serum formulated to improve skin texture and tone.',
 5, 1, 1),

(N'TO Lactic Acid 5% + HA',
 N'A mild exfoliant designed for sensitive skin.',
 5, 1, 1),

(N'TO Rose Hip Seed Oil',
 N'A nourishing facial oil rich in antioxidants.',
 5, 1, 1),

(N'TO Granactive Retinoid 2%',
 N'A gentle retinoid solution for anti-aging benefits.',
 5, 1, 1),

(N'TO Amino Acids + B5',
 N'A hydrating solution that supports skin barrier health.',
 5, 1, 1),

(N'TO Buffet + Copper Peptides',
 N'A multi-peptide serum designed to improve skin firmness.',
 5, 1, 1),

(N'TO 100% Plant-Derived Squalane',
 N'A lightweight oil that hydrates and softens skin.',
 5, 1, 1),

(N'TO Ascorbyl Glucoside 12%',
 N'A stable vitamin C serum that brightens skin tone.',
 5, 1, 1),

(N'TO EUK 134 0.1%',
 N'A powerful antioxidant serum protecting skin from environmental stress.',
 5, 1, 1),

(N'TO SPF Mineral UV Filters',
 N'A mineral sunscreen providing broad-spectrum protection.',
 5, 5, 1),

 (N'3CE Glow Lip Color',
 N'A glossy lip product delivering vibrant color and shine.',
 6, 2, 1),

(N'3CE Mood Recipe Face Blush',
 N'A soft powder blush for natural-looking cheeks.',
 6, 2, 1),

(N'3CE Liquid Primer',
 N'A smoothing primer that enhances makeup longevity.',
 6, 2, 1),

(N'3CE Eye Brow Fixer',
 N'A clear brow gel for long-lasting hold.',
 6, 2, 1),

(N'3CE Velvet Pressed Powder',
 N'A lightweight powder that controls shine and sets makeup.',
 6, 2, 1),

(N'3CE Soft Matte Foundation',
 N'A matte foundation offering smooth coverage.',
 6, 2, 1),

(N'3CE Take A Layer Blush',
 N'A versatile cream blush with buildable color.',
 6, 2, 1),

(N'3CE Eye Palette Mini',
 N'A compact eyeshadow palette with versatile shades.',
 6, 2, 1),

(N'3CE Tint Remover',
 N'A gentle lip makeup remover.',
 6, 2, 1),

(N'3CE Fragrance Body Lotion',
 N'A scented body lotion that hydrates and refreshes skin.',
 6, 4, 1),

 (N'COSRX Galactomyces Essence',
 N'A brightening essence that improves skin tone and texture.',
 7, 1, 1),

(N'COSRX Propolis Toner',
 N'A nourishing toner infused with propolis extract.',
 7, 1, 1),

(N'COSRX Hyaluronic Hydra Ampoule',
 N'A moisture-boosting ampoule delivering intense hydration.',
 7, 1, 1),

(N'COSRX AC Collection Cleanser',
 N'A gentle cleanser for acne-prone skin.',
 7, 1, 1),

(N'COSRX Balancium Comfort Cream',
 N'A barrier-strengthening cream for sensitive skin.',
 7, 1, 1),

(N'COSRX Two In One Poreless Power',
 N'A toner-essence hybrid to tighten pores.',
 7, 1, 1),

(N'COSRX Green Hero Calming Pad',
 N'A soothing toner pad infused with centella.',
 7, 1, 1),

(N'COSRX Vitamin E Vitalizing Cream',
 N'A nourishing cream formulated with vitamin E.',
 7, 1, 1),

(N'COSRX Rice Sleeping Mask',
 N'A brightening overnight mask.',
 7, 1, 1),

(N'COSRX Shield Fit Sunscreen SPF50+',
 N'A lightweight sunscreen suitable for daily wear.',
 7, 5, 1),

 (N'Vichy Minéral 89 Eyes',
 N'A hydrating eye cream that strengthens the delicate eye area.',
 8, 1, 1),

(N'Vichy Normaderm Mask',
 N'A detoxifying mask designed for oily skin.',
 8, 1, 1),

(N'Vichy Liftactiv Collagen Specialist',
 N'A firming cream targeting visible signs of collagen loss.',
 8, 1, 1),

(N'Vichy Aqualia Thermal Serum',
 N'A refreshing serum that boosts hydration.',
 8, 1, 1),

(N'Vichy Capital Soleil Stick SPF50+',
 N'A portable sun stick offering high protection.',
 8, 5, 1),

(N'Vichy Dercos Energy Shampoo',
 N'A strengthening shampoo that reduces hair loss.',
 8, 3, 1),

(N'Vichy Homme After Shave Balm',
 N'A soothing balm to calm skin after shaving.',
 8, 1, 1),

(N'Vichy Ideal Body Serum Milk',
 N'A fast-absorbing body moisturizer.',
 8, 4, 1),

(N'Vichy Pureté Thermale Cleanser',
 N'A gentle cleanser removing impurities and makeup.',
 8, 1, 1),

(N'Vichy Stress Resist Roll-On',
 N'An anti-perspirant roll-on offering long-lasting protection.',
 8, 4, 1);

INSERT INTO Product_Variant
(product_id, variant_name, price, stock, status)
VALUES
(1, N'30ml', 399000, 100, 1),
(1, N'50ml', 599000, 80, 1),
(2, N'200ml', 329000, 120, 1),
(3, N'100ml', 189000, 200, 1),
(3, N'400ml', 329000, 150, 1),
(4, N'40ml', 349000, 90, 1),
(5, N'50ml', 299000, 110, 1),
(6, N'100ml', 259000, 95, 1),
(7, N'15ml', 289000, 70, 1),
(8, N'Light', 459000, 60, 1),
(8, N'Natural', 459000, 55, 1),
(9, N'116 I Enjoy', 279000, 100, 1),
(9, N'121 I Choose', 279000, 95, 1),
(10, N'100ml', 299000, 85, 1),
(11, N'400ml', 219000, 130, 1),
(12, N'400ml', 219000, 130, 1),
(13, N'100ml', 249000, 90, 1),
(14, N'50ml', 329000, 75, 1),
(15, N'50ml', 359000, 80, 1),
(16, N'30ml', 429000, 100, 1),
(16, N'50ml', 629000, 70, 1),
(17, N'200ml', 349000, 120, 1),
(18, N'40ml', 289000, 90, 1),
(19, N'100ml', 259000, 110, 1),
(20, N'150ml', 219000, 130, 1),
(21, N'50ml', 499000, 75, 1),
(22, N'50ml', 349000, 90, 1),
(23, N'No Sebum', 179000, 150, 1),
(24, N'3.5g', 129000, 200, 1),
(25, N'100ml', 299000, 85, 1),
(26, N'250ml', 189000, 140, 1),
(27, N'50ml', 99000, 180, 1),
(28, N'300ml', 129000, 160, 1),
(29, N'100ml', 279000, 95, 1),
(30, N'120ml', 319000, 90, 1),
(31, N'70ml', 599000, 80, 1),
(32, N'20g', 499000, 90, 1),
(33, N'150ml', 429000, 100, 1),
(34, N'50ml', 649000, 70, 1),
(35, N'40ml', 699000, 60, 1),
(36, N'50ml', 799000, 55, 1),
(37, N'20ml', 599000, 75, 1),
(38, N'21N', 799000, 65, 1),
(39, N'21C', 799000, 65, 1),
(40, N'No.2', 429000, 85, 1),
(41, N'30ml', 499000, 90, 1),
(42, N'150ml', 379000, 110, 1),
(43, N'200ml', 329000, 120, 1),
(44, N'150ml', 399000, 100, 1),
(45, N'50ml', 389000, 95, 1),
(46, N'200ml', 329000, 120, 1),
(47, N'40ml', 429000, 80, 1),
(48, N'200ml', 349000, 110, 1),
(49, N'40ml', 529000, 70, 1),
(50, N'100ml', 389000, 85, 1),
(51, N'50ml', 449000, 90, 1),
(52, N'50ml', 429000, 90, 1),
(53, N'30ml', 899000, 55, 1),
(54, N'30ml', 899000, 55, 1),
(55, N'400ml', 379000, 130, 1),
(56, N'200ml', 329000, 120, 1),
(57, N'Black', 459000, 60, 1),
(58, N'200ml', 429000, 100, 1),
(59, N'400ml', 389000, 130, 1),
(60, N'200ml', 319000, 110, 1),
(61, N'30ml', 299000, 100, 1),
(62, N'30ml', 279000, 110, 1),
(63, N'30ml', 329000, 90, 1),
(64, N'240ml', 349000, 120, 1),
(65, N'30ml', 289000, 95, 1),
(66, N'30ml', 259000, 100, 1),
(67, N'30ml', 339000, 85, 1),
(68, N'30ml', 329000, 90, 1),
(69, N'100ml', 299000, 110, 1),
(70, N'50ml', 279000, 120, 1),
(71, N'30ml', 269000, 100, 1),
(72, N'30ml', 299000, 95, 1),
(73, N'30ml', 289000, 100, 1),
(74, N'30ml', 359000, 80, 1),
(75, N'50ml', 329000, 90, 1),
(76, N'Soft Pink', 299000, 120, 1),
(77, N'Coral', 319000, 110, 1),
(78, N'Mood Rose', 349000, 100, 1),
(79, N'Red Tint', 289000, 130, 1),
(80, N'Peach', 399000, 90, 1),
(81, N'Rose', 359000, 95, 1),
(82, N'Glow', 429000, 80, 1),
(83, N'Champagne', 329000, 100, 1),
(84, N'Warm Brown', 699000, 70, 1),
(85, N'Velvet Red', 329000, 110, 1),
(86, N'Coral Peach', 349000, 105, 1),
(87, N'Natural', 299000, 120, 1),
(88, N'Translucent', 279000, 130, 1),
(89, N'Fixer Mist', 259000, 140, 1),
(90, N'Body Mist', 289000, 150, 1),
(91, N'150ml', 249000, 120, 1),
(92, N'100ml', 359000, 100, 1),
(93, N'100ml', 389000, 90, 1),
(94, N'100ml', 329000, 110, 1),
(95, N'100ml', 329000, 110, 1),
(96, N'30g', 289000, 95, 1),
(97, N'24 patches', 89000, 200, 1),
(98, N'30ml', 379000, 90, 1),
(99, N'50ml', 399000, 85, 1),
(100, N'150ml', 279000, 120, 1),
(101, N'50ml', 359000, 95, 1),
(102, N'50ml', 289000, 100, 1),
(103, N'50ml', 329000, 90, 1),
(104, N'150ml', 239000, 130, 1),
(105, N'100ml', 299000, 110, 1),
(106, N'50ml', 789000, 70, 1),
(107, N'200ml', 349000, 120, 1),
(108, N'50ml', 459000, 90, 1),
(109, N'50ml', 899000, 60, 1),
(110, N'20ml', 789000, 70, 1),
(111, N'50ml', 549000, 80, 1),
(112, N'150ml', 259000, 130, 1),
(113, N'200ml', 329000, 120, 1),
(114, N'200ml', 349000, 115, 1),
(115, N'50ml', 499000, 85, 1),
(116, N'100ml', 459000, 90, 1),
(117, N'150ml', 299000, 110, 1),
(118, N'50ml', 259000, 120, 1),
(119, N'200ml', 279000, 130, 1),
(120, N'50ml', 239000, 140, 1),
(1, N'100ml', 799000, 50, 1),
(2, N'400ml', 529000, 80, 1),
(3, N'200ml', 259000, 120, 1),
(4, N'80ml', 499000, 60, 1),
(5, N'100ml', 459000, 70, 1),
(6, N'200ml', 399000, 75, 1),
(7, N'30ml', 399000, 60, 1),
(16, N'100ml', 829000, 50, 1),
(17, N'400ml', 549000, 70, 1),
(18, N'80ml', 399000, 65, 1),
(19, N'200ml', 459000, 80, 1),
(20, N'300ml', 299000, 100, 1),
(21, N'100ml', 799000, 55, 1),
(22, N'80ml', 449000, 60, 1),
(31, N'120ml', 699000, 60, 1),
(33, N'300ml', 599000, 70, 1),
(34, N'100ml', 899000, 45, 1),
(35, N'80ml', 949000, 40, 1),
(46, N'400ml', 529000, 70, 1),
(48, N'400ml', 549000, 65, 1),
(51, N'100ml', 649000, 55, 1),
(52, N'100ml', 629000, 60, 1),
(61, N'60ml', 499000, 60, 1),
(62, N'60ml', 469000, 70, 1),
(63, N'60ml', 529000, 55, 1),
(64, N'480ml', 549000, 80, 1),
(65, N'60ml', 489000, 65, 1),
(91, N'300ml', 349000, 90, 1),
(92, N'200ml', 499000, 70, 1),
(93, N'200ml', 529000, 60, 1),
(94, N'200ml', 449000, 75, 1),
(106, N'100ml', 1099000, 40, 1),
(109, N'100ml', 1299000, 35, 1),
(115, N'100ml', 699000, 55, 1),
(121, N'30ml', 499000, 80, 1),
(121, N'50ml', 699000, 60, 1),
(122, N'50ml', 459000, 90, 1),
(123, N'150ml', 399000, 100, 1),
(123, N'300ml', 599000, 70, 1),
(124, N'Shade 01', 429000, 85, 1),
(124, N'Shade 02', 429000, 80, 1),
(125, N'50ml', 389000, 90, 1),
(126, N'30ml', 359000, 100, 1),
(126, N'100ml', 559000, 65, 1),
(127, N'200ml', 299000, 120, 1),
(128, N'250ml', 329000, 110, 1),
(129, N'50ml', 419000, 85, 1),
(130, N'40ml', 459000, 90, 1),
(130, N'80ml', 659000, 60, 1),
(131, N'50ml', 479000, 85, 1),
(132, N'200ml', 319000, 110, 1),
(132, N'400ml', 499000, 75, 1),
(133, N'30ml', 699000, 65, 1),
(134, N'150ml', 289000, 120, 1),
(135, N'4g', 199000, 150, 1),
(136, N'150ml', 329000, 100, 1),
(136, N'300ml', 529000, 70, 1),
(137, N'40ml', 299000, 110, 1),
(138, N'30ml', 349000, 95, 1),
(139, N'300ml', 259000, 130, 1),
(140, N'20g', 389000, 80, 1),
(140, N'40g', 589000, 55, 1),
(141, N'50ml', 749000, 65, 1),
(142, N'40ml', 699000, 70, 1),
(143, N'150ml', 529000, 90, 1),
(143, N'250ml', 749000, 60, 1),
(144, N'80ml', 899000, 55, 1),
(145, N'Shade 21', 829000, 60, 1),
(145, N'Shade 23', 829000, 55, 1),
(146, N'10g', 399000, 100, 1),
(147, N'30ml', 499000, 85, 1),
(148, N'200ml', 579000, 75, 1),
(149, N'150ml', 469000, 90, 1),
(150, N'50ml', 599000, 70, 1),
(151, N'200ml', 389000, 120, 1),
(152, N'30ml', 699000, 65, 1),
(152, N'50ml', 899000, 45, 1),
(153, N'15ml', 549000, 60, 1),
(154, N'40ml', 589000, 75, 1),
(155, N'150ml', 329000, 110, 1),
(156, N'400ml', 579000, 80, 1),
(157, N'50ml', 649000, 70, 1),
(158, N'100ml', 499000, 90, 1),
(159, N'50ml', 599000, 65, 1),
(160, N'200ml', 349000, 120, 1),
(161, N'30ml', 299000, 120, 1),
(161, N'60ml', 499000, 75, 1),
(162, N'30ml', 279000, 130, 1),
(163, N'30ml', 329000, 100, 1),
(164, N'240ml', 349000, 110, 1),
(165, N'30ml', 289000, 105, 1),
(165, N'60ml', 489000, 70, 1),
(166, N'30ml', 259000, 120, 1),
(167, N'30ml', 339000, 95, 1),
(168, N'30ml', 329000, 100, 1),
(169, N'50ml', 379000, 85, 1),
(170, N'50ml', 359000, 90, 1),
(171, N'Soft Pink', 299000, 130, 1),
(172, N'Coral', 319000, 120, 1),
(173, N'Mood Rose', 349000, 110, 1),
(173, N'Deep Rose', 349000, 100, 1),
(174, N'Red Tint', 289000, 140, 1),
(175, N'Peach', 399000, 95, 1),
(176, N'Rose', 359000, 100, 1),
(177, N'Glow', 429000, 85, 1),
(178, N'Champagne', 329000, 110, 1),
(179, N'Warm Brown', 699000, 70, 1),
(180, N'Velvet Red', 329000, 120, 1),
(181, N'150ml', 249000, 130, 1),
(182, N'100ml', 359000, 100, 1),
(182, N'200ml', 559000, 70, 1),
(183, N'100ml', 389000, 90, 1),
(184, N'30g', 289000, 95, 1),
(185, N'24 patches', 89000, 200, 1),
(186, N'50ml', 399000, 85, 1),
(187, N'150ml', 279000, 120, 1),
(188, N'50ml', 359000, 95, 1),
(189, N'200ml', 329000, 110, 1),
(190, N'50ml', 459000, 80, 1),
(190, N'100ml', 699000, 55, 1),
(191, N'200ml', 349000, 120, 1),
(192, N'150ml', 259000, 130, 1),
(193, N'50ml', 499000, 85, 1),
(194, N'100ml', 459000, 90, 1),
(195, N'150ml', 299000, 110, 1),
(196, N'50ml', 259000, 120, 1),
(197, N'200ml', 279000, 130, 1),
(198, N'50ml', 239000, 140, 1);

USE CosmeticShopDB_v8;
GO

-- N?u c?n seed l?i t? d?u thì m? 2 dòng du?i
-- DELETE FROM Product_Image;
-- GO

DECLARE @pid INT;
DECLARE @maxPid INT;

SELECT 
  @pid = MIN(product_id),
  @maxPid = MAX(product_id)
FROM Product;

WHILE @pid IS NOT NULL AND @pid <= @maxPid
BEGIN
  /* ===== IMAGE 1: MAIN (100%) ===== */
  INSERT INTO Product_Image (product_id, image_url, is_main)
  VALUES (
    @pid,
    CONCAT('product_', @pid, '_1.jpg'),
    1
  );

  /* ===== IMAGE 2: ~70% PRODUCT ===== */
  IF (@pid % 10) NOT IN (1, 3, 7)
  BEGIN
    INSERT INTO Product_Image (product_id, image_url, is_main)
    VALUES (
      @pid,
      CONCAT('product_', @pid, '_2.jpg'),
      0
    );
  END

  /* ===== IMAGE 3: ~40% PRODUCT ===== */
  IF (@pid % 5) IN (0, 2)
  BEGIN
    INSERT INTO Product_Image (product_id, image_url, is_main)
    VALUES (
      @pid,
      CONCAT('product_', @pid, '_3.jpg'),
      0
    );
  END

  SET @pid += 1;
END;
GO

UPDATE Product_Variant
SET avg_cost = ROUND(price * 0.7, 0)
WHERE avg_cost = 0;

USE CosmeticShopDB_v8;
GO

SET NOCOUNT ON;
GO

USE CosmeticShopDB_v8;
GO

SET NOCOUNT ON;
GO

/* =====================================================
   RESET TEST DATA FOR STATISTICS
===================================================== */

IF NOT EXISTS (SELECT 1 FROM Manager WHERE email = 'admin@averis.com')
BEGIN
    INSERT INTO Manager (full_name, email, password, manager_role, status)
    VALUES (N'System Admin', 'admin@averis.com', '123456', 'ADMIN', 1);
END;

IF NOT EXISTS (SELECT 1 FROM Manager WHERE email = 'staff@averis.com')
BEGIN
    INSERT INTO Manager (full_name, email, password, manager_role, status)
    VALUES (N'Statistic Staff', 'staff@averis.com', '123456', 'STAFF', 1);
END;
GO

IF NOT EXISTS (SELECT 1 FROM Customers WHERE username = 'stat_user_01')
BEGIN
    INSERT INTO Customers
    (username, full_name, email, password, avatar, gender, date_of_birth, status, email_verified, auth_token, auth_token_type, auth_token_expired_at, auth_token_used)
    VALUES
    ('stat_user_01', N'Nguyen Van A', 'stat_user_01@test.com', '123456', NULL, 'MALE', '1998-05-10', 1, 1, NULL, NULL, NULL, 0);
END;

IF NOT EXISTS (SELECT 1 FROM Customers WHERE username = 'stat_user_02')
BEGIN
    INSERT INTO Customers
    (username, full_name, email, password, avatar, gender, date_of_birth, status, email_verified, auth_token, auth_token_type, auth_token_expired_at, auth_token_used)
    VALUES
    ('stat_user_02', N'Tran Thi B', 'stat_user_02@test.com', '123456', NULL, 'FEMALE', '1999-08-20', 1, 1, NULL, NULL, NULL, 0);
END;

IF NOT EXISTS (SELECT 1 FROM Customers WHERE username = 'stat_user_03')
BEGIN
    INSERT INTO Customers
    (username, full_name, email, password, avatar, gender, date_of_birth, status, email_verified, auth_token, auth_token_type, auth_token_expired_at, auth_token_used)
    VALUES
    ('stat_user_03', N'Le Hoang C', 'stat_user_03@test.com', '123456', NULL, 'OTHER', '2000-01-15', 1, 1, NULL, NULL, NULL, 0);
END;
GO

DECLARE @c1 INT = (SELECT customer_id FROM Customers WHERE username = 'stat_user_01');
DECLARE @c2 INT = (SELECT customer_id FROM Customers WHERE username = 'stat_user_02');
DECLARE @c3 INT = (SELECT customer_id FROM Customers WHERE username = 'stat_user_03');

IF NOT EXISTS (SELECT 1 FROM Address WHERE customer_id = @c1)
BEGIN
    INSERT INTO Address
    (customer_id, receiver_name, phone, province, district, ward, street_address, is_default, is_deleted)
    VALUES
    (@c1, N'Nguyen Van A', '0901000001', N'Ho Chi Minh', N'Quan 1', N'Ben Nghe', N'12 Nguyen Hue', 1, 0);
END;

IF NOT EXISTS (SELECT 1 FROM Address WHERE customer_id = @c2)
BEGIN
    INSERT INTO Address
    (customer_id, receiver_name, phone, province, district, ward, street_address, is_default, is_deleted)
    VALUES
    (@c2, N'Tran Thi B', '0901000002', N'Ho Chi Minh', N'Quan 3', N'Vo Thi Sau', N'45 Cach Mang Thang 8', 1, 0);
END;

IF NOT EXISTS (SELECT 1 FROM Address WHERE customer_id = @c3)
BEGIN
    INSERT INTO Address
    (customer_id, receiver_name, phone, province, district, ward, street_address, is_default, is_deleted)
    VALUES
    (@c3, N'Le Hoang C', '0901000003', N'Ho Chi Minh', N'Binh Thanh', N'Ward 25', N'99 Dien Bien Phu', 1, 0);
END;
GO

DELETE od
FROM Order_Detail od
JOIN Orders o ON od.order_id = o.order_id
WHERE o.customer_id IN (
    SELECT customer_id FROM Customers
    WHERE username IN ('stat_user_01', 'stat_user_02', 'stat_user_03')
);

DELETE FROM Orders
WHERE customer_id IN (
    SELECT customer_id FROM Customers
    WHERE username IN ('stat_user_01', 'stat_user_02', 'stat_user_03')
);

DELETE FROM Statistic_Report_Item;
DELETE FROM Statistic_Report;
GO

DECLARE @managerId INT = (
    SELECT TOP 1 manager_id
    FROM Manager
    WHERE email IN ('admin@averis.com', 'staff@averis.com')
    ORDER BY CASE WHEN email = 'admin@averis.com' THEN 0 ELSE 1 END
);

DECLARE @a1 INT = (
    SELECT TOP 1 address_id FROM Address
    WHERE customer_id = (SELECT customer_id FROM Customers WHERE username = 'stat_user_01')
);
DECLARE @a2 INT = (
    SELECT TOP 1 address_id FROM Address
    WHERE customer_id = (SELECT customer_id FROM Customers WHERE username = 'stat_user_02')
);
DECLARE @a3 INT = (
    SELECT TOP 1 address_id FROM Address
    WHERE customer_id = (SELECT customer_id FROM Customers WHERE username = 'stat_user_03')
);

DECLARE @u1 INT = (SELECT customer_id FROM Customers WHERE username = 'stat_user_01');
DECLARE @u2 INT = (SELECT customer_id FROM Customers WHERE username = 'stat_user_02');
DECLARE @u3 INT = (SELECT customer_id FROM Customers WHERE username = 'stat_user_03');

DECLARE @Seed TABLE (
    row_no INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT,
    address_id INT,
    variant_id INT,
    quantity INT,
    price_at_order DECIMAL(10,2),
    cost_price_at_order DECIMAL(10,2),
    payment_method VARCHAR(20),
    payment_status VARCHAR(20),
    order_status VARCHAR(20),
    created_at DATETIME,
    paid_at DATETIME NULL,
    completed_at DATETIME NULL
);

INSERT INTO @Seed
(customer_id, address_id, variant_id, quantity, price_at_order, cost_price_at_order, payment_method, payment_status, order_status, created_at, paid_at, completed_at)
VALUES
(@u1, @a1, 1,   2, 399000, 280000, 'COD',  'SUCCESS', 'COMPLETED', '2025-12-20 09:15:00', '2025-12-20 09:20:00', '2025-12-23 15:00:00'),
(@u2, @a2, 16,  1, 429000, 300000, 'MOMO', 'SUCCESS', 'COMPLETED', '2025-12-28 10:00:00', '2025-12-28 10:05:00', '2025-12-30 14:10:00'),

(@u1, @a1, 31,  1, 599000, 420000, 'COD',  'SUCCESS', 'COMPLETED', '2026-01-03 08:30:00', '2026-01-03 08:31:00', '2026-01-05 16:20:00'),
(@u2, @a2, 22,  2, 349000, 240000, 'MOMO', 'SUCCESS', 'COMPLETED', '2026-01-08 11:00:00', '2026-01-08 11:02:00', '2026-01-10 17:00:00'),
(@u3, @a3, 70,  3, 279000, 190000, 'COD',  'SUCCESS', 'COMPLETED', '2026-01-21 09:45:00', '2026-01-21 09:50:00', '2026-01-24 12:30:00'),
(@u3, @a3, 47,  1, 429000, 310000, 'COD',  'FAILED',  'CANCELLED', '2026-01-14 14:00:00', NULL, NULL),

(@u2, @a2, 91,  2, 249000, 170000, 'MOMO', 'SUCCESS', 'COMPLETED', '2026-02-02 10:15:00', '2026-02-02 10:17:00', '2026-02-04 18:00:00'),
(@u3, @a3, 106, 1, 789000, 560000, 'COD',  'SUCCESS', 'COMPLETED', '2026-02-06 13:10:00', '2026-02-06 13:12:00', '2026-02-09 15:40:00'),
(@u1, @a1, 121, 2, 499000, 350000, 'MOMO', 'SUCCESS', 'COMPLETED', '2026-02-15 16:00:00', '2026-02-15 16:01:00', '2026-02-18 11:15:00'),
(@u2, @a2, 140, 1, 389000, 260000, 'COD',  'PENDING', 'PROCESSING', '2026-02-22 09:20:00', NULL, NULL),
(@u3, @a3, 152, 1, 699000, 500000, 'COD',  'FAILED',  'CANCELLED', '2026-02-25 19:10:00', NULL, NULL),

(@u1, @a1, 1,   1, 399000, 280000, 'COD',  'SUCCESS', 'COMPLETED', '2026-03-01 08:10:00', '2026-03-01 08:12:00', '2026-03-03 10:00:00'),
(@u2, @a2, 16,  2, 429000, 300000, 'MOMO', 'SUCCESS', 'COMPLETED', '2026-03-05 10:25:00', '2026-03-05 10:27:00', '2026-03-07 16:45:00'),
(@u3, @a3, 31,  1, 599000, 420000, 'COD',  'SUCCESS', 'COMPLETED', '2026-03-10 15:00:00', '2026-03-10 15:05:00', '2026-03-12 13:10:00'),
(@u1, @a1, 70,  4, 279000, 190000, 'COD',  'SUCCESS', 'COMPLETED', '2026-03-14 09:10:00', '2026-03-14 09:14:00', '2026-03-16 11:50:00'),
(@u2, @a2, 91,  2, 249000, 170000, 'MOMO', 'SUCCESS', 'COMPLETED', '2026-03-18 17:30:00', '2026-03-18 17:32:00', '2026-03-20 14:40:00'),
(@u3, @a3, 106, 1, 789000, 560000, 'COD',  'SUCCESS', 'COMPLETED', '2026-03-22 12:45:00', '2026-03-22 12:50:00', '2026-03-24 18:20:00'),
(@u1, @a1, 121, 3, 499000, 350000, 'COD',  'SUCCESS', 'COMPLETED', '2026-03-25 09:00:00', '2026-03-25 09:03:00', '2026-03-27 16:00:00'),
(@u3, @a3, 152, 1, 699000, 500000, 'COD',  'SUCCESS', 'COMPLETED', '2026-03-28 14:20:00', '2026-03-28 14:22:00', '2026-03-28 18:15:00'),
(@u2, @a2, 140, 1, 389000, 260000, 'MOMO', 'FAILED',  'CANCELLED', '2026-03-26 11:00:00', NULL, NULL),

(@u1, @a1, 22,  2, 349000, 240000, 'COD',  'SUCCESS', 'COMPLETED', '2026-04-02 09:00:00', '2026-04-02 09:02:00', '2026-04-04 14:00:00'),
(@u2, @a2, 70,  2, 279000, 190000, 'MOMO', 'SUCCESS', 'COMPLETED', '2026-04-07 15:30:00', '2026-04-07 15:31:00', '2026-04-09 16:10:00'),
(@u3, @a3, 91,  3, 249000, 170000, 'COD',  'SUCCESS', 'COMPLETED', '2026-04-12 11:20:00', '2026-04-12 11:22:00', '2026-04-14 18:30:00'),
(@u1, @a1, 106, 1, 789000, 560000, 'COD',  'SUCCESS', 'COMPLETED', '2026-04-16 10:00:00', '2026-04-16 10:01:00', '2026-04-18 13:30:00'),
(@u2, @a2, 121, 2, 499000, 350000, 'MOMO', 'SUCCESS', 'COMPLETED', '2026-04-21 08:45:00', '2026-04-21 08:47:00', '2026-04-23 10:10:00'),

(@u1, @a1, 1,   2, 399000, 280000, 'COD',  'SUCCESS', 'COMPLETED', '2026-05-03 09:40:00', '2026-05-03 09:42:00', '2026-05-05 14:50:00'),
(@u2, @a2, 31,  2, 599000, 420000, 'MOMO', 'SUCCESS', 'COMPLETED', '2026-05-06 16:10:00', '2026-05-06 16:12:00', '2026-05-08 17:20:00'),
(@u3, @a3, 70,  5, 279000, 190000, 'COD',  'SUCCESS', 'COMPLETED', '2026-05-10 13:30:00', '2026-05-10 13:33:00', '2026-05-12 19:00:00'),
(@u1, @a1, 106, 1, 789000, 560000, 'COD',  'SUCCESS', 'COMPLETED', '2026-05-15 12:00:00', '2026-05-15 12:01:00', '2026-05-18 11:00:00'),
(@u2, @a2, 152, 1, 699000, 500000, 'MOMO', 'SUCCESS', 'COMPLETED', '2026-05-20 10:20:00', '2026-05-20 10:22:00', '2026-05-22 15:40:00');

DECLARE
    @row INT = 1,
    @maxRow INT,
    @customer_id INT,
    @address_id INT,
    @variant_id INT,
    @quantity INT,
    @price_at_order DECIMAL(10,2),
    @cost_price_at_order DECIMAL(10,2),
    @payment_method VARCHAR(20),
    @payment_status VARCHAR(20),
    @order_status VARCHAR(20),
    @created_at DATETIME,
    @paid_at DATETIME,
    @completed_at DATETIME,
    @order_id INT,
    @total_amount DECIMAL(10,2);

SELECT @maxRow = MAX(row_no) FROM @Seed;

WHILE @row <= @maxRow
BEGIN
    SELECT
        @customer_id = customer_id,
        @address_id = address_id,
        @variant_id = variant_id,
        @quantity = quantity,
        @price_at_order = price_at_order,
        @cost_price_at_order = cost_price_at_order,
        @payment_method = payment_method,
        @payment_status = payment_status,
        @order_status = order_status,
        @created_at = created_at,
        @paid_at = paid_at,
        @completed_at = completed_at
    FROM @Seed
    WHERE row_no = @row;

    SET @total_amount = @quantity * @price_at_order;

    INSERT INTO Orders
    (customer_id, address_id, handled_by, voucher_id, discount_amount, payment_method, payment_status, order_status, total_amount, created_at, paid_at, completed_at)
    VALUES
    (@customer_id, @address_id, @managerId, NULL, 0, @payment_method, @payment_status, @order_status, @total_amount, @created_at, @paid_at, @completed_at);

    SET @order_id = SCOPE_IDENTITY();

    INSERT INTO Order_Detail
    (order_id, variant_id, quantity, price_at_order, cost_price_at_order, rating, review_comment, reviewed_at, manager_response, response_content, responded_at)
    VALUES
    (@order_id, @variant_id, @quantity, @price_at_order, @cost_price_at_order, NULL, NULL, NULL, NULL, NULL, NULL);

    SET @row += 1;
END;
GO

SELECT
    YEAR(ISNULL(o.completed_at, o.created_at)) AS stat_year,
    MONTH(ISNULL(o.completed_at, o.created_at)) AS stat_month,
    SUM(o.total_amount) AS total_revenue,
    SUM((od.price_at_order - od.cost_price_at_order) * od.quantity) AS total_profit,
    COUNT(DISTINCT o.order_id) AS completed_orders
FROM Orders o
JOIN Order_Detail od ON o.order_id = od.order_id
WHERE o.order_status = 'COMPLETED'
  AND o.customer_id IN (
      SELECT customer_id FROM Customers
      WHERE username IN ('stat_user_01', 'stat_user_02', 'stat_user_03')
  )
GROUP BY YEAR(ISNULL(o.completed_at, o.created_at)),
         MONTH(ISNULL(o.completed_at, o.created_at))
ORDER BY stat_year, stat_month;
GO