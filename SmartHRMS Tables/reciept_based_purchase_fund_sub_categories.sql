USE [hrms]
GO

/****** Object:  Table [dbo].[reciept_based_purchase_fund_sub_categories]    Script Date: 11/26/2025 8:55:21 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[reciept_based_purchase_fund_sub_categories](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[main_category_id] [int] NOT NULL,
	[name] [varchar](150) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[reciept_based_purchase_fund_sub_categories]  WITH CHECK ADD FOREIGN KEY([main_category_id])
REFERENCES [dbo].[reciept_based_purchase_fund_main_categories] ([id])
ON DELETE CASCADE
GO


