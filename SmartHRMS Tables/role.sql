USE [hrms]
GO

/****** Object:  Table [dbo].[role]    Script Date: 11/26/2025 8:55:43 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[role](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[role_name] [varchar](100) NULL
) ON [PRIMARY]
GO


