USE [hrms]
GO

/****** Object:  Table [dbo].[role_permissions]    Script Date: 11/26/2025 8:56:14 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[role_permissions](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[role_id] [int] NOT NULL,
	[permission_name] [varchar](150) NOT NULL,
	[permission_description] [varchar](255) NULL,
	[is_active] [bit] NULL,
	[created_date] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[role_permissions] ADD  DEFAULT ((1)) FOR [is_active]
GO

ALTER TABLE [dbo].[role_permissions] ADD  DEFAULT (getdate()) FOR [created_date]
GO

ALTER TABLE [dbo].[role_permissions]  WITH CHECK ADD  CONSTRAINT [FK_role_permissions_roles] FOREIGN KEY([role_id])
REFERENCES [dbo].[system_roles] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[role_permissions] CHECK CONSTRAINT [FK_role_permissions_roles]
GO


