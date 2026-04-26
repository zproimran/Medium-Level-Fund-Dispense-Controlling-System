USE [hrms]
GO

/****** Object:  Table [dbo].[purchase_fund_agreements]    Script Date: 11/26/2025 8:50:30 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[purchase_fund_agreements](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[employee_name] [nvarchar](150) NULL,
	[introduction] [nvarchar](max) NULL,
	[purpose] [nvarchar](max) NULL,
	[consent] [nvarchar](max) NULL,
	[parties] [nvarchar](max) NULL,
	[nature_of_work] [nvarchar](max) NULL,
	[employer_rights] [nvarchar](max) NULL,
	[employee_rights] [nvarchar](max) NULL,
	[created_at] [datetime] NULL,
	[employer_duties] [nvarchar](max) NULL,
	[employee_duties] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[purchase_fund_agreements] ADD  DEFAULT (getdate()) FOR [created_at]
GO


