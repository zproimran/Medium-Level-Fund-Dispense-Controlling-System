USE [hrms]
GO

/****** Object:  Table [dbo].[employee_signatures]    Script Date: 11/26/2025 8:46:44 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[employee_signatures](
	[employee_id] [varchar](20) NOT NULL,
	[employee_name] [varchar](100) NOT NULL,
	[department] [varchar](50) NOT NULL,
	[position] [varchar](50) NOT NULL,
	[enrollment_date] [date] NOT NULL,
	[status] [varchar](20) NOT NULL,
	[fingerprint_template] [varbinary](max) NULL,
	[signature_image] [varbinary](max) NULL,
	[created_date] [datetime] NULL,
	[last_updated] [datetime] NULL,
	[username] [varchar](50) NULL,
	[role] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[employee_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[employee_signatures] ADD  DEFAULT (getdate()) FOR [created_date]
GO

ALTER TABLE [dbo].[employee_signatures] ADD  DEFAULT (getdate()) FOR [last_updated]
GO


