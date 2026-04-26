USE [hrms]
GO

/****** Object:  Table [dbo].[dispensed_reciept_based_purchase_fund_records_dual]    Script Date: 11/26/2025 8:46:25 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[dispensed_reciept_based_purchase_fund_records_dual](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[request_id] [varchar](50) NOT NULL,
	[requisition_unit] [varchar](100) NULL,
	[reason] [varchar](max) NULL,
	[payee] [varchar](100) NULL,
	[requested_amount] [decimal](10, 2) NULL,
	[given_amount] [decimal](10, 2) NULL,
	[given_by] [varchar](100) NULL,
	[request_date] [date] NULL,
	[completed_date] [date] NULL,
	[payee_signature] [varbinary](max) NULL,
	[dispenser_signature] [varbinary](max) NULL,
	[payee_fingerprint] [varbinary](max) NULL,
	[dispenser_fingerprint] [varbinary](max) NULL,
	[verification_method] [varchar](50) NULL,
	[created_at] [datetime] NULL,
	[approval_status] [varchar](50) NULL,
	[approved_by] [varchar](100) NULL,
	[approver_name] [varchar](100) NULL,
	[approver_employee_id] [varchar](50) NULL,
	[approver_department] [varchar](100) NULL,
	[approver_signature] [varbinary](max) NULL,
	[approver_fingerprint] [varbinary](max) NULL,
	[approval_notes] [varchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[dispensed_reciept_based_purchase_fund_records_dual] ADD  DEFAULT (getdate()) FOR [created_at]
GO

ALTER TABLE [dbo].[dispensed_reciept_based_purchase_fund_records_dual]  WITH CHECK ADD  CONSTRAINT [FK_dispensed_reciept_based_purchase_fund_request] FOREIGN KEY([request_id])
REFERENCES [dbo].[reciept_based_purchase_fund_requests] ([request_id])
GO

ALTER TABLE [dbo].[dispensed_reciept_based_purchase_fund_records_dual] CHECK CONSTRAINT [FK_dispensed_reciept_based_purchase_fund_request]
GO


