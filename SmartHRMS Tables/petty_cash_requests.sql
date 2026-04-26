USE [hrms]
GO

/****** Object:  Table [dbo].[petty_cash_requests]    Script Date: 11/26/2025 8:50:07 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[petty_cash_requests](
	[request_id] [varchar](50) NOT NULL,
	[requisition_unit] [varchar](100) NOT NULL,
	[reason] [varchar](max) NOT NULL,
	[payee] [varchar](100) NOT NULL,
	[amount_requested] [decimal](10, 2) NOT NULL,
	[request_date] [date] NOT NULL,
	[confirmation_status] [varchar](20) NULL,
	[confirmed_by] [varchar](100) NULL,
	[approval_status] [varchar](20) NULL,
	[approved_by] [varchar](100) NULL,
	[dispensed_status] [varchar](3) NULL,
	[dispensed_by] [varchar](100) NULL,
	[void_status] [varchar](3) NULL,
	[voided_by] [varchar](100) NULL,
	[created_at] [datetime] NULL,
	[updated_at] [datetime] NULL,
	[dispense_approval_status] [varchar](20) NULL,
	[dispense_approved_by] [varchar](100) NULL,
	[void_reason] [varchar](max) NULL,
	[main_category] [varchar](150) NULL,
	[sub_category] [varchar](150) NULL,
PRIMARY KEY CLUSTERED 
(
	[request_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT ('Pending') FOR [confirmation_status]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT ('') FOR [confirmed_by]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT ('Pending') FOR [approval_status]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT ('') FOR [approved_by]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT ('No') FOR [dispensed_status]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT ('') FOR [dispensed_by]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT ('No') FOR [void_status]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT ('') FOR [voided_by]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT (getdate()) FOR [created_at]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  DEFAULT (getdate()) FOR [updated_at]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  CONSTRAINT [DF_petty_cash_requests_dispense_approval_status]  DEFAULT ('Pending') FOR [dispense_approval_status]
GO

ALTER TABLE [dbo].[petty_cash_requests] ADD  CONSTRAINT [DF_petty_cash_requests_dispense_approved_by]  DEFAULT ('') FOR [dispense_approved_by]
GO


