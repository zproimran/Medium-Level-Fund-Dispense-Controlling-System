USE [hrms]
GO

/****** Object:  Table [dbo].[maximum_amount_to_request]    Script Date: 11/26/2025 8:47:06 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[maximum_amount_to_request](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[fund_type] [varchar](100) NULL,
	[maximum_amount] [varchar](100) NULL
) ON [PRIMARY]
GO


