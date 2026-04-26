USE [hrms]
GO

/****** Object:  Table [dbo].[purchase_fund_reciepts]    Script Date: 11/26/2025 8:50:50 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[purchase_fund_reciepts](
	[reciept_id] [int] IDENTITY(1,1) NOT NULL,
	[request_id] [varchar](50) NOT NULL,
	[file_name] [varchar](255) NOT NULL,
	[file_data] [varbinary](max) NOT NULL,
	[uploaded_by] [varchar](100) NOT NULL,
	[upload_date] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[reciept_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[purchase_fund_reciepts] ADD  DEFAULT (sysdatetime()) FOR [upload_date]
GO

ALTER TABLE [dbo].[purchase_fund_reciepts]  WITH CHECK ADD  CONSTRAINT [FK_purchase_fund_reciepts_request] FOREIGN KEY([request_id])
REFERENCES [dbo].[reciept_based_purchase_fund_requests] ([request_id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[purchase_fund_reciepts] CHECK CONSTRAINT [FK_purchase_fund_reciepts_request]
GO


