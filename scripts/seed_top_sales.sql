/* Seed data to create at least 30 top-selling products */
USE CosmeticShopDB_v4;
GO

SET NOCOUNT ON;

DECLARE @addressId INT;
DECLARE @customerId INT;

SELECT TOP 1
    @addressId = a.address_id,
    @customerId = a.customer_id
FROM Address a
JOIN Customers c ON c.customer_id = a.customer_id
WHERE c.status = 1
ORDER BY a.address_id;

IF @addressId IS NULL OR @customerId IS NULL
BEGIN
    PRINT N'No valid Address/Customer found. Please seed Customers + Address first.';
    RETURN;
END;

IF OBJECT_ID('tempdb..#TopProducts') IS NOT NULL
    DROP TABLE #TopProducts;

SELECT TOP 30
    p.product_id,
    pv.variant_id,
    pv.price,
    ROW_NUMBER() OVER (ORDER BY p.product_id) AS rn
INTO #TopProducts
FROM Product p
CROSS APPLY (
    SELECT TOP 1 v.variant_id, v.price
    FROM Product_Variant v
    WHERE v.product_id = p.product_id
      AND v.status = 1
      AND v.stock >= 20
    ORDER BY v.variant_id
) pv
WHERE p.status = 1
ORDER BY p.product_id;

IF (SELECT COUNT(*) FROM #TopProducts) < 30
BEGIN
    PRINT N'Not enough active products/variants (need at least 30 with stock >= 20).';
    RETURN;
END;

DECLARE @i INT = 1;
DECLARE @max INT = (SELECT MAX(rn) FROM #TopProducts);
DECLARE @orderId INT;
DECLARE @variantId INT;
DECLARE @price DECIMAL(10,2);
DECLARE @qty INT;
DECLARE @createdAt DATETIME;

BEGIN TRAN;

BEGIN TRY
    WHILE @i <= @max
    BEGIN
        SELECT
            @variantId = variant_id,
            @price = price
        FROM #TopProducts
        WHERE rn = @i;

        /* Bigger qty for lower rn so top sales ranking is clear */
        SET @qty = 61 - @i; -- rn=1 => 60, rn=30 => 31
        SET @createdAt = DATEADD(DAY, -@i, GETDATE());

        INSERT INTO Orders (
            customer_id,
            address_id,
            voucher_id,
            discount_amount,
            payment_method,
            payment_status,
            order_status,
            total_amount,
            created_at,
            paid_at
        )
        VALUES (
            @customerId,
            @addressId,
            NULL,
            0,
            'COD',
            'SUCCESS',
            'COMPLETED',
            @price * @qty,
            @createdAt,
            @createdAt
        );

        SET @orderId = SCOPE_IDENTITY();

        INSERT INTO Order_Detail (order_id, variant_id, quantity, price_at_order)
        VALUES (@orderId, @variantId, @qty, @price);

        /* Keep stock consistent with seeded sales */
        UPDATE Product_Variant
        SET stock = stock - @qty
        WHERE variant_id = @variantId
          AND stock >= @qty;

        SET @i = @i + 1;
    END;

    COMMIT TRAN;
    PRINT N'Seed completed: inserted 30 completed orders for top-sales ranking.';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRAN;

    THROW;
END CATCH;

