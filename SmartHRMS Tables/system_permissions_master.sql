USE [hrms]
GO

/****** Object:  Table [dbo].[system_permissions_master]    Script Date: 11/26/2025 8:57:03 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[system_permissions_master](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[permission_code] [varchar](100) NOT NULL,
	[permission_name] [varchar](150) NOT NULL,
	[permission_description] [varchar](255) NULL,
	[permission_category] [varchar](100) NULL,
	[is_active] [bit] NULL,
	[created_date] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[permission_code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[system_permissions_master] ADD  DEFAULT ((1)) FOR [is_active]
GO

ALTER TABLE [dbo].[system_permissions_master] ADD  DEFAULT (getdate()) FOR [created_date]
GO


