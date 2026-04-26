USE [hrms]
GO

/****** Object:  Table [dbo].[department]    Script Date: 11/26/2025 8:44:39 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[department](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[department_name] [varchar](100) NULL
) ON [PRIMARY]
GO


