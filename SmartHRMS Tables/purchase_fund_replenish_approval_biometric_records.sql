USE [hrms]
GO

/****** Object:  Table [dbo].[purchase_fund_replenish_approval_biometric_records]    Script Date: 11/26/2025 8:51:10 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[purchase_fund_replenish_approval_biometric_records](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[request_id] [varchar](50) NOT NULL,
	[approver_name] [varchar](100) NULL,
	[approver_employee_id] [varchar](50) NULL,
	[approver_department] [varchar](100) NULL,
	[approver_signature] [varbinary](max) NULL,
	[approver_fingerprint] [varbinary](max) NULL,
	[approval_notes] [varchar](max) NULL,
	[approval_date] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[purchase_fund_replenish_approval_biometric_records] ADD  DEFAULT (getdate()) FOR [approval_date]
GO

ALTER TABLE [dbo].[purchase_fund_replenish_approval_biometric_records]  WITH CHECK ADD  CONSTRAINT [FK_purchase_fund_replenish_approval_request] FOREIGN KEY([request_id])
REFERENCES [dbo].[purchase_fund_replenish_requests] ([request_id])
GO

ALTER TABLE [dbo].[purchase_fund_replenish_approval_biometric_records] CHECK CONSTRAINT [FK_purchase_fund_replenish_approval_request]
GO


