USE [hrms]
GO

/****** Object:  Table [dbo].[agreement_based_purchase_fund_requests_payee_id_cards]    Script Date: 11/26/2025 8:43:54 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[agreement_based_purchase_fund_requests_payee_id_cards](
	[file_id] [int] IDENTITY(1,1) NOT NULL,
	[request_id] [varchar](50) NOT NULL,
	[file_name] [varchar](255) NOT NULL,
	[file_type] [varchar](50) NULL,
	[file_data] [varbinary](max) NOT NULL,
	[uploaded_at] [datetime] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[file_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[agreement_based_purchase_fund_requests_payee_id_cards] ADD  DEFAULT (getdate()) FOR [uploaded_at]
GO

ALTER TABLE [dbo].[agreement_based_purchase_fund_requests_payee_id_cards]  WITH CHECK ADD  CONSTRAINT [FK_agreement_files_request] FOREIGN KEY([request_id])
REFERENCES [dbo].[agreement_based_purchase_fund_requests] ([request_id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[agreement_based_purchase_fund_requests_payee_id_cards] CHECK CONSTRAINT [FK_agreement_files_request]
GO


