
USE [hrms]
GO
CREATE TABLE VatPurchases (
    Id INT IDENTITY(1,1) PRIMARY KEY,

    VatCategory VARCHAR(50) NOT NULL,
    CalendarType VARCHAR(20) NOT NULL,
    PurchaseType VARCHAR(50) NOT NULL,

    SellerTIN VARCHAR(20),
    SellerName VARCHAR(150),

    PurchaseDate DATE NOT NULL,
    MRCNumber VARCHAR(50),
    ReceiptNumber VARCHAR(50) NOT NULL,

    Description VARCHAR(255),
    UnitMeasure VARCHAR(50),

    Quantity DECIMAL(18,2) NOT NULL,
    UnitPrice DECIMAL(18,2) NOT NULL,
    TotalValue DECIMAL(18,2) NOT NULL,
    VatAmount DECIMAL(18,2) NOT NULL,
    TotalAfterVat DECIMAL(18,2) NOT NULL,

    CreatedAt DATETIME DEFAULT GETDATE(),
    Voided BIT DEFAULT 0,

    CreatedBy VARCHAR(100) NOT NULL
);