# Medium-Level-Fund-Dispense-Controlling-System
**1.Agreement Based Purchase Fund Management System  End User Documentation**
Table of Contents
1.	System Overview
2.	User Roles and Permissions
3.	Getting Started
4.	Creating Purchase Fund Requests
5.	Workflow Management
6.	Biometric Signature Verification
7.	Cash Dispensing Process
8.	Reporting and Export
9.	Troubleshooting
10.	Best Practices
________________________________________
System Overview
Purpose
The Agreement-Based Purchase Fund Management System is designed to streamline and secure the process of requesting, approving, and disbursing purchase funds within Afran General Hospital. The system incorporates biometric signature verification to ensure accountability and prevent fraud.
Key Features
•	Biometric Authentication: Fingerprint verification for all approvals
•	Multi-level Approval Workflow: Structured approval process
•	Digital Signatures: Secure electronic signature storage
•	Real-time Tracking: Monitor request status in real-time
•	Comprehensive Reporting: Export capabilities for audit trails
•	Role-based Access Control: Different permissions based on user roles
System Requirements
•	Windows 10/11 operating system
•	ZKTECO fingerprint scanner device
•	Java Runtime Environment 8+
•	Stable internet connection
•	Minimum 4GB RAM
________________________________________
User Roles and Permissions
Administrator
•	Create, edit, void, and delete requests
•	Approve requests and dispense approvals
•	Manage all workflow stages
•	Access all system features
Finance Administrator
•	Create new purchase fund requests
•	Void existing requests
•	View request details
•	Approve requests and approve dispenses
Accountant
•	View request details
•	Confirm requests after approval
•	Access reporting features
Cashier
•	Void requests
•	View request details
•	Dispense cash to approved requests
•	Access basic reporting
________________________________________
Getting Started
1. System Login
•	Launch the Smart HRMS application
•	Enter your username and password
•	The system automatically detects your role and displays appropriate permissions
2. Device Connection
Before starting any operations, ensure the fingerprint device is connected:
1.	Click "Connect Device" button in the toolbar
2.	System will attempt to connect to the fingerprint scanner
3.	Successful connection shows: "Device Status: Connected ✓"
4.	If connection fails, check:
o	USB cable connection
o	Device drivers installation
o	Device not being used by another application
3. Interface Overview
•	Header: Hospital branding and current date
•	Summary Panel: Quick statistics of requests
•	Button Panel: Action buttons based on your role
•	Main Table: List of all purchase fund requests
•	Footer: Device status and system information
________________________________________
Creating Purchase Fund Requests
Step-by-Step Process
1. Initiate New Request
•	Click "New Request" button
•	Fill in the required information:
2. Basic Information
•	Requisition Unit: Select your department/unit
•	Main Category: Choose appropriate category (e.g., Medical Supplies, Equipment)
•	Sub Category: Select specific sub-category
•	Reason: Detailed explanation for the fund request
•	Payee: Select employee who will receive the funds
•	Amount: Enter requested amount in ETB
•	Request Date: Automatically set to current date
3. Agreement Template
The system automatically loads the standard employment agreement template with the following sections:
•	Employee Name
•	Agreement Introduction
•	Purpose of Agreement
•	Mutual Consent
•	Parties Involved
•	Nature of Work
•	Employer Rights and Duties
•	Employee Rights and Duties
4. Document Upload
•	Click "Upload Files" to attach supporting documents
•	Supported formats: PDF, DOCX, JPG, PNG, TXT
•	Multiple files can be uploaded
•	Remove files using the "Remove" button if needed
5. Validation and Submission
•	System validates all required fields
•	Amount is checked against maximum allowed limits
•	Click "OK" to submit the request
•	System generates unique Request ID (format: AGRBPF20231215001)
Request ID Format
text
AGRBPF + YYYYMMDD + Sequential Number
Example: AGRBPF20231215001
________________________________________
Workflow Management
1. Request Approval Process
Step 1: Approve Request (Finance Administrator)
1.	Select the pending request from the table
2.	Click "Approve Request"
3.	Biometric Verification Required:
o	Verify identity using fingerprint scanner
o	System loads approver's digital signature
4.	Enter approval notes (optional)
5.	Click "APPROVE REQUEST"
6.	Status changes from "Pending" to "Approved"
Step 2: Cash Dispensing (Cashier)
1.	Select approved request
2.	Click "Dispense Cash"
3.	Dual Biometric Verification Required:
o	Verify payee identity
o	Verify dispenser identity
4.	Enter dispense details:
o	Given amount
o	Completion date
o	Given by (auto-filled from verified dispenser)
5.	System records dual signatures and timestamps
Step 3: Request Confirmation (Accountant)
1.	Select dispensed request
2.	Click "Confirm Request"
3.	Biometric Verification Required:
o	Verify confirmer identity
o	System loads confirmer's digital signature
4.	Enter confirmation notes (optional)
5.	Click "CONFIRM REQUEST"
Step 4: Dispense Approval (Finance Administrator)
1.	Select confirmed request
2.	Click "Approve Dispense"
3.	Biometric Verification Required:
o	Verify approver identity
o	System loads approver's digital signature
4.	Enter approval notes (optional)
5.	Click "APPROVE DISPENSE"
6.	Workflow completed
Workflow Summary
text
New Request → Approve Request → Dispense Cash → Confirm Request → Approve Dispense → COMPLETED
________________________________________
Biometric Signature Verification
Fingerprint Verification Process
1. Starting Verification
•	Click any verification button (e.g., "Verify Identity & Load Signature")
•	System checks device connection
•	Fingerprint scanner activates
2. Scanning Process
•	Place finger firmly on the scanner
•	Keep finger still during scanning
•	Green status indicates successful capture
•	Red status indicates retry needed
3. Identity Matching
•	System compares captured fingerprint with database
•	Matches against registered employee signatures
•	Loads corresponding digital signature
•	Displays employee name upon successful match
Troubleshooting Biometric Issues
Issue	Solution
Fingerprint not recognized	Clean finger and scanner surface
Multiple failed attempts	Wait 30 seconds and retry
"No matching employee"	Contact system administrator
Device connection lost	Reconnect USB cable and click "Connect Device"
________________________________________
Cash Dispensing Process
Available Amount Management
Automatic Replenishment Status Update
The system automatically manages available amounts and replenishment status:
java
// Enhanced dispense logic with automatic status updates
private void processDispense(AgreementBasedPurchaseFundRecordModel record) {
    // Check current available amount
    double currentAvailable = databaseConnector.getAvailableAgreementBasedPurchaseFundAmount();
    
    // Process dispense
    if (currentAvailable <= 0) {
        // Auto-update replenishment status to "Completed"
        databaseConnector.updateReplenishmentStatus("Completed");
        
        // Load current available amount with status filters:
        // - currentstatus = 'in use'
        // - dispense_status = 'approved'
        currentAvailable = databaseConnector.getCurrentAvailableAmountWithFilters();
    }
    
    // Continue with dispense process...
}
Dispensing Steps
1. Pre-Dispense Checks
•	Verify request is in "Approved" status
•	Confirm fingerprint device is connected
•	Ensure sufficient available funds
2. Dual Verification Process
Payee Verification:
•	Payee places finger on scanner
•	System verifies payee identity
•	Digital signature is loaded and recorded
Dispenser Verification:
•	Cashier places finger on scanner
•	System verifies cashier identity
•	Digital signature is loaded and recorded
3. Dispense Recording
•	Enter actual amount given
•	Verify completion date
•	System automatically records:
o	Timestamp of transaction
o	Both digital signatures
o	Fingerprint templates for audit
o	Dispenser and payee information
4. Post-Dispense Actions
•	Request status updates to "Dispensed"
•	Available amount is automatically deducted
•	If available amount reaches zero, replenishment status changes to "Completed"
•	Audit trail is created for reconciliation
________________________________________
Request Management
Viewing Request Details
1.	Select request from table
2.	Click "View Details"
3.	Comprehensive view includes:
o	Basic request information
o	Status history
o	Agreement details
o	Digital signatures
o	Supporting documents
o	Audit trail
Editing Requests
•	Only available for non-dispensed, non-voided requests
•	Select request and click "Edit Request"
•	Modify necessary fields
•	Save changes
Voiding Requests
1.	Select request
2.	Click "Void Request"
3.	Mandatory: Enter void reason
4.	System records:
o	Void timestamp
o	User who voided request
o	Void reason for audit purposes
Deleting Requests
•	Only available for non-dispensed requests
•	Confirmation dialog appears
•	Permanent deletion with audit trail
________________________________________
Reporting and Export
Available Reports
1. Summary Report
•	Total requests count
•	Pending approvals
•	Approved requests
•	Confirmed requests
•	Dispensed amounts
•	Total financial summary
2. Detailed Request Report
•	Individual request details
•	Complete workflow history
•	Digital signatures
•	Supporting documents
•	Agreement terms
3. Audit Trail Report
•	All system activities
•	User actions timestamps
•	Biometric verification records
•	Status change history
Export Options
PDF Export
1.	Select request(s)
2.	Click "Export To PDF"
3.	System generates comprehensive PDF including:
o	Hospital letterhead
o	Request details
o	Agreement terms
o	All digital signatures
o	Timestamps and approvals
Excel Export
•	Tabular data export
•	Filtering and sorting capabilities
•	Financial summaries
•	Status reports
Word Export
•	Editable document format
•	Customizable reports
•	Template-based output
Print Options
•	Direct printing from application
•	Page setup customization
•	Header/footer configuration
•	Multiple copy printing
________________________________________
Advanced Features
Search and Filtering
Basic Search
•	Search by: Request ID, Payee Name, Requisition Unit
•	Real-time filtering as you type
•	Case-insensitive search
Advanced Search
•	Multiple criteria combination
•	Date range filtering (based on request date)
•	Status-based filtering
•	Amount range filtering
Status Filtering Options
•	All statuses
•	Pending approval
•	Approved
•	Confirmed
•	Dispensed
•	Voided
Data Refresh
•	Click "Refresh Data" to sync with latest database changes
•	Automatic summary updates
•	Real-time status changes
________________________________________
Troubleshooting
Common Issues and Solutions
Device Connection Problems
Problem	Solution
"Device Not Connected"	Click "Connect Device", check USB cable
"Connection Failed"	Restart application, reinstall drivers
Intermittent connection	Use different USB port, check cable quality
Biometric Verification Issues
Problem	Solution
Fingerprint not recognized	Clean finger, use different finger
"No matching employee"	Contact admin to register fingerprints
Slow verification	Ensure good finger placement, adequate pressure
System Performance
Problem	Solution
Slow loading	Close other applications, check internet
Export failures	Check disk space, file permissions
Data not refreshing	Click "Refresh Data", check database connection
Error Messages
Error Message	Meaning	Action Required
"Amount Exceeds Limit"	Requested amount over maximum allowed	Reduce amount or contact administrator
"Device Not Connected"	Fingerprint scanner not detected	Connect device and click "Connect Device"
"No Selection"	No request selected from table	Select a request before performing action
"Already Approved"	Request already in approved status	Move to next workflow step
"Cannot Edit Voided Request"	Attempting to modify voided request	Create new request instead
________________________________________
Best Practices
Security Guidelines
1.	Never share your login credentials
2.	Always verify biometric prompts - ensure you're approving legitimate requests
3.	Log out when not using the system
4.	Report suspicious activities immediately
Data Accuracy
1.	Double-check amounts before submission
2.	Verify payee information matches intended recipient
3.	Upload all required supporting documents
4.	Provide clear and detailed reasons for requests
Workflow Efficiency
1.	Process requests in order of submission date
2.	Use bulk operations for similar requests
3.	Regularly refresh data to see latest status
4.	Utilize search and filter to find specific requests
System Maintenance
1.	Keep the application updated
2.	Regularly backup important exports
3.	Report system issues promptly
4.	Attend training sessions for new features
________________________________________
Support and Contact Information
Technical Support
•	IT Help Desk: Extension 1234
•	Email: it-support@afranhospital.gov.et
•	Office Hours: 8:00 AM - 5:00 PM, Monday-Friday
System Administrators
•	Finance Department: Extension 5678
•	HR Department: Extension 9012
Emergency Contact
For after-hours critical system issues, contact the on-call IT support at: +251-911-123456
________________________________________
Appendix
Keyboard Shortcuts
•	F5: Refresh data
•	Ctrl+F: Open search
•	Ctrl+N: New request
•	Ctrl+E: Export dialog
•	Ctrl+P: Print options
Status Definitions
•	Pending: Waiting for initial approval
•	Approved: Finance administrator approved
•	Confirmed: Accountant verified dispense
•	Dispensed: Cash given to payee
•	Voided: Request cancelled with reason
•	Completed: Full workflow finished
Available Amount Status
•	In Use: Funds currently allocated for active requests
•	Completed: All funds dispensed, replenishment needed
•	Available: Funds ready for new requests



**2.AgreementBased Purchase Fund Analytics Dashboard  End User Documentation**
Table of Contents
1.	System Overview
2.	Dashboard Interface
3.	Data Filtering and Search
4.	Analytics and Charts
5.	Export and Reporting
6.	Advanced Features
7.	Troubleshooting
8.	Best Practices
________________________________________
System Overview
Purpose
The Agreement-Based Purchase Fund Analytics Dashboard provides comprehensive financial analysis and reporting capabilities specifically designed for agreement-based purchase fund management at Afran General Hospital. This system tracks, analyzes, and reports on all agreement-based purchase fund requests with advanced categorization, employee tracking, and comprehensive status monitoring.
Key Features
•	Multi-dimensional Analytics: Table views, pie charts, bar charts, line charts, and advanced analytics
•	Employee-Centric Tracking: Track both payee and employee information
•	Advanced Category Management: Main categories and sub-categories with dynamic filtering
•	Comprehensive Filtering: Date ranges, categories, employee filters, and custom search
•	Multiple Export Formats: Excel, PDF, Word with professional formatting
•	Real-time Data Visualization: Interactive charts and summary metrics
•	Agreement-Specific Analytics: Specialized analysis for agreement-based workflows
User Roles and Permissions
•	All Users: Access to view analytics based on their department/role
•	Finance Team: Full access to financial analytics and exports
•	Department Heads: Access to department-specific data
•	Administrators: Complete system access with all features
•	HR Personnel: Access to employee-related analytics
________________________________________
Dashboard Interface
Main Components
1. Header Section
•	Dashboard Title: "Agreement-Based Purchase Fund Analytics Dashboard"
•	Subtitle: "Comprehensive Financial Analysis and Reporting"
•	Control Buttons: Export options, print, refresh, dark mode toggle
2. Filter Section
•	Date Range: From/To date pickers with calendar interface
•	Text Filters: Unit, payee, employee name, general search
•	Category Filters: Main category and sub-category dropdowns
•	Status Filters: Approval, confirmation, dispensed, dispense approval, void status
•	Action Buttons: Reset filters, advanced filters
3. Main Content Area (Tabbed Interface)
•	Data Table Tab: Detailed record view with sorting and context menu
•	Status Distribution Tab: Pie chart showing request status distribution
•	Monthly Analysis Tab: Bar chart of monthly funding trends
•	Trend Analysis Tab: Line chart for time-based analysis
•	Unit Analysis Tab: Bar chart by requisition unit
•	Category Analysis Tab: Combined pie and bar charts for categories
•	Advanced Analytics Tab: KPIs and detailed metrics with insights
4. Summary Section
•	Real-time summary cards showing key performance indicators
•	Color-coded metrics for quick reference
•	Automatic updates with filter changes
________________________________________
Data Filtering and Search
Employee-Centric Filtering
Employee Information Tracking
•	Payee: Person receiving the funds
•	Employee: Employee associated with the agreement
•	Dual Tracking: Separate filtering for payee and employee
Category-Based Filtering
•	Main Categories: Broad classification groups
•	Sub-Categories: Specific items within main categories
•	Dynamic Relationship: Sub-categories change based on main category selection
Filter Types
1. Date Range Filtering
•	From Date: Start date for analysis period
•	To Date: End date for analysis period
•	Behavior: Inclusive range (includes both start and end dates)
2. Text-based Filters
•	Requisition Unit: Filter by department or organizational unit
•	Payee: Filter by person receiving funds
•	Employee: Filter by employee associated with agreement
•	Quick Search: Searches across all text fields simultaneously
3. Status Filters
•	Approval Status: Pending, Approved, All
•	Confirmation Status: Pending, Confirmed, All
•	Dispensed Status: Yes, No, All
•	Dispense Approval: Pending, Approved, All
•	Void Status: Yes, No, All
Advanced Filtering Features
Combined Filter Logic
•	AND Logic: All active filters must match
•	Real-time Updates: Charts and summary update immediately
•	Progressive Filtering: Start broad and narrow down gradually
Search Capabilities
•	Cross-field Search: Searches all text fields simultaneously
•	Case-insensitive: Search works regardless of capitalization
•	Partial matches: Finds records containing search terms
•	Real-time Results: Instant filtering as you type
Resetting Filters
•	One-click Reset: "Reset Filters" button clears all filters
•	Complete Clear: Returns to showing all records
•	Instant Update: Immediate refresh of all displays
________________________________________
Analytics and Charts
Data Table Tab
Features
•	Sortable Columns: Click any column header to sort
•	Context Menu: Right-click for additional operations
•	Status Coloring: Color-coded status indicators
•	Real-time Count: Shows number of displayed records
Context Menu Options
•	Export Selected: Export chosen records to Excel
•	Copy to Clipboard: Copy record details in tabular format
•	View Details: Show comprehensive record information
Column Information
•	Request ID: Unique identifier for each request
•	Unit: Requisition department/unit
•	Main Category: Primary category classification
•	Sub Category: Specific sub-category
•	Payee: Recipient of funds
•	Employee: Employee associated with agreement
•	Amount: Formatted currency display (ETB)
•	Date: Request submission date
•	Status: Color-coded approval status
•	Dispensed: Dispensation status with color coding
Chart Types and Analysis
1. Status Distribution (Pie Chart)
•	Visualization: Circular chart showing status proportions
•	Data: Count of requests by approval status
•	Colors: Green (Approved), Yellow (Pending), Blue (Confirmed)
•	Interactivity: Hover for exact counts and percentages
2. Monthly Analysis (Bar Chart)
•	X-Axis: Months of the year
•	Y-Axis: Total amount in ETB
•	Purpose: Identify seasonal spending patterns
•	Usage: Budget planning and trend analysis
3. Trend Analysis (Line Chart)
•	X-Axis: Dates formatted as "MMM dd"
•	Y-Axis: Daily total amounts
•	Purpose: Track spending patterns over time
•	Features: Smooth line connecting data points
4. Unit Analysis (Bar Chart)
•	X-Axis: Requisition units/departments
•	Y-Axis: Total amounts by unit
•	Purpose: Identify department spending patterns
•	Usage: Department budget analysis
5. Category Analysis
•	Pie Chart: Distribution of requests across categories
•	Bar Chart: Total spending by category
•	Combined View: Comprehensive category insights
•	Usage: Identify high-spending categories
Advanced Analytics Tab
Key Performance Indicators (KPIs)
•	Approval Rate: Percentage of approved requests
•	Dispensed Rate: Percentage of dispensed requests
•	Average Processing Time: Typical request processing duration
•	Pending Actions: Number requiring attention
Category Insights
•	Detailed Analysis: Spending patterns by category
•	Trend Identification: Category-specific trends
•	Actionable Insights: Recommendations for optimization
Performance Metrics
•	Efficiency Score: Combined metric of approval and completion rates
•	Bottleneck Identification: Process stages causing delays
•	Recommendations: Data-driven improvement suggestions
________________________________________
Export and Reporting
Comprehensive Export Options
1. Excel Export
Features:
•	Multiple Sheets: Executive Summary, Detailed Records, Category Analysis, Status Analysis, Monthly Analysis, Department Analysis, Charts Data
•	Professional Formatting: Headers, colors, and auto-sized columns
•	Formulas and Calculations: Automated summary calculations
•	Chart Data: Underlying data for all visualizations
Export Process:
1.	Click "Export Excel" button
2.	Choose save location and filename
3.	System generates comprehensive workbook
4.	File automatically opens after export
Sheet Structure:
•	Executive Summary: Overview and key metrics
•	Detailed Records: Complete data in tabular format
•	Category Analysis: Breakdown by main and sub-categories
•	Status Analysis: Workflow and status distribution
•	Monthly Analysis: Time-based trends
•	Department Analysis: Unit/department performance
•	Charts Data: Data tables for chart recreation
2. PDF Export
Features:
•	Professional Document: Cover page, table of contents, structured sections
•	Print-ready Format: Optimized for physical printing
•	Comprehensive Content: All analysis and data
•	Signature Sections: Approval and authorization areas
Document Sections:
•	Cover Page: Hospital branding and report metadata
•	Executive Summary: Key findings and metrics
•	Category Analysis: Detailed category breakdown
•	Detailed Records: Complete data listing
•	Analysis & Charts: Analytical insights
•	Signatures: Approval and authorization
3. Word Export
Features:
•	Editable Format: Microsoft Word document
•	Structured Sections: Headings and subheadings
•	Table-based Data: Professional table formatting
•	Business Report Style: Standard business document format
Printing Capabilities
Print Options
•	Print Report: Full dashboard printout
•	Professional Layout: Optimized for paper printing
•	Multi-page Support: Automatic pagination
•	Header/Footer: Professional document formatting
Print Features
•	Cover Page: Hospital branding and title
•	Summary Section: Key metrics and findings
•	Category Analysis: Visual and tabular data
•	Detailed Records: Comprehensive data listing
•	Analysis Section: Insights and recommendations
•	Footer: Confidentiality notice and timestamps
Individual Record Operations
Viewing Record Details
1.	Select record in table
2.	Right-click → "View Details" or use context menu
3.	Comprehensive popup shows all record information
4.	Organized in easy-to-read format
Record Information Displayed
•	Basic Information: ID, Unit, Categories, Payee, Employee, Amount, Date
•	Status Information: Approval, Confirmation, Dispense statuses
•	Personnel Tracking: Approved By, Confirmed By, Dispensed By
•	Additional Data: Void status, Reason for request
Copying to Clipboard
•	Formatted Output: Tab-separated values for easy pasting
•	Complete Information: All key fields included
•	Multi-record Support: Copy multiple selected records
•	Excel-ready Format: Direct paste into spreadsheet applications
________________________________________
Advanced Features
Employee and Payee Tracking
Dual Tracking System
•	Payee Tracking: Person receiving the funds
•	Employee Tracking: Employee associated with agreement
•	Separate Filtering: Independent filtering for payee and employee
•	Relationship Management: Maintains proper associations
Agreement-Specific Analytics
•	Employee-Centric Analysis: Track employee-related spending
•	Department Correlation: Employee-department relationships
•	Workflow Tracking: Agreement-specific approval processes
•	Compliance Monitoring: Agreement compliance tracking
Category Management System
Dynamic Category Hierarchy
•	Main Categories: Broad classification groups
•	Sub-Categories: Specific items within main categories
•	Automatic Updates: Changes reflect immediately in dashboard
•	Validation: Ensures data consistency and accuracy
Category-Based Insights
•	Spending Patterns: Identify category-specific trends
•	Budget Allocation: Data for category budget planning
•	Performance Metrics: Category-specific efficiency measures
•	Optimization Opportunities: Category-based improvement areas
Performance Optimization
Efficient Data Handling
•	Optimized Queries: Efficient database access
•	Memory Management: Proper handling of large datasets
•	Background Processing: Non-blocking user interface
•	Error Handling: Graceful degradation on errors
User Experience Features
•	Responsive Design: Adapts to different screen sizes
•	Intuitive Navigation: Easy-to-use interface
•	Quick Access: Frequently used features readily available
•	Customizable Views: Adjustable display options
Dark Mode Support
•	Toggle Feature: Switch between light and dark themes
•	Eye Comfort: Reduced eye strain in low-light environments
•	Consistent Experience: All components theme-aware
•	Preference Memory: Remembers user preference
Advanced Analytics
Efficiency Calculations
•	Approval Rate: Percentage of approved requests
•	Completion Rate: Percentage of completed workflows
•	Processing Efficiency: Combined performance metric
•	Trend Analysis: Performance changes over time
Bottleneck Identification
•	Stage Analysis: Identify slowest process stages
•	Recommendations: Data-driven improvement suggestions
•	Performance Metrics: Quantitative performance measurements
•	Actionable Insights: Specific improvement actions
Category Insights
•	Spending Patterns: Identify high-spending categories
•	Department Analysis: Department-specific trends
•	Budget Optimization: Data for budget planning
•	Resource Allocation: Informed resource distribution
________________________________________
Troubleshooting
Common Issues and Solutions
Data Display Issues
Problem	Solution
No data showing	Check filters, click "Reset Filters"
Categories not loading	Refresh data, check category configuration
Charts not updating	Ensure filters are applied correctly
Missing employee data	Verify employee information in source system
Export Problems
Problem	Solution
Export fails	Check disk space, file permissions
Excel file won't open	Ensure Excel is installed
PDF formatting issues	Update PDF reader
Large export timeout	Use more specific filters
Performance Issues
Problem	Solution
Slow loading	Reduce dataset size with filters
Filter lag	Use more specific search criteria
Interface freezing	Close other applications
Memory issues	Restart application
Error Messages
Error Message	Meaning	Action Required
"No data to export"	No records match current filters	Adjust filters or reset to show all records
"Export failed"	File system or permission issue	Check save location, ensure write permissions
"Database error"	Connection or query issue	Check network, contact administrator
"Memory error"	Too much data for system	Use filters to reduce dataset
Data Quality Issues
Handling Missing Data
•	Null categories: Displayed as empty or "Uncategorized"
•	Missing dates: Handled gracefully in date filters
•	Incomplete records: Clearly marked in displays
•	Data validation: Automatic filtering of corrupt data
Data Consistency
•	Category validation: Ensures valid category combinations
•	Date validation: Prevents invalid date ranges
•	Status validation: Maintains consistent status values
•	Amount validation: Ensures numeric values only
________________________________________
Best Practices
Efficient Dashboard Usage
1. Filter Strategy
•	Start Broad: Begin with few filters, narrow down gradually
•	Use Date Ranges: Focus on relevant time periods
•	Category First: Use category filters before other criteria
•	Employee Focus: Utilize employee filters for personnel analysis
2. Data Analysis Approach
•	Regular Reviews: Schedule weekly/monthly analytics sessions
•	Comparative Analysis: Compare different time periods
•	Department Focus: Analyze department-specific patterns
•	Employee Tracking: Monitor employee-related spending
3. Reporting Strategy
•	Standardized Exports: Use consistent naming conventions
•	Scheduled Reports: Set regular export schedules
•	Data Archiving: Maintain historical reports
•	Distribution Planning: Plan report distribution channels
Performance Optimization
Large Dataset Management
•	Use Specific Date Ranges: Limit data to relevant periods
•	Apply Category Filters: Reduce data by category first
•	Employee Filtering: Use employee filters for focused analysis
•	Export in Batches: Split large exports if needed
System Resource Management
•	Close Other Applications: Free up system resources
•	Adequate RAM: Ensure sufficient memory for large datasets
•	Regular Maintenance: Clear cache and temporary files
•	System Updates: Keep software updated
Data Security and Compliance
Access Control
•	Role-based Permissions: Ensure appropriate data access
•	Export Controls: Limit sensitive data exports
•	Audit Trails: Maintain access and export records
•	Data Classification: Identify sensitive information
Confidential Information Handling
•	Secure Storage: Protect exported files
•	Controlled Distribution: Limit report distribution
•	Proper Disposal: Secure deletion of sensitive data
•	Compliance Adherence: Follow hospital data policies
Backup and Recovery
Regular Backups
•	Export Key Reports: Regular export of important analyses
•	Data Archiving: Maintain historical data archives
•	Configuration Backup: Save filter and setting configurations
•	Documentation: Maintain system documentation
Disaster Recovery
•	Procedure Documentation: Document export and analysis procedures
•	Training: Train multiple staff members
•	System Documentation: Maintain current system documentation
•	Recovery Testing: Regular testing of recovery procedures
________________________________________
Quick Reference Guide
Keyboard Shortcuts
•	Ctrl + F: Focus on search field
•	Ctrl + R: Refresh data
•	Ctrl + E: Export dialog
•	Ctrl + P: Print options
•	Esc: Close dialogs/cancel operations
•	Tab: Navigate between filters
Common Workflows
Monthly Financial Reporting
1.	Set date range to month boundaries
2.	Apply department filters if needed
3.	Review status distribution chart
4.	Analyze category spending
5.	Export comprehensive PDF report
6.	Print summary for management meetings
Employee Spending Analysis
1.	Filter by specific employee or payee
2.	Analyze category distribution for employee
3.	Review approval and completion rates
4.	Export detailed Excel analysis
5.	Share findings with department head
Department Budget Analysis
1.	Filter by specific department
2.	Analyze category distribution
3.	Review employee spending patterns
4.	Export detailed Excel analysis
5.	Share findings with department head
Audit Preparation
1.	Export comprehensive Excel report
2.	Generate PDF with all records
3.	Print selection for physical files
4.	Archive digital copies
5.	Document findings and observations
Category Performance Review
1.	Filter by specific category
2.	Analyze spending trends over time
3.	Review approval efficiency
4.	Compare with other categories
5.	Generate recommendations report
Export Templates
Standard Report Package
1.	Executive Summary: PDF format for management
2.	Detailed Analysis: Excel format for deep analysis
3.	Category Breakdown: Word format for department reviews
4.	Print-ready Summary: For physical distribution
Custom Export Strategies
•	Department-specific: Filter by department before export
•	Employee-focused: Specific employee or payee analysis
•	Time-period focused: Specific date ranges for period analysis
•	Category-focused: Deep dive into specific categories
•	Status-based: Analysis of pending/approved/completed requests
________________________________________
Support and Resources
Technical Support
•	IT Help Desk: Extension 1234
•	Email Support: it-support@afranhospital.gov.et
•	Office Hours: 8:00 AM - 5:00 PM, Monday-Friday
•	Emergency Contact: After-hours support for critical issues
Training Resources
•	User Manuals: Complete documentation available
•	Video Tutorials: Step-by-step operation guides
•	Workshop Sessions: Regular training workshops
•	Quick Reference Cards: Laminated guides for common tasks
System Information
•	Version: Agreement-Based Purchase Fund Analytics Dashboard v3.0
•	Last Updated: December 2024
•	Compatibility: Windows 10/11, Java 8+, Microsoft Office for exports
•	Database: Integrated with hospital HRMS system
Feedback and Improvement
•	User Feedback Portal: Online suggestion system
•	Feature Requests: Submit via hospital ticketing system
•	Bug Reports: Immediate reporting for critical issues
•	User Group Meetings: Quarterly user feedback sessions



**3.Petty Cash Analytics Dashboard  End User Documentation**
Table of Contents
1.	System Overview
2.	Dashboard Interface
3.	Data Filtering and Search
4.	Analytics and Charts
5.	Export and Reporting
6.	Advanced Features
7.	Troubleshooting
8.	Best Practices
________________________________________
System Overview
Purpose
The Petty Cash Analytics Dashboard provides comprehensive reporting and analysis capabilities for petty cash management at Afran General Hospital. It enables users to visualize, analyze, and export petty cash data with advanced filtering and multiple chart types.
Key Features
•	Multi-tab Analytics: Table view, pie charts, bar charts, line charts, and advanced analytics
•	Advanced Filtering: Date range, categories, status, and custom search
•	Multiple Export Formats: Excel, PDF, Word with detailed formatting
•	Interactive Charts: Real-time data visualization
•	Role-based Access: Different permissions based on user roles
•	Print Capabilities: Professional report printing
User Roles and Permissions
Role	Permissions
Administrator	Full access to all features and data
Finance Administrator	Access to financial analytics and exports
Accountant	View analytics and generate reports
Cashier	Basic viewing and limited exports
Replenish Dispenser	Specialized access for cash replenishment
________________________________________
Dashboard Interface
Main Components
1. Header Section
•	Dashboard Title: "Petty Cash Analytics Dashboard"
•	Control Buttons: Export options, print, refresh, dark mode toggle
•	User Information: Current logged-in user display
2. Filter Section
•	Date Range: From/To date pickers
•	Text Filters: Unit, payee, general search
•	Category Filters: Main category and sub-category dropdowns
•	Status Filters: Approval, confirmation, dispensed status
•	Action Buttons: Reset filters, advanced filters
3. Main Content Area (Tabbed Interface)
•	Data Table Tab: Detailed record view with sorting and context menu
•	Status Distribution Tab: Pie chart showing request status
•	Monthly Analysis Tab: Bar chart of monthly trends
•	Trend Analysis Tab: Line chart for time-based analysis
•	Unit Analysis Tab: Bar chart by requisition unit
•	Category Analysis Tab: Combined pie and bar charts for categories
•	Advanced Analytics Tab: KPIs and detailed metrics
4. Summary Section
•	Real-time summary cards showing key metrics
•	Color-coded for quick reference
•	Updates automatically with filters
________________________________________
Data Filtering and Search
Basic Filtering
1. Date Range Filtering
•	From Date: Start date for filtering records
•	To Date: End date for filtering records
•	Usage: Select dates using date picker calendar
•	Behavior: Includes records from and including selected dates
2. Text-based Filters
•	Requisition Unit: Filter by department/unit name
•	Payee: Filter by employee receiving funds
•	Quick Search: Searches across all fields (ID, unit, category, payee, reason)
3. Category Filters
•	Main Category: Broad category classification
•	Sub-Category: Specific sub-categories within main categories
•	Auto-populated: Options generated from existing data
4. Status Filters
•	Approval Status: Pending, Approved, All
•	Confirmation Status: Pending, Confirmed, All
•	Dispensed Status: Yes, No, All
•	Dispense Approval: Pending, Approved, All
•	Void Status: Yes, No, All
Advanced Filtering Techniques
Combined Filtering
•	Multiple filters work together (AND logic)
•	Example: Show approved requests from Finance department in January
•	Real-time updates as filters change
Search Tips
•	Case-insensitive: Search works regardless of capitalization
•	Partial matches: "fin" will match "Finance" and "Financial"
•	Multiple fields: Quick search checks all text fields simultaneously
Resetting Filters
•	Click "Reset Filters" button to clear all filters
•	Returns to showing all records
•	Updates charts and summary immediately
________________________________________
Analytics and Charts
Data Table Tab
Features
•	Sortable Columns: Click column headers to sort
•	Context Menu: Right-click for additional options
•	Status Coloring: Color-coded status indicators
•	Real-time Count: Shows number of displayed records
Context Menu Options
•	Export Selected: Export chosen records to Excel
•	Copy to Clipboard: Copy record details to clipboard
•	View Details: Show detailed record information
•	Print Details: Print selected records
Chart Types
1. Status Distribution (Pie Chart)
•	Visualizes distribution of request statuses
•	Color-coded segments (Approved, Pending, etc.)
•	Shows count and percentage for each status
•	Interactive: Hover for details
2. Monthly Analysis (Bar Chart)
•	Shows monthly totals of petty cash amounts
•	X-axis: Months, Y-axis: Amount in ETB
•	Useful for identifying seasonal trends
3. Trend Analysis (Line Chart)
•	Daily trend of petty cash amounts
•	Shows spending patterns over time
•	Ideal for cash flow analysis
4. Unit Analysis (Bar Chart)
•	Breakdown by requisition units/departments
•	Identifies highest spending departments
•	Helps in budget allocation
5. Category Analysis
•	Pie Chart: Distribution across categories
•	Bar Chart: Amount totals by category
•	Combined view for comprehensive analysis
Advanced Analytics Tab
Key Performance Indicators (KPIs)
•	Approval Rate: Percentage of approved requests
•	Dispensed Rate: Percentage of dispensed requests
•	Average Processing Time: Typical request processing duration
•	Pending Actions: Number of requests needing attention
Category Insights
•	Detailed analysis of spending by category
•	Identifies top spending categories
•	Provides actionable insights for budget management
________________________________________
Export and Reporting
Export Options
1. Excel Export
Features:
•	Multiple sheets: Summary, Detailed Records, Individual Records
•	Professional formatting with headers and styling
•	Auto-sized columns for optimal readability
•	Formulas and calculations for summary data
Process:
1.	Click "Export Excel" button
2.	Choose save location and filename
3.	System generates comprehensive Excel report
4.	File automatically opens after export
File Structure:
•	Report Summary: Overview and key statistics
•	Detailed Records: All data in tabular format
•	Individual Records: Separate sheets for detailed viewing (10 records per sheet)
2. PDF Export
Features:
•	Professional document formatting
•	Cover page with hospital branding
•	Detailed summary section
•	Individual record pages
•	Signature sections for approvals
Content:
•	Cover page with report metadata
•	Executive summary with key metrics
•	Detailed record listings
•	Approval and signature sections
•	Timestamp and generation information
3. Word Export
Features:
•	Editable document format
•	Structured sections with headings
•	Table-based data presentation
•	Professional business report style
Printing Capabilities
Print Options
•	Print Report: Full dashboard printout
•	Print Selection: Selected records only
•	Print Details: Individual record detailed print
Print Features
•	Header: Hospital branding and report title
•	Professional Layout: Clean, readable format
•	Page Breaks: Optimized for multi-page documents
•	Footer: Confidentiality notice and timestamps
Individual Record Operations
Viewing Record Details
1.	Select record in table
2.	Right-click → "View Details" or use toolbar button
3.	Detailed popup shows all record information
4.	Options to print or export individual record
Copying to Clipboard
•	Copies formatted record information
•	Useful for quick sharing or pasting into other applications
•	Includes all key fields in readable format
________________________________________
Advanced Features
Dark Mode
•	Toggle between light and dark themes
•	Reduces eye strain in low-light environments
•	Preserves all functionality while changing appearance
Data Refresh
•	Manual Refresh: Click refresh button to update from database
•	Real-time Updates: Filters and charts update immediately
•	Data Integrity: Ensures latest information is displayed
Advanced Search Techniques
Using Date Ranges Effectively
•	Monthly Analysis: Set range to first and last day of month
•	Quarterly Reporting: Use three-month ranges
•	Year-over-Year: Compare same periods across years
Category Analysis
•	Identify spending patterns by department
•	Track category-specific trends
•	Support budget planning and allocation
Bulk Operations
Mass Export
•	Export all filtered records with one click
•	Multiple format options (Excel, PDF, Word)
•	Professional formatting maintained
Batch Printing
•	Print multiple records in single operation
•	Automatic pagination and formatting
•	Header/footer on each page
________________________________________
Troubleshooting
Common Issues and Solutions
Data Display Issues
Problem	Solution
No data showing	Check filters, click "Reset Filters"
Charts not updating	Ensure filters are applied, refresh data
Missing categories	Some records may not have category data
Export Problems
Problem	Solution
Export fails	Check disk space, file permissions
Excel file won't open	Ensure Excel is installed, file not corrupted
PDF formatting issues	Update PDF reader, try different export format
Performance Issues
Problem	Solution
Slow loading	Close other applications, check network
Filter lag	Reduce number of concurrent filters
Export timeout	Export smaller datasets, use advanced filters
Error Messages
Error Message	Meaning	Action
"No data to export"	No records match current filters	Adjust filters or reset to show all records
"Export failed"	File system or permission issue	Check save location, ensure write permissions
"Print error"	Printer configuration issue	Check printer connection, try different printer
Data Quality Issues
Handling Missing Data
•	Null categories: Displayed as "Uncategorized"
•	Missing dates: Excluded from date-based filters
•	Incomplete records: Clearly marked in exports
Data Validation
•	Automatic filtering of corrupt records
•	Clear indication of data issues
•	Maintenance of data integrity during operations
________________________________________
Best Practices
Efficient Dashboard Usage
1. Filter Management
•	Start broad: Begin with few filters, narrow down gradually
•	Use date ranges: Focus on relevant time periods
•	Save filter combinations: Note effective filter sets for recurring reports
2. Data Analysis
•	Regular reviews: Schedule weekly/monthly analytics sessions
•	Trend spotting: Use line charts to identify patterns
•	Comparative analysis: Compare different time periods or departments
3. Reporting Strategy
•	Standardize exports: Use consistent naming conventions
•	Schedule reports: Set regular export schedules
•	Archive data: Maintain historical reports for auditing
Performance Optimization
Large Datasets
•	Use specific date ranges to limit data
•	Apply category filters before exporting
•	Export in batches if dealing with very large datasets
System Resources
•	Close other applications during large exports
•	Ensure adequate RAM for processing large files
•	Regular system maintenance for optimal performance
Data Security
Access Control
•	Role-based permissions: Ensure users only see authorized data
•	Export controls: Limit sensitive data exports based on roles
•	Audit trails: Maintain records of data access and exports
Confidential Information
•	Secure storage of exported files
•	Proper disposal of printed materials
•	Adherence to hospital data protection policies
Backup and Recovery
Regular Backups
•	Export key reports regularly
•	Maintain historical data archives
•	Store backups in secure locations
Disaster Recovery
•	Document export procedures
•	Train multiple staff members
•	Maintain system documentation
________________________________________
Support and Resources
Technical Support
•	IT Help Desk: Extension 1234
•	Email Support: it-support@afranhospital.gov.et
•	Office Hours: 8:00 AM - 5:00 PM, Monday-Friday
Training Resources
•	User Manuals: Available in hospital documentation portal
•	Video Tutorials: Step-by-step guidance for common tasks
•	Workshop Sessions: Regular training sessions scheduled monthly
System Information
•	Version: Petty Cash Analytics Dashboard v2.0
•	Last Updated: December 2024
•	Compatibility: Windows 10/11, Java 8+
Feedback and Improvement
•	User Feedback: Provide suggestions through IT department
•	Feature Requests: Submit via hospital ticketing system
•	Bug Reports: Immediate reporting for critical issues
________________________________________
Quick Reference Guide
Keyboard Shortcuts
•	Ctrl + F: Focus on search field
•	Ctrl + R: Refresh data
•	Ctrl + E: Export dialog
•	Ctrl + P: Print options
•	Esc: Close dialogs/cancel operations
Common Workflows
Monthly Reporting
1.	Set date range to month boundaries
2.	Apply department filters if needed
3.	Review charts for trends
4.	Export to PDF for distribution
5.	Print summary for meetings
Department Analysis
1.	Filter by specific requisition unit
2.	Analyze category distribution
3.	Review approval rates
4.	Export detailed Excel report
5.	Share findings with department head
Audit Preparation
1.	Export comprehensive Excel report
2.	Generate PDF with all records
3.	Print selection for physical files
4.	Archive digital copies
5.	Document findings and observations



**4.Petty Cash Replenish Request Management System - End User Documentation**
Table of Contents
1.	System Overview
2.	Getting Started
3.	User Roles and Permissions
4.	Main Interface Overview
5.	Workflow Process
6.	Detailed Feature Guide
7.	Biometric Signature Integration
8.	Reporting and Export
9.	Troubleshooting
10.	Best Practices
________________________________________
System Overview
Purpose
The Petty Cash Replenish Request Management System is designed to streamline and secure the process of requesting, approving, confirming, and dispensing petty cash funds at Afran General Hospital. The system integrates biometric signature verification to ensure accountability and prevent fraud.
Key Features
•	Secure Workflow Management: Multi-step approval process with role-based access
•	Biometric Verification: Fingerprint-based identity verification for all critical actions
•	Digital Signature Storage: Secure storage and retrieval of employee signatures
•	Comprehensive Reporting: Multiple export formats (PDF, Excel, Word) with detailed analytics
•	Real-time Status Tracking: Live updates on request status throughout the workflow
•	Audit Trail: Complete history of all actions with timestamps and verifiers
________________________________________
Getting Started
System Requirements
•	Operating System: Windows 10/11
•	Java Runtime: Version 8 or higher
•	Fingerprint Device: ZKTECO biometric scanner
•	Database: Pre-configured SQL database
•	Permissions: Network access to database server
Initial Setup
1.	Hardware Connection
o	Connect ZKTECO fingerprint device via USB
o	Ensure device drivers are installed
o	Verify device is detected by the system
2.	User Authentication
o	Launch the application
o	Enter your username (automatically detects your role)
o	System automatically configures permissions based on your role
3.	Device Connection
o	Click "Connect Device" in the button panel
o	Wait for "Device Status: Connected ✓" confirmation
o	If connection fails, check USB connection and drivers
________________________________________
User Roles and Permissions
Administrator (Admin)
Full system access including:
•	Create, view, and void requests
•	Approve requests and dispenses
•	Confirm requests
•	Dispense cash
•	Manage all export and reporting functions
•	Device management
Cashier
Request Management:
•	Create new petty cash requests
•	Void own requests (if not dispensed)
•	View request details
•	Dispense cash (with dual verification)
•	Export and search functions
Replenish Dispenser
Dispensing Functions:
•	View request details
•	Dispense cash (with dual verification)
•	Basic export and search capabilities
Accountant
Confirmation Role:
•	View request details
•	Confirm approved requests using biometric verification
•	Export and reporting access
Finance Administrator
Approval Authority:
•	Void requests
•	View details
•	Approve requests and dispenses
•	Full export and reporting capabilities
________________________________________
Main Interface Overview
Header Section
•	Hospital Information: Afran General Hospital branding
•	Department: Petty Cash Replenish Request Management
•	Current Date: Automatic date display
Summary Panel
•	Real-time statistics showing:
o	Total requests
o	Pending approvals
o	Approved requests
o	Confirmed requests
o	Dispensed requests
o	Total amount across all requests
Button Panel
Role-based buttons appear based on your permissions:
Workflow Buttons (Follow Sequential Order):
1.	New Replenish - Create new request
2.	Approve Request - First approval step
3.	Dispense Cash - After approval
4.	Confirm Request - After dispensing
5.	Approve Dispense - Final approval
Management Buttons:
•	Void Request - Cancel a request
•	View Details - Detailed record view
•	Connect/Disconnect Device - Biometric device management
Export Buttons:
•	Export To Excel/Word/PDF - Various format exports
•	Print Report - Printing options
•	Advanced Search - Filter and search records
•	Refresh Data - Update data from database
Table Section
Interactive table showing all requests with columns:
•	Request ID, Requisition Unit, Reason, Payee
•	Amount Requested, Amount Available, Current Status
•	Dates, Approval Statuses, Verification Personnel
•	Color-coded status indicators
Footer Section
•	Device Status: Connection status of biometric device
•	Operation Status: Current system status messages
•	System Information: Version and copyright details
________________________________________
Workflow Process
Step 1: Create New Request (Cashier/Admin)
Accessing the Request Form
1.	Click "New Replenish" button
2.	Form opens with required fields marked with *
Filling the Request Form
Required Fields:
•	Requisition Unit: Select department/unit (e.g., "HR Department")
•	Reason: Detailed explanation for the request
•	Payee: Select from dropdown list of authorized employees
•	Amount: Enter amount in ETB (numeric only)
Automatic Fields:
•	Request ID: Automatically generated (format: PCREPLN20231215001)
•	Request Date: Current date (auto-filled)
Submission Process
1.	Complete all required fields
2.	Click "OK" to submit
3.	System validates information
4.	Success confirmation with Request ID
5.	Request appears in table with "Pending" status
Step 2: Approve Request (Finance Admin/Admin)
Approval Prerequisites
•	Request must be in "Pending" status
•	Not voided or already dispensed
•	Biometric device must be connected
Approval Process
1.	Select the request from the table
2.	Click "Approve Request"
3.	Biometric verification dialog opens
4.	Step 1: Verify approver identity via fingerprint
5.	Step 2: System loads approver's digital signature
6.	Optional: Add approval notes
7.	Click "APPROVE REQUEST" to complete
Post-Approval
•	Status changes to "Approved"
•	Approver's name and signature recorded
•	Request moves to next workflow step
Step 3: Dispense Cash (Cashier/Replenish Dispenser/Admin)
Dispensing Prerequisites
•	Request must be "Approved"
•	Not voided
•	Biometric device connected
•	No active petty cash fund in use
Dual Verification Process
1.	Select approved request
2.	Click "Dispense Cash"
3.	Dispense form opens with pre-filled request details
Required Information:
•	Given Amount (defaults to requested amount)
•	Completed Date (defaults to current date)
•	Given By (auto-filled after verification)
Biometric Verification Steps:
Cashier Verification:
1.	Click "Verify Identity & Load Signature" in Cashier section
2.	Scan fingerprint
3.	System verifies identity and loads signature
4.	Payee field auto-populates
Dispenser Verification:
1.	Click "Verify Identity & Load Signature" in Dispenser section
2.	Scan fingerprint
3.	System verifies identity and loads signature
4.	Given By field auto-populates
Completion
1.	Verify all information is correct
2.	Click "OK" to complete dispense
3.	Status updates to "Yes" for dispensed
4.	Both signatures stored in database
Step 4: Confirm Request (Accountant/Admin)
Confirmation Prerequisites
•	Request must be "Approved" and "Dispensed"
•	Not voided
•	Biometric device connected
Confirmation Process
1.	Select dispensed request
2.	Click "Confirm Request"
3.	Biometric verification dialog opens
4.	Verify confirmer identity via fingerprint
5.	System loads confirmer's digital signature
6.	Add confirmation notes if needed
7.	Click "CONFIRM REQUEST"
Post-Confirmation
•	Confirmation status changes to "Confirmed"
•	Confirmer's name and signature recorded
Step 5: Approve Dispense (Finance Admin/Admin)
Final Approval Prerequisites
•	Request must be "Approved", "Dispensed", and "Confirmed"
•	Not voided
•	Biometric device connected
•	No active petty cash fund in use
Final Approval Process
1.	Select confirmed request
2.	Click "Approve Dispense"
3.	Biometric verification dialog opens
4.	Verify approver identity via fingerprint
5.	System loads approver's digital signature
6.	Add approval notes if needed
7.	Click "APPROVE DISPENSE"
Completion
•	Dispense approval status changes to "Approved"
•	Request workflow complete
•	All buttons disabled for this request
________________________________________
Detailed Feature Guide
Viewing Request Details
Quick View
•	Select any request in the table
•	Basic information displayed in table columns
•	Color-coded status indicators
Detailed View
1.	Select request
2.	Click "View Details" or double-click the row
3.	Comprehensive details window opens with:
o	Basic information section
o	Status information section
o	Biometric signatures section (all verifiers)
o	Export to PDF capability
Voiding Requests
When to Void
•	Request created in error
•	Before dispensing occurs
•	Authorization withdrawn
Void Process
1.	Select request (must not be dispensed)
2.	Click "Void Request"
3.	Enter mandatory void reason
4.	Click "OK" to confirm
5.	Status changes to "Yes" for voided
6.	Void reason recorded in database
Restrictions
•	Cannot void dispensed requests
•	Cannot void already voided requests
•	Void reason is mandatory
Search and Filtering
Quick Search
•	Use search field above table
•	Searches: Request ID, Payee, Requisition Unit, Reason
•	Real-time filtering as you type
Status Filter
•	Dropdown to filter by approval status
•	Options: All, Pending, Approved, Rejected, Confirmed, Dispensed, Voided
Date Range Filter
•	From Date: Start date for filtering
•	To Date: End date for filtering
•	Combines with other filters
Advanced Search
1.	Click "Advanced Search"
2.	Multiple criteria available:
o	Request ID
o	Payee name
o	Requisition Unit
o	Approval Status
o	Confirmation Status
o	Dispensed Status
o	Date range
3.	Click "SEARCH" to apply filters
4.	Use "Clear" to reset filters
Data Management
Refreshing Data
•	Click "Refresh Data" button
•	Fetches latest data from database
•	Updates summary statistics
•	Useful after other users make changes
Table Customization
•	Column Sorting: Click column headers to sort
•	Column Resizing: Drag column borders
•	Row Colors: Automatic color coding by status
•	Selection: Click to select, double-click for details
________________________________________
Biometric Signature Integration
Device Management
Connecting the Device
1.	Ensure ZKTECO device is connected via USB
2.	Click "Connect Device"
3.	Wait for "Device Status: Connected ✓"
4.	Operation status shows "Device connected successfully"
Troubleshooting Connection
•	Check USB connection
•	Verify drivers are installed
•	Ensure device not used by another application
•	Restart application if needed
Disconnecting
•	Click "Disconnect Device"
•	Wait for confirmation message
•	Device status shows "Disconnected"
Fingerprint Verification Process
During Workflow Steps
1.	System prompts for fingerprint verification
2.	Place finger on scanner when instructed
3.	System captures fingerprint template
4.	Matches against database of authorized users
5.	Loads corresponding digital signature
6.	Displays verification success message
Verification Failure
•	No match found: Verify user is in database
•	Poor quality scan: Clean finger and scanner, try again
•	Device error: Check connection and restart if needed
Signature Storage and Retrieval
Digital Signatures
•	Stored securely in database
•	Associated with employee records
•	Automatically retrieved during verification
•	Displayed in details view and exports
Signature Security
•	Encrypted storage
•	Access-controlled retrieval
•	Audit trail of all signature uses
•	Cannot be modified by users
________________________________________
Reporting and Export
Export to PDF
Single Record Export
1.	Open "View Details" for a request
2.	Click "Export To PDF" button (if all signatures available)
3.	System generates PDF with:
o	Hospital header and logo
o	Basic request information
o	Status details
o	All biometric signatures
o	Generation timestamp
4.	PDF saved to Documents/PettyCashFunds/ folder
5.	Automatically opens after creation
PDF Requirements
•	All required signatures must be available
•	System validates before allowing export
•	Missing signatures trigger warning message
Export to Excel
Options Available:
1.	Tabular Export (Date Range)
o	Multiple records in spreadsheet format
o	Includes all data columns
o	Summary statistics
2.	Selected Row Details
o	Single record with detailed information
o	Formatted layout
o	Basic signature information
3.	Date Range Details with Images
o	Multiple records with comprehensive details
o	Includes status information
Excel Export Process
1.	Choose export option from dialog
2.	Select date range if required
3.	Choose save location and filename
4.	System generates Excel file (.xlsx)
5.	Success confirmation with record count
Export to Word
Document Types:
1.	Tabular Export: Table format with multiple records
2.	Selected Row Details: Formatted document for single record
3.	Date Range Details: Comprehensive report for multiple records
Word Features:
•	Professional formatting
•	Hospital branding
•	Table of data
•	Summary information
•	Timestamp and generation details
Printing Options
Available Print Types:
1.	Tabular Print (Date Range)
o	Multiple records in table format
o	Summary statistics
o	Professional layout
2.	Selected Row Details with Images
o	Single record comprehensive printout
o	Includes all available information
o	Signature representations
3.	Date Range Details with Images
o	Multiple detailed records
o	Formatted sections for each record
o	Status summaries
Print Process
1.	Select print option
2.	Choose date range if needed
3.	Print dialog opens
4.	Select printer and settings
5.	Confirm print job
Report Customization
Date Range Selection
•	From Date: Start of reporting period
•	To Date: End of reporting period
•	Validation: Ensures logical date range
•	Empty results: Notification if no records found
Content Filtering
•	Combine with search filters
•	Status-based filtering
•	Department/unit filtering
•	Amount range filtering (if needed)
________________________________________
Troubleshooting
Common Issues and Solutions
Biometric Device Problems
Issue: "Device Not Connected"
•	✅ Check USB cable connection
•	✅ Verify device power indicator
•	✅ Reinstall device drivers
•	✅ Restart application
Issue: "Fingerprint Not Recognized"
•	✅ Clean fingerprint scanner surface
•	✅ Ensure finger is clean and dry
•	✅ Try different finger
•	✅ Contact admin to verify enrollment in system
Issue: "No Matching Employee Found"
•	✅ Verify employee is registered in system
•	✅ Check fingerprint template is stored
•	✅ Contact system administrator
Application Errors
Issue: Buttons Disabled
•	✅ Verify row is selected in table
•	✅ Check user role permissions
•	✅ Confirm workflow sequence is correct
•	✅ Ensure request isn't voided or completed
Issue: "Cannot Save Request"
•	✅ Check database connection
•	✅ Verify all required fields are completed
•	✅ Ensure network connectivity
•	✅ Contact IT support if persistent
Issue: Slow Performance
•	✅ Check network connection
•	✅ Verify database server status
•	✅ Clear search filters if large dataset
•	✅ Restart application
Workflow Blockers
Issue: "Cannot Dispense - Active Fund in Use"
•	✅ Wait for current active fund to be utilized
•	✅ Contact finance department about fund status
•	✅ System prevents overlapping dispensations
Issue: "Request Already Processed"
•	✅ Check current status in details view
•	✅ Verify you're attempting correct workflow step
•	✅ Contact previous processor if needed
Error Messages Reference
Error Message	Cause	Solution
"Device Status: Not Connected"	Biometric device offline	Connect device and click "Connect Device"
"No Selection"	No row selected in table	Click on a request in the table
"Cannot Void Dispensed Request"	Attempting to void completed request	Void only possible before dispensing
"Missing Signatures"	Required signatures not available	Ensure all verifiers are registered in system
"Database Error"	Connection or query failure	Check network, contact IT support
System Maintenance
Regular Checks
•	✅ Verify biometric device connectivity
•	✅ Confirm database connection
•	✅ Check user role assignments
•	✅ Validate signature database integrity
Data Backup
•	Automated daily backups
•	Manual export capability for records
•	Archive completed requests periodically
________________________________________
Best Practices
Request Creation
•	Be Specific: Provide detailed reasons for requests
•	Accurate Amounts: Request exact needed amounts
•	Correct Payee: Verify payee selection before submission
•	Timely Submission: Submit requests well before funds needed
Security Practices
•	Never Share Credentials: Your username is personal
•	Secure Device Access: Only authorized personnel should use biometric device
•	Log Out Properly: Close application when not in use
•	Report Suspicious Activity: Immediately report any unusual system behavior
Workflow Efficiency
•	Follow Sequence: Complete workflow steps in order
•	Batch Processing: Group similar requests when possible
•	Regular Monitoring: Check request status frequently
•	Clear Communication: Use notes fields for important information
Data Management
•	Regular Exports: Export important records for personal records
•	Use Search Features: Utilize filters to find records quickly
•	Verify Information: Double-check details before finalizing actions
•	Archive Completed: Move completed requests from active view when needed
System Usage
•	Regular Updates: Ensure application is up to date
•	Device Care: Handle biometric device carefully
•	Network Awareness: Be mindful of network issues during critical operations
•	Backup Habits: Keep personal copies of important transactions
________________________________________
Support and Contact
Internal Support
•	IT Help Desk: Extension 4357
•	Finance Department: For workflow and approval questions
•	System Administrator: For user access and role changes
Documentation
•	This user guide available in digital format
•	Quick reference guides posted in departments
•	Video tutorials available on hospital intranet
Training
•	New user orientation sessions monthly
•	Role-specific training available
•	Refresher courses quarterly
Feedback
•	System improvement suggestions welcome
•	Report issues through IT help desk
•	User experience surveys conducted annually


**5.Petty Cash Request Management System End User Documentation**
Table of Contents
1.	System Overview
2.	User Roles and Permissions
3.	Getting Started
4.	Main Interface Overview
5.	Petty Cash Request Workflow
6.	Detailed Feature Guide
7.	Biometric Signature Integration
8.	Reporting and Export
9.	Troubleshooting
10.	Best Practices
________________________________________
System Overview
Purpose
The Petty Cash Request Management System is designed to streamline and secure the process of requesting, approving, confirming, and dispensing petty cash funds at Afran General Hospital. The system incorporates biometric signature verification to ensure accountability and prevent fraud.
Key Features
•	Biometric Authentication: Fingerprint verification for all approvals
•	Dual Signature Verification: For cash dispensing operations
•	Automated Workflow: Structured approval process
•	Real-time Tracking: Monitor request status in real-time
•	Comprehensive Reporting: Export to Excel, Word, and PDF
•	Security: Role-based access control
________________________________________
User Roles and Permissions
Administrator
•	Full system access
•	Can create, edit, void, and delete requests
•	Can approve requests and dispenses
•	Device management capabilities
Finance Administrator
•	Create new petty cash requests
•	Void requests
•	View request details
•	Approve requests and dispense approvals
Accountant
•	View request details
•	Confirm requests after approval
•	Export and print reports
Cashier
•	Void requests
•	View request details
•	Dispense cash (with dual biometric verification)
•	Export and print reports
________________________________________
Getting Started
System Requirements
•	Windows 10/11
•	Java Runtime Environment 8+
•	ZKTECO fingerprint scanner device
•	Internet connection for database access
Initial Setup
1.	Connect Fingerprint Device
o	Click "Connect Device" button
o	Ensure USB connection is secure
o	Wait for "Device Status: Connected ✓" confirmation
2.	User Authentication
o	System automatically detects your role based on login credentials
o	Buttons and features adjust according to your permissions
3.	Database Connection
o	Automatic connection to hospital database
o	No manual configuration required
________________________________________
Main Interface Overview
Header Section
•	Hospital name and department
•	Current date display
•	System title
Summary Panel
•	Real-time statistics of petty cash requests
•	Shows totals, pending, approved, confirmed, and dispensed counts
•	Total amount summary
Button Panel
Workflow Buttons (Role-based):
•	New Request
•	Edit Request
•	Void Request
•	Delete Request
•	View Details
•	Approve Request
•	Confirm Request
•	Dispense Cash
•	Approve Dispense
Utility Buttons:
•	Connect/Disconnect Device
•	Export to Excel/Word/PDF
•	Print Reports
•	Advanced Search
•	Refresh Data
Data Table
•	Displays all petty cash requests
•	Color-coded status indicators
•	Double-click any row to view full details
•	Sortable and filterable columns
________________________________________
Petty Cash Request Workflow
Step 1: Create New Request
1.	Click "New Request" button
2.	Fill in required fields:
o	Requisition Unit: Your department name
o	Main Category: Select from dropdown
o	Sub Category: Automatically populated based on main category
o	Reason: Detailed explanation of need
o	Payee: Select employee from list
o	Amount: Enter amount in ETB
3.	Validation Rules:
o	Amount cannot exceed maximum petty cash limit
o	Amount cannot exceed available petty cash balance
o	All required fields must be completed
4.	Automatic Request ID Generation:
o	Format: PCYYYYMM#### (e.g., PC2023120001)
o	Resets monthly
Step 2: Request Approval
1.	Finance Admin selects request and clicks "Approve Request"
2.	Biometric Verification Required:
o	Approver must verify identity via fingerprint
o	System loads approver's digital signature
o	Optional approval notes can be added
3.	Request status changes to "Approved"
Step 3: Cash Dispensing
1.	Cashier selects approved request and clicks "Dispense Cash"
2.	Dual Biometric Verification Required:
o	Step 1: Verify Payee identity
o	Step 2: Verify Dispenser identity
3.	Enter dispensing details:
o	Given amount (defaults to requested amount)
o	Completion date
o	Given by (auto-filled from biometric verification)
4.	Both signatures are digitally recorded
Step 4: Confirmation
1.	Accountant selects dispensed request and clicks "Confirm Request"
2.	Biometric Verification Required:
o	Confirmer must verify identity via fingerprint
o	System loads confirmer's digital signature
3.	Request status changes to "Confirmed"
Step 5: Dispense Approval
1.	Finance Admin selects confirmed request and clicks "Approve Dispense"
2.	Biometric Verification Required:
o	Approver must verify identity via fingerprint
o	System loads approver's digital signature
3.	Request status changes to "Approved" for dispense
________________________________________
Detailed Feature Guide
Creating Petty Cash Requests
Form Fields Explained:
•	Requisition Unit: The department or unit requesting funds
•	Main Category: Broad classification (e.g., Office Supplies, Medical Supplies)
•	Sub Category: Specific item type (e.g., Stationery, Cleaning Materials)
•	Reason: Detailed justification for the expense
•	Payee: Employee who will receive the cash
•	Amount: Requested amount in Ethiopian Birr
Validation Rules:
•	Maximum request limit: System-defined (typically 5,000 ETB)
•	Available balance check: Prevents over-committing petty cash
•	Required field validation: All fields marked with * are mandatory
Editing Requests
•	Only available for non-voided, non-dispensed requests
•	Select request and click "Edit Request"
•	Modify necessary fields
•	Save changes
Voiding Requests
•	Available for non-dispensed requests
•	Mandatory void reason required
•	Permanent record maintained for audit trail
•	Cannot be undone
Deleting Requests
•	Only available for non-dispensed requests
•	Permanent deletion from system
•	Use with caution - prefer voiding instead
Viewing Request Details
•	Double-click any row or click "View Details"
•	Complete request information
•	Status history
•	Biometric signatures
•	Export to PDF capability
________________________________________
Biometric Signature Integration
Device Connection
1.	Connect Device: Establishes connection with fingerprint scanner
2.	Status Indicators:
o	Green: Connected and ready
o	Red: Disconnected or error
o	Yellow: Processing
Fingerprint Verification Process
1.	Click verification button in any approval dialog
2.	Place finger on scanner when prompted
3.	System captures fingerprint template
4.	Matches against employee database
5.	Loads corresponding digital signature
6.	Verification status displayed
Signature Storage
•	All signatures stored securely in database
•	Linked to employee records
•	Cannot be modified by users
•	Audit trail maintained
________________________________________
Reporting and Export
Export to Excel
Options Available:
1.	Tabular Export: Basic data in spreadsheet format
2.	Selected Row Export: Detailed information for specific request
3.	Date Range Export: Filter by specific time period
Export to Word
Options Available:
1.	Tabular Report: Formatted table in Word document
2.	Detailed Report: Comprehensive request details
3.	Date Range Report: Custom time period selection
Export to PDF
•	Available from request details view
•	Includes all signatures and status information
•	Professional formatting for official records
Printing Options
1.	Tabular Print: Quick data table printout
2.	Selected Row Print: Detailed single request print
3.	Date Range Print: Custom period with images
Advanced Search
Search Criteria:
•	Request ID
•	Payee name
•	Requisition unit
•	Approval status
•	Confirmation status
•	Dispensed status
•	Date range
________________________________________
Available Balance Management & Replenishment
Current Available Amount Calculation
The system automatically calculates available petty cash based on:
•	Current Status = "In Use" funds
•	Dispense Approval Status = "Approved" transactions
Automatic Replenishment Trigger
When available amount reaches 0 ETB:
1.	System automatically changes replenish status to "Completed"
2.	Finance department is notified for fund replenishment
3.	New petty cash cycle begins
Balance Validation During Requests
When creating new requests:
•	System checks: Requested Amount ≤ Available Balance
•	If insufficient funds: "Requested amount exceeds available petty cash balance" error
•	Prevents over-committing funds
Real-time Balance Updates
•	Available amount updates immediately after dispense approval
•	No manual intervention required
•	Transparent fund tracking
________________________________________
Troubleshooting
Common Issues and Solutions
Fingerprint Device Issues
Problem: "Device Not Connected" error
Solutions:
1.	Check USB connection
2.	Click "Connect Device" button
3.	Restart application if persistent
4.	Verify device drivers are installed
Problem: Fingerprint not recognized
Solutions:
1.	Ensure finger is clean and dry
2.	Reposition finger on scanner
3.	Contact admin to verify fingerprint enrollment
Request Creation Issues
Problem: "Amount exceeds maximum limit" error
Solution: Reduce requested amount to within allowed limits
Problem: "Amount exceeds available balance" error
Solution:
•	Check current available petty cash balance
•	Reduce amount or wait for fund replenishment
•	Contact finance department for balance inquiry
System Performance
Problem: Slow loading or unresponsive
Solutions:
1.	Click "Refresh Data" button
2.	Check network connection
3.	Close and reopen application
Error Messages Reference
Error Message	Cause	Solution
"Device Status: Connection Failed"	Fingerprint device not detected	Check USB connection and drivers
"No matching employee found"	Fingerprint not in database	Contact system administrator
"Cannot edit dispensed request"	Request already processed	Create new request instead
"Void reason is required"	Mandatory field missing	Enter reason for voiding
________________________________________
Best Practices
For Requesters
•	Plan petty cash needs in advance
•	Provide detailed and accurate reasons
•	Verify amounts before submission
•	Keep within departmental budgets
For Approvers
•	Verify request legitimacy before approval
•	Use biometric verification for all approvals
•	Add meaningful approval notes when necessary
•	Monitor available balances regularly
For Cashiers
•	Always perform dual verification during dispensing
•	Verify payee identity matches request
•	Maintain accurate dispensing records
•	Report any discrepancies immediately
For All Users
•	Keep login credentials secure
•	Log out when not using the system
•	Report system issues promptly
•	Regularly update stored fingerprints if physical changes occur
System Maintenance
•	Regular database backups
•	Keep fingerprint device clean
•	Update software as new versions become available
•	Regular audit of system logs
________________________________________
Support and Contact Information
Technical Support
•	IT Department: Extension 1234
•	Email: it-support@afranhospital.gov.et
•	Office Hours: 8:00 AM - 5:00 PM, Monday-Friday
Finance Department
•	Petty Cash Administrator: Extension 5678
•	Email: finance@afranhospital.gov.et
Emergency Support
For critical system issues outside office hours, contact:
•	On-call IT Support: +251-911-234-567
________________________________________
Version Information
Current Version: Petty Cash Management System v3.0
Last Updated: December 2025
Compatibility: Smart HRMS Integration Ready




**6.Purchase Fund Replenish Request Management System  End User Documentation**
Table of Contents
1.	System Overview
2.	Getting Started
3.	User Roles and Permissions
4.	Main Interface Overview
5.	Workflow Process
6.	Detailed Feature Guide
7.	Biometric Signature Integration
8.	Reporting and Export
9.	Troubleshooting
10.	Best Practices
________________________________________
System Overview
Purpose
The Purchase Fund Replenish Request Management System is designed to manage and secure the process of requesting, approving, confirming, and dispensing purchase funds at Afran General Hospital. This system specifically handles larger purchase amounts for departmental needs and integrates biometric signature verification for enhanced security and accountability.
Key Features
•	Secure Multi-Step Workflow: Comprehensive approval process with role-based access control
•	Biometric Identity Verification: Fingerprint-based authentication for all critical actions
•	Digital Signature Management: Secure storage and retrieval of employee signatures
•	Advanced Reporting: Multiple export formats (PDF, Excel, Word) with detailed analytics
•	Real-time Status Monitoring: Live tracking of request status throughout the workflow
•	Complete Audit Trail: Full history of all actions with timestamps and verifier information
________________________________________
Getting Started
System Requirements
•	Operating System: Windows 10/11
•	Java Runtime: Version 8 or higher
•	Biometric Device: ZKTECO fingerprint scanner
•	Database: Pre-configured SQL database connection
•	Network: Access to hospital database server
Initial Setup
1.	Hardware Connection
o	Connect ZKTECO fingerprint device via USB port
o	Ensure device drivers are properly installed
o	Verify device recognition in system settings
2.	User Authentication
o	Launch the application
o	System automatically detects and authenticates your username
o	Permissions are automatically configured based on your role
3.	Device Connection
o	Click "Connect Device" in the button panel
o	Wait for confirmation: "Device Status: Connected ✓"
o	If connection fails, check USB connection and device drivers
________________________________________
User Roles and Permissions
Administrator (Admin)
Full system access including:
•	Create, view, and void purchase fund requests
•	Approve requests and dispenses
•	Confirm requests
•	Dispense funds
•	Manage all export and reporting functions
•	Device management and system configuration
Cashier
Request Management Functions:
•	Void own requests (if not dispensed)
•	View request details
•	Dispense funds (with dual verification)
•	Export and search capabilities
•	Device connectivity management
Replenish Dispenser
Dispensing Functions:
•	View request details
•	Dispense funds (with dual verification)
•	Basic export and search functions
•	Device management
Accountant
Confirmation Role:
•	View request details
•	Confirm approved requests using biometric verification
•	Export and reporting access
•	Device connectivity
Finance Administrator
Approval Authority:
•	Create new purchase fund requests
•	Void requests
•	View detailed information
•	Approve requests and dispenses
•	Full export and reporting capabilities
•	Device management
________________________________________
Main Interface Overview
Header Section
•	Hospital Information: Afran General Hospital branding with Amharic and English names
•	Department: Purchase Fund Replenish Request Management
•	Current Date: Automatic date display with full formatting
Summary Panel
•	Real-time statistics displaying:
o	Total purchase fund requests
o	Pending approval requests
o	Approved requests
o	Confirmed requests
o	Dispensed requests
o	Total amount across all requests
Button Panel
Role-based buttons dynamically appear based on your permissions:
Workflow Buttons (Sequential Order):
1.	New Replenish - Create new purchase fund request
2.	Approve Request - First approval step
3.	Dispense Cash - After approval
4.	Confirm Request - After dispensing
5.	Approve Dispense - Final approval
Management Buttons:
•	Void Request - Cancel a purchase fund request
•	View Details - Detailed record examination
•	Connect/Disconnect Device - Biometric device management
Export and Utility Buttons:
•	Export To Excel/Word/PDF - Multiple format exports
•	Print Report - Various printing options
•	Advanced Search - Comprehensive filtering and search
•	Refresh Data - Update from database
Table Section
Interactive table displaying all purchase fund requests with columns:
•	Request ID, Requisition Unit, Reason, Payee
•	Amount Requested, Amount Available, Current Status
•	Dates, Approval Statuses, Verification Personnel
•	Color-coded status indicators for quick visual reference
Footer Section
•	Device Status: Real-time connection status of biometric device
•	Operation Status: Current system status and messages
•	System Information: Version details and copyright information
________________________________________
Workflow Process
Step 1: Create New Request (Finance Admin/Admin)
Accessing the Request Form
1.	Click "New Replenish" button
2.	Form opens with required fields clearly marked with *
Filling the Purchase Fund Request Form
Required Fields:
•	Requisition Unit: Select department/unit requiring funds (e.g., "Medical Supplies Department")
•	Reason: Detailed explanation for the purchase fund request
•	Payee: Select from dropdown list of authorized employees
•	Amount: Enter amount in ETB (numeric only, supports decimals)
Automatic Fields:
•	Request ID: Automatically generated (format: PFREPLN20231215001)
•	Request Date: Current date (auto-filled, non-editable)
Submission Process
1.	Complete all required fields
2.	Click "OK" to submit
3.	System validates all information
4.	Success confirmation with generated Request ID
5.	Request appears in table with "Pending" status
Step 2: Approve Request (Finance Admin/Admin)
Approval Prerequisites
•	Request must be in "Pending" status
•	Not voided or already dispensed
•	Biometric device must be connected and ready
Approval Process
1.	Select the request from the table
2.	Click "Approve Request"
3.	Biometric verification dialog opens
4.	Step 1: Verify approver identity via fingerprint scan
5.	Step 2: System loads approver's digital signature
6.	Optional: Add approval notes or comments
7.	Click "APPROVE REQUEST" to complete the process
Post-Approval
•	Status changes to "Approved"
•	Approver's name and signature recorded in database
•	Request becomes eligible for next workflow step
Step 3: Dispense Funds (Cashier/Replenish Dispenser/Admin)
Dispensing Prerequisites
•	Request must be "Approved"
•	Not voided
•	Biometric device connected
•	No active purchase fund in use
Dual Verification Process
1.	Select approved request from table
2.	Click "Dispense Cash"
3.	Dispense form opens with pre-filled request details
Required Information:
•	Given Amount (defaults to requested amount, can be adjusted)
•	Completed Date (defaults to current date)
•	Given By (auto-filled after verification)
Biometric Verification Steps:
Payee Verification:
1.	Click "Verify Identity & Load Signature" in Payee section
2.	Scan fingerprint of the payee
3.	System verifies identity and loads digital signature
4.	Payee field auto-populates with verified name
Dispenser Verification:
1.	Click "Verify Identity & Load Signature" in Dispenser section
2.	Scan fingerprint of the dispenser
3.	System verifies identity and loads digital signature
4.	Given By field auto-populates with dispenser's name
Completion
1.	Verify all information is accurate
2.	Click "OK" to complete the dispense process
3.	Status updates to "Yes" for dispensed
4.	Both signatures stored securely in database
Step 4: Confirm Request (Accountant/Admin)
Confirmation Prerequisites
•	Request must be "Approved" and "Dispensed"
•	Not voided
•	Biometric device connected and functional
Confirmation Process
1.	Select dispensed request from table
2.	Click "Confirm Request"
3.	Biometric verification dialog opens
4.	Verify confirmer identity via fingerprint
5.	System loads confirmer's digital signature
6.	Add confirmation notes if necessary
7.	Click "CONFIRM REQUEST"
Post-Confirmation
•	Confirmation status changes to "Confirmed"
•	Confirmer's name and signature recorded in system
•	Request progresses to final approval step
Step 5: Approve Dispense (Finance Admin/Admin)
Final Approval Prerequisites
•	Request must be "Approved", "Dispensed", and "Confirmed"
•	Not voided
•	Biometric device connected
•	No active purchase fund currently in use
Final Approval Process
1.	Select confirmed request from table
2.	Click "Approve Dispense"
3.	Biometric verification dialog opens
4.	Verify approver identity via fingerprint
5.	System loads approver's digital signature
6.	Add final approval notes if needed
7.	Click "APPROVE DISPENSE"
Completion
•	Dispense approval status changes to "Approved"
•	Purchase fund request workflow is complete
•	All workflow buttons disabled for this request
________________________________________
Detailed Feature Guide
Viewing Request Details
Quick View
•	Select any request in the main table
•	Basic information displayed in table columns
•	Color-coded status indicators for quick assessment
Detailed View
1.	Select request from table
2.	Click "View Details" or double-click the row
3.	Comprehensive details window opens with:
o	Basic information section
o	Status information section
o	Biometric signatures section (all verifiers)
o	Export to PDF capability
Voiding Purchase Fund Requests
When to Void
•	Request created in error
•	Before dispensing occurs
•	Authorization withdrawn
•	Budget constraints
Void Process
1.	Select request (must not be dispensed)
2.	Click "Void Request"
3.	Enter mandatory void reason in text area
4.	Click "OK" to confirm void action
5.	Status changes to "Yes" for voided
6.	Void reason recorded in database for audit purposes
Restrictions
•	Cannot void dispensed requests
•	Cannot void already voided requests
•	Void reason is mandatory for accountability
Search and Filtering
Quick Search
•	Use search field above the main table
•	Searches across: Request ID, Payee, Requisition Unit, Reason
•	Real-time filtering as you type
Status Filter
•	Dropdown to filter by approval status
•	Options: All, Pending, Approved, Rejected, Confirmed, Dispensed, Voided
Date Range Filter
•	From Date: Start date for filtering period
•	To Date: End date for filtering period
•	Combines with other filters for precise results
Advanced Search
1.	Click "Advanced Search" button
2.	Multiple criteria available:
o	Request ID (exact or partial)
o	Payee name
o	Requisition Unit
o	Approval Status
o	Confirmation Status
o	Dispensed Status
o	Date range selection
3.	Click "SEARCH" to apply all filters
4.	Use "Clear" to reset all filters
Data Management
Refreshing Data
•	Click "Refresh Data" button
•	Fetches latest data from central database
•	Updates summary statistics in real-time
•	Essential after other users make changes
Table Customization
•	Column Sorting: Click column headers to sort ascending/descending
•	Column Resizing: Drag column borders to adjust widths
•	Row Colors: Automatic color coding by status for quick visual reference
•	Selection: Single click to select, double-click for details
________________________________________
Biometric Signature Integration
Device Management
Connecting the Device
1.	Ensure ZKTECO device is properly connected via USB
2.	Click "Connect Device" in button panel
3.	Wait for "Device Status: Connected ✓" confirmation
4.	Operation status shows "Device connected successfully"
Troubleshooting Connection Issues
•	Check physical USB connection
•	Verify device drivers are properly installed
•	Ensure device is not being used by another application
•	Restart application if connection issues persist
Disconnecting Properly
•	Click "Disconnect Device" when finished
•	Wait for confirmation message
•	Device status shows "Disconnected"
Fingerprint Verification Process
During Workflow Steps
1.	System automatically prompts for fingerprint verification when required
2.	Place finger on scanner when instructed
3.	System captures fingerprint template
4.	Matches against database of authorized users
5.	Loads corresponding digital signature
6.	Displays verification success message
Verification Failure Scenarios
•	No match found: Verify user is registered in the system database
•	Poor quality scan: Clean finger and scanner surface, try again
•	Device error: Check connection and restart application if needed
Signature Storage and Retrieval
Digital Signatures
•	Stored securely in encrypted database
•	Associated with specific employee records
•	Automatically retrieved during verification processes
•	Displayed in details view and export documents
Signature Security Features
•	Encrypted storage for protection
•	Access-controlled retrieval
•	Complete audit trail of all signature uses
•	Cannot be modified or tampered with by users
________________________________________
Reporting and Export
Export to PDF
Single Record Export
1.	Open "View Details" for a specific request
2.	Click "Export To PDF" button (enabled when all signatures available)
3.	System generates comprehensive PDF including:
o	Hospital header with logo
o	Basic request information
o	Detailed status information
o	All biometric signatures
o	Generation timestamp and audit trail
4.	PDF automatically saved to Documents/PurchaseFunds/ folder
5.	File automatically opens after creation for review
PDF Export Requirements
•	All required signatures must be available in system
•	System validates signature availability before allowing export
•	Missing signatures trigger clear warning message
Export to Excel
Available Export Options:
1.	Tabular Export (Date Range)
o	Multiple records in organized spreadsheet format
o	Includes all data columns from main table
o	Summary statistics and totals
2.	Selected Row Details
o	Single record with detailed information layout
o	Formatted professional presentation
o	Basic signature information included
3.	Date Range Details with Images
o	Multiple records with comprehensive details
o	Includes status information and verification data
Excel Export Process
1.	Choose desired export option from dialog
2.	Select date range if required for the export
3.	Choose save location and filename
4.	System generates Excel file (.xlsx format)
5.	Success confirmation with record count summary
Export to Word
Document Types Available:
1.	Tabular Export: Professional table format with multiple records
2.	Selected Row Details: Formatted document for single record
3.	Date Range Details: Comprehensive report for multiple records
Word Document Features:
•	Professional formatting and layout
•	Hospital branding and headers
•	Organized table of data
•	Summary information section
•	Timestamp and generation details
Printing Options
Available Print Types:
1.	Tabular Print (Date Range)
o	Multiple records in clean table format
o	Summary statistics and overview
o	Professional layout for reporting
2.	Selected Row Details with Images
o	Single record comprehensive printout
o	Includes all available information
o	Signature representations
3.	Date Range Details with Images
o	Multiple detailed records in formatted sections
o	Status summaries and verification information
Print Process
1.	Select appropriate print option
2.	Choose date range if needed for the printout
3.	Print dialog opens for printer selection
4.	Select printer and adjust settings as needed
5.	Confirm print job execution
Report Customization
Date Range Selection
•	From Date: Start of reporting period
•	To Date: End of reporting period
•	Validation: Ensures logical date range selection
•	Empty results: Clear notification if no records found in selected range
Content Filtering
•	Combine with search filters for precision
•	Status-based filtering options
•	Department/unit specific filtering
•	Amount range filtering capabilities
________________________________________
Troubleshooting
Common Issues and Solutions
Biometric Device Problems
Issue: "Device Not Connected"
•	✅ Check USB cable connection thoroughly
•	✅ Verify device power indicator is on
•	✅ Reinstall device drivers if necessary
•	✅ Restart application completely
Issue: "Fingerprint Not Recognized"
•	✅ Clean fingerprint scanner surface with appropriate cloth
•	✅ Ensure finger is clean, dry, and free of debris
•	✅ Try different finger if one isn't working
•	✅ Contact system admin to verify enrollment in system
Issue: "No Matching Employee Found"
•	✅ Verify employee is properly registered in system
•	✅ Check that fingerprint template is stored in database
•	✅ Contact system administrator for assistance
Application Errors
Issue: Buttons Appear Disabled
•	✅ Verify a row is selected in the main table
•	✅ Check user role permissions and access levels
•	✅ Confirm workflow sequence is being followed correctly
•	✅ Ensure request isn't voided or already completed
Issue: "Cannot Save Request"
•	✅ Check database connection status
•	✅ Verify all required fields are properly completed
•	✅ Ensure network connectivity is stable
•	✅ Contact IT support if issue persists
Issue: Slow System Performance
•	✅ Check network connection speed
•	✅ Verify database server status and response times
•	✅ Clear search filters if working with large datasets
•	✅ Restart application to refresh system resources
Workflow Blockers
Issue: "Cannot Dispense - Active Fund in Use"
•	✅ Wait for current active fund to be fully utilized
•	✅ Contact finance department about current fund status
•	✅ System prevents overlapping fund dispensations for control
Issue: "Request Already Processed"
•	✅ Check current status in detailed view
•	✅ Verify you're attempting the correct workflow step
•	✅ Contact previous processor if clarification needed
Error Messages Reference
Error Message	Cause	Solution
"Device Status: Not Connected"	Biometric device offline	Connect device and click "Connect Device"
"No Selection"	No row selected in table	Click on a request in the table to select it
"Cannot Void Dispensed Request"	Attempting to void completed request	Void only possible before dispensing occurs
"Missing Signatures"	Required signatures not available	Ensure all verifiers are registered in system
"Database Error"	Connection or query failure	Check network, contact IT support
"Active Fund in Use"	Another fund currently active	Wait for current fund to be utilized or closed
System Maintenance
Regular Operational Checks
•	✅ Verify biometric device connectivity daily
•	✅ Confirm database connection is active
•	✅ Check user role assignments are current
•	✅ Validate signature database integrity periodically
Data Backup Procedures
•	Automated daily database backups
•	Manual export capability for important records
•	Archive completed requests periodically
•	Regular system health checks
________________________________________
Best Practices
Request Creation
•	Be Specific and Detailed: Provide comprehensive reasons for purchase fund requests
•	Accurate Amounts: Request exact amounts needed with proper justification
•	Correct Payee Selection: Verify payee information before submission
•	Timely Submission: Submit requests well in advance of when funds are needed
Security Practices
•	Never Share Credentials: Your username is personal and should not be shared
•	Secure Device Access: Only authorized personnel should use biometric device
•	Proper Log Out: Always close application when not in use
•	Report Suspicious Activity: Immediately report any unusual system behavior
Workflow Efficiency
•	Follow Sequential Order: Complete workflow steps in proper sequence
•	Batch Processing: Group similar requests when possible for efficiency
•	Regular Status Monitoring: Check request status frequently for updates
•	Clear Communication: Use notes fields for important information and context
Data Management
•	Regular Exports: Export important records for personal backup and records
•	Utilize Search Features: Use filters to quickly find specific records
•	Information Verification: Double-check details before finalizing any actions
•	Proper Archiving: Move completed requests from active view when appropriate
System Usage
•	Stay Updated: Ensure application is kept up to date with latest versions
•	Device Care: Handle biometric device with care and keep it clean
•	Network Awareness: Be mindful of network issues during critical operations
•	Backup Habits: Maintain personal copies of important transactions and records
________________________________________
Support and Contact
Internal Support Channels
•	IT Help Desk: Extension 4357 for technical issues
•	Finance Department: For workflow and approval process questions
•	System Administrator: For user access and role change requests
Documentation Resources
•	This comprehensive user guide available in digital format
•	Quick reference guides posted in departmental areas
•	Video tutorials accessible on hospital intranet
•	Regular training session materials
Training Opportunities
•	New user orientation sessions conducted monthly
•	Role-specific training workshops available
•	Quarterly refresher courses for existing users
•	One-on-one training sessions by appointment
Feedback and Improvement
•	System improvement suggestions always welcome
•	Report issues through IT help desk ticketing system
•	User experience surveys conducted annually
•	Continuous improvement based on user feedback




**7.Receipt Based Purchase Fund Analytics Dashboard  End User Documentation**
Table of Contents
1.	System Overview
2.	Dashboard Interface
3.	Data Filtering and Search
4.	Analytics and Charts
5.	Export and Reporting
6.	Advanced Features
7.	Troubleshooting
8.	Best Practices
________________________________________
System Overview
Purpose
The Receipt-Based Purchase Fund Analytics Dashboard provides comprehensive financial analysis and reporting capabilities for purchase fund management at Afran General Hospital. It enables users to track, analyze, and report on all receipt-based purchase fund requests with advanced categorization and status tracking.
Key Features
•	Multi-dimensional Analytics: Table views, pie charts, bar charts, line charts, and advanced analytics
•	Advanced Category Management: Main categories and sub-categories with database integration
•	Comprehensive Filtering: Date ranges, categories, status filters, and custom search
•	Multiple Export Formats: Excel, PDF, Word with professional formatting
•	Real-time Data Visualization: Interactive charts and summary metrics
•	Performance Optimization: Debounced filtering and efficient data handling
User Roles and Permissions
•	All Users: Access to view analytics based on their department/role
•	Finance Team: Full access to financial analytics and exports
•	Department Heads: Access to department-specific data
•	Administrators: Complete system access with all features
________________________________________
Dashboard Interface
Main Components
1. Header Section
•	Dashboard Title: "Receipt-Based Purchase Fund Analytics Dashboard"
•	Subtitle: "Comprehensive Financial Analysis and Reporting"
•	Control Buttons: Export options, print, refresh, dark mode toggle
2. Filter Section
•	Date Range: From/To date pickers with calendar interface
•	Text Filters: Unit, payee, general search across all fields
•	Category Filters: Main category and sub-category dropdowns (database-driven)
•	Status Filters: Approval, confirmation, dispensed, receipt upload, void status
•	Action Buttons: Reset filters, advanced filters
3. Main Content Area (Tabbed Interface)
•	Data Table Tab: Detailed record view with sorting and context menu
•	Status Distribution Tab: Pie chart showing request status distribution
•	Monthly Analysis Tab: Bar chart of monthly funding trends
•	Trend Analysis Tab: Line chart for time-based analysis
•	Category Analysis Tab: Combined pie and bar charts for categories
•	Advanced Analytics Tab: KPIs and detailed metrics with insights
4. Summary Section
•	Real-time summary cards showing key performance indicators
•	Color-coded metrics for quick reference
•	Automatic updates with filter changes
________________________________________
Data Filtering and Search
Category-Based Filtering
Main Categories
The system automatically loads categories from the database:
•	Medical Supplies: Surgical instruments, disposables, protective equipment
•	Office Supplies: Stationery, furniture, IT equipment
•	Equipment: Medical devices, maintenance tools, monitoring equipment
•	Medications: Antibiotics, analgesics, emergency drugs
•	Laboratory: Reagents, test kits, lab equipment
•	Radiology: X-ray supplies, contrast media, film
Sub-Category Management
•	Dynamic Loading: Sub-categories load based on selected main category
•	Database-Driven: Categories are maintained in the database system
•	Automatic Updates: Changes in categories reflect immediately in the dashboard
Filter Types
1. Date Range Filtering
•	From Date: Start date for analysis period
•	To Date: End date for analysis period
•	Behavior: Inclusive range (includes both start and end dates)
2. Text-based Filters
•	Requisition Unit: Filter by department or organizational unit
•	Payee: Filter by employee receiving funds
•	Quick Search: Searches across all text fields simultaneously
3. Status Filters
•	Approval Status: Pending, Approved, All
•	Confirmation Status: Pending, Confirmed, All
•	Dispensed Status: Yes, No, All
•	Dispense Approval: Pending, Approved, All
•	Receipt Upload: Pending, Uploaded, All
•	Void Status: Yes, No, All
Advanced Filtering Features
Debounced Search
•	Automatic Delay: 300ms delay after typing before applying filters
•	Performance Optimization: Prevents excessive filtering during rapid typing
•	Smooth User Experience: No lag during data entry
Combined Filter Logic
•	AND Logic: All active filters must match
•	Real-time Updates: Charts and summary update immediately
•	Progressive Filtering: Start broad and narrow down gradually
Resetting Filters
•	One-click Reset: "Reset Filters" button clears all filters
•	Complete Clear: Returns to showing all records
•	Instant Update: Immediate refresh of all displays
________________________________________
Analytics and Charts
Data Table Tab
Features
•	Sortable Columns: Click any column header to sort
•	Context Menu: Right-click for additional operations
•	Status Coloring: Color-coded status indicators
•	Real-time Count: Shows number of displayed records
Context Menu Options
•	Export Selected: Export chosen records to Excel
•	Copy to Clipboard: Copy record details in tabular format
•	View Details: Show comprehensive record information
Column Information
•	Request ID: Unique identifier for each request
•	Unit: Requisition department/unit
•	Main Category: Primary category classification
•	Sub Category: Specific sub-category
•	Payee: Recipient of funds
•	Amount: Formatted currency display (ETB)
•	Date: Request submission date
•	Status: Color-coded approval status
•	Receipt Upload: Upload status with color coding
Chart Types and Analysis
1. Status Distribution (Pie Chart)
•	Visualization: Circular chart showing status proportions
•	Data: Count of requests by approval status
•	Colors: Green (Approved), Yellow (Pending), Blue (Confirmed)
•	Interactivity: Hover for exact counts and percentages
2. Monthly Analysis (Bar Chart)
•	X-Axis: Months of the year
•	Y-Axis: Total amount in ETB
•	Purpose: Identify seasonal spending patterns
•	Usage: Budget planning and trend analysis
3. Trend Analysis (Line Chart)
•	X-Axis: Dates formatted as "MMM dd"
•	Y-Axis: Daily total amounts
•	Purpose: Track spending patterns over time
•	Features: Smooth line connecting data points
4. Category Analysis
•	Pie Chart: Distribution of requests across categories
•	Bar Chart: Total spending by category
•	Combined View: Comprehensive category insights
•	Usage: Identify high-spending categories
Advanced Analytics Tab
Key Performance Indicators (KPIs)
•	Approval Rate: Percentage of approved requests
•	Receipt Upload Rate: Percentage with uploaded receipts
•	Average Processing Time: Typical request processing duration
•	Pending Actions: Number requiring attention
Category Insights
•	Detailed Analysis: Spending patterns by category
•	Trend Identification: Category-specific trends
•	Actionable Insights: Recommendations for optimization
Performance Metrics
•	Efficiency Score: Combined metric of approval and receipt rates
•	Bottleneck Identification: Process stages causing delays
•	Recommendations: Data-driven improvement suggestions
________________________________________
Export and Reporting
Comprehensive Export Options
1. Excel Export
Features:
•	Multiple Sheets: Executive Summary, Detailed Records, Category Analysis, Charts & Analysis, Advanced Analytics
•	Professional Formatting: Headers, colors, and auto-sized columns
•	Formulas and Calculations: Automated summary calculations
•	Chart Data: Underlying data for all visualizations
Export Process:
1.	Click "Export Excel" button
2.	Choose save location and filename
3.	System generates comprehensive workbook
4.	File automatically opens after export
Sheet Structure:
•	Executive Summary: Overview and key metrics
•	Detailed Records: Complete data in tabular format
•	Category Analysis: Breakdown by main and sub-categories
•	Charts & Analysis: Data tables for chart recreation
•	Advanced Analytics: KPIs and recommendations
2. PDF Export
Features:
•	Professional Document: Cover page, table of contents, structured sections
•	Print-ready Format: Optimized for physical printing
•	Comprehensive Content: All analysis and data
•	Signature Sections: Approval and authorization areas
Document Sections:
•	Cover Page: Hospital branding and report metadata
•	Executive Summary: Key findings and metrics
•	Category Analysis: Detailed category breakdown
•	Detailed Records: Complete data listing
•	Analysis & Charts: Analytical insights
•	Signatures: Approval and authorization
3. Word Export
Features:
•	Editable Format: Microsoft Word document
•	Structured Sections: Headings and subheadings
•	Table-based Data: Professional table formatting
•	Business Report Style: Standard business document format
Printing Capabilities
Print Options
•	Print Report: Full dashboard printout
•	Professional Layout: Optimized for paper printing
•	Multi-page Support: Automatic pagination
•	Header/Footer: Professional document formatting
Print Features
•	Cover Page: Hospital branding and title
•	Summary Section: Key metrics and findings
•	Category Analysis: Visual and tabular data
•	Detailed Records: Comprehensive data listing
•	Analysis Section: Insights and recommendations
•	Footer: Confidentiality notice and timestamps
Individual Record Operations
Viewing Record Details
1.	Select record in table
2.	Right-click → "View Details" or use context menu
3.	Comprehensive popup shows all record information
4.	Organized in easy-to-read format
Record Information Displayed
•	Basic Information: ID, Unit, Categories, Payee, Amount, Date
•	Status Information: Approval, Confirmation, Dispense statuses
•	Personnel: Approved By, Confirmed By, Dispensed By
•	Additional Data: Receipt upload, Void status, Reason
Copying to Clipboard
•	Formatted Output: Tab-separated values for easy pasting
•	Complete Information: All key fields included
•	Multi-record Support: Copy multiple selected records
•	Excel-ready Format: Direct paste into spreadsheet applications
________________________________________
Advanced Features
Category Management System
Database Integration
•	Automatic Loading: Categories loaded from central database
•	Consistent Classification: Standardized across all requests
•	Easy Maintenance: Categories managed through database administration
•	Real-time Updates: Changes reflect immediately in dashboard
Category Hierarchy
•	Main Categories: Broad classification groups
•	Sub-Categories: Specific items within main categories
•	Dynamic Relationship: Sub-categories change based on main category selection
•	Validation: Ensures data consistency and accuracy
Performance Optimization
Debounced Filtering
•	300ms Delay: Prevents excessive filtering during typing
•	Smooth Performance: No interface lag
•	Efficient Processing: Optimized for large datasets
•	User Experience: Responsive and fast
Data Handling
•	Efficient Queries: Optimized database access
•	Memory Management: Proper handling of large datasets
•	Background Processing: Non-blocking user interface
•	Error Handling: Graceful degradation on errors
Dark Mode Support
•	Toggle Feature: Switch between light and dark themes
•	Eye Comfort: Reduced eye strain in low-light environments
•	Consistent Experience: All components theme-aware
•	Preference Memory: Remembers user preference
Advanced Analytics
Efficiency Calculations
•	Approval Rate: Percentage of approved requests
•	Receipt Upload Rate: Percentage with uploaded receipts
•	Processing Efficiency: Combined performance metric
•	Trend Analysis: Performance changes over time
Bottleneck Identification
•	Stage Analysis: Identify slowest process stages
•	Recommendations: Data-driven improvement suggestions
•	Performance Metrics: Quantitative performance measurements
•	Actionable Insights: Specific improvement actions
Category Insights
•	Spending Patterns: Identify high-spending categories
•	Department Analysis: Department-specific trends
•	Budget Optimization: Data for budget planning
•	Resource Allocation: Informed resource distribution
________________________________________
Troubleshooting
Common Issues and Solutions
Data Display Issues
Problem	Solution
No data showing	Check filters, click "Reset Filters"
Categories not loading	Refresh data, check database connection
Charts not updating	Ensure filters are applied, wait for debounce
Missing sub-categories	Select a main category first
Export Problems
Problem	Solution
Export fails	Check disk space, file permissions
Excel file won't open	Ensure Excel is installed
PDF formatting issues	Update PDF reader
Large export timeout	Use more specific filters
Performance Issues
Problem	Solution
Slow loading	Reduce dataset size with filters
Filter lag	Use more specific search criteria
Interface freezing	Close other applications
Memory issues	Restart application
Error Messages
Error Message	Meaning	Action Required
"No data to export"	No records match current filters	Adjust filters or reset to show all records
"Export failed"	File system or permission issue	Check save location, ensure write permissions
"Database error"	Connection or query issue	Check network, contact administrator
"Memory error"	Too much data for system	Use filters to reduce dataset
Data Quality Issues
Handling Missing Data
•	Null categories: Displayed as empty or "Uncategorized"
•	Missing dates: Handled gracefully in date filters
•	Incomplete records: Clearly marked in displays
•	Data validation: Automatic filtering of corrupt data
Data Consistency
•	Category validation: Ensures valid category combinations
•	Date validation: Prevents invalid date ranges
•	Status validation: Maintains consistent status values
•	Amount validation: Ensures numeric values only
________________________________________
Best Practices
Efficient Dashboard Usage
1. Filter Strategy
•	Start Broad: Begin with few filters, narrow down gradually
•	Use Date Ranges: Focus on relevant time periods
•	Category First: Use category filters before other criteria
•	Progressive Refinement: Add filters incrementally
2. Data Analysis Approach
•	Regular Reviews: Schedule weekly/monthly analytics sessions
•	Comparative Analysis: Compare different time periods
•	Department Focus: Analyze department-specific patterns
•	Trend Identification: Look for patterns over time
3. Reporting Strategy
•	Standardized Exports: Use consistent naming conventions
•	Scheduled Reports: Set regular export schedules
•	Data Archiving: Maintain historical reports
•	Distribution Planning: Plan report distribution channels
Performance Optimization
Large Dataset Management
•	Use Specific Date Ranges: Limit data to relevant periods
•	Apply Category Filters: Reduce data by category first
•	Export in Batches: Split large exports if needed
•	Pre-filter Data: Use database views for common filters
System Resource Management
•	Close Other Applications: Free up system resources
•	Adequate RAM: Ensure sufficient memory for large datasets
•	Regular Maintenance: Clear cache and temporary files
•	System Updates: Keep software updated
Data Security and Compliance
Access Control
•	Role-based Permissions: Ensure appropriate data access
•	Export Controls: Limit sensitive data exports
•	Audit Trails: Maintain access and export records
•	Data Classification: Identify sensitive information
Confidential Information Handling
•	Secure Storage: Protect exported files
•	Controlled Distribution: Limit report distribution
•	Proper Disposal: Secure deletion of sensitive data
•	Compliance Adherence: Follow hospital data policies
Backup and Recovery
Regular Backups
•	Export Key Reports: Regular export of important analyses
•	Data Archiving: Maintain historical data archives
•	Configuration Backup: Save filter and setting configurations
•	Documentation: Maintain system documentation
Disaster Recovery
•	Procedure Documentation: Document export and analysis procedures
•	Training: Train multiple staff members
•	System Documentation: Maintain current system documentation
•	Recovery Testing: Regular testing of recovery procedures
________________________________________
Quick Reference Guide
Keyboard Shortcuts
•	Ctrl + F: Focus on search field
•	Ctrl + R: Refresh data
•	Ctrl + E: Export dialog
•	Ctrl + P: Print options
•	Esc: Close dialogs/cancel operations
•	Tab: Navigate between filters
Common Workflows
Monthly Financial Reporting
1.	Set date range to month boundaries
2.	Apply department filters if needed
3.	Review status distribution chart
4.	Analyze category spending
5.	Export comprehensive PDF report
6.	Print summary for management meetings
Department Budget Analysis
1.	Filter by specific department
2.	Analyze category distribution
3.	Review approval and receipt rates
4.	Export detailed Excel analysis
5.	Share findings with department head
Audit Preparation
1.	Export comprehensive Excel report
2.	Generate PDF with all records
3.	Print selection for physical files
4.	Archive digital copies
5.	Document findings and observations
Category Performance Review
1.	Filter by specific category
2.	Analyze spending trends over time
3.	Review approval efficiency
4.	Compare with other categories
5.	Generate recommendations report
Export Templates
Standard Report Package
1.	Executive Summary: PDF format for management
2.	Detailed Analysis: Excel format for deep analysis
3.	Category Breakdown: Word format for department reviews
4.	Print-ready Summary: For physical distribution
Custom Export Strategies
•	Department-specific: Filter by department before export
•	Time-period focused: Specific date ranges for period analysis
•	Category-focused: Deep dive into specific categories
•	Status-based: Analysis of pending/approved/completed requests
________________________________________
Support and Resources
Technical Support
•	IT Help Desk: Extension 1234
•	Email Support: it-support@afranhospital.gov.et
•	Office Hours: 8:00 AM - 5:00 PM, Monday-Friday
•	Emergency Contact: After-hours support for critical issues
Training Resources
•	User Manuals: Complete documentation available
•	Video Tutorials: Step-by-step operation guides
•	Workshop Sessions: Regular training workshops
•	Quick Reference Cards: Laminated guides for common tasks
System Information
•	Version: Receipt-Based Purchase Fund Analytics Dashboard v3.0
•	Last Updated: December 2024
•	Compatibility: Windows 10/11, Java 8+, Microsoft Office for exports
•	Database: Integrated with hospital HRMS system
Feedback and Improvement
•	User Feedback Portal: Online suggestion system
•	Feature Requests: Submit via hospital ticketing system
•	Bug Reports: Immediate reporting for critical issues
•	User Group Meetings: Quarterly user feedback sessions





**8.Receipt Based Purchase Fund Management System End User Documentation**
Table of Contents
1.	System Overview
2.	User Roles and Permissions
3.	Getting Started
4.	Main Interface Overview
5.	Purchase Fund Request Workflow
6.	Receipt Management
7.	Detailed Feature Guide
8.	Biometric Signature Integration
9.	Reporting and Export
10.	Available Balance Management
11.	Troubleshooting
12.	Best Practices
________________________________________
System Overview
Purpose
The Receipt-Based Purchase Fund Management System is designed to manage purchase fund requests that require receipt submission for accountability. The system ensures proper documentation, approval workflows, and financial tracking for all hospital purchases.
Key Features
•	Receipt Upload and Management: Secure storage and viewing of purchase receipts
•	Biometric Authentication: Fingerprint verification for all approvals
•	Category-based Purchasing: Organized by main and sub-categories
•	Automated Workflow: Structured approval and confirmation process
•	Dual Signature Verification: For fund dispensing operations
•	Comprehensive Reporting: Export to Excel, Word, and PDF with receipt attachments
•	Real-time Balance Tracking: Monitor available purchase funds
________________________________________
User Roles and Permissions
Administrator
•	Full system access
•	Can create, edit, void, and delete requests
•	Upload receipts for any request
•	Approve requests and dispenses
•	Device management capabilities
Finance Administrator
•	Create new purchase fund requests
•	Void requests
•	View request details
•	Approve requests and dispense approvals
Accountant
•	View request details
•	Confirm requests after approval and receipt upload
•	Export and print reports
•	Access receipt documentation
Cashier
•	Upload receipts for approved requests
•	Void requests
•	View request details
•	Dispense funds (with dual biometric verification)
•	Export and print reports
________________________________________
Getting Started
System Requirements
•	Windows 10/11
•	Java Runtime Environment 8+
•	ZKTECO fingerprint scanner device
•	Internet connection for database access
•	File upload capabilities for receipts
Initial Setup
1.	Connect Fingerprint Device
o	Click "Connect Device" button
o	Ensure USB connection is secure
o	Wait for "Device Status: Connected ✓" confirmation
2.	User Authentication
o	System automatically detects your role based on login credentials
o	Buttons and features adjust according to your permissions
3.	Database Connection
o	Automatic connection to hospital database
o	No manual configuration required
________________________________________
Main Interface Overview
Header Section
•	Hospital name and department
•	Current date display
•	System title: "RECEIPT BASED PURCHASE FUND MANAGEMENT"
Summary Panel
•	Real-time statistics of purchase fund requests
•	Shows totals, pending, approved, confirmed, and dispensed counts
•	Total amount summary
Button Panel
Workflow Buttons (Role-based):
•	New Request
•	Edit Request
•	Upload Receipt
•	Void Request
•	Delete Request
•	View Details
•	Approve Request
•	Confirm Request
•	Dispense Cash
•	Approve Dispense
Utility Buttons:
•	Connect/Disconnect Device
•	Export to Excel/Word/PDF
•	Print Reports
•	Advanced Search
•	Refresh Data
Data Table
•	Displays all purchase fund requests
•	Color-coded status indicators
•	Double-click any row to view full details including receipts
•	Sortable and filterable columns
________________________________________
Purchase Fund Request Workflow
Step 1: Create New Request
1.	Click "New Request" button
2.	Fill in required fields:
o	Requisition Unit: Your department name
o	Main Category: Select from dropdown (e.g., Medical Supplies, Office Equipment)
o	Sub Category: Automatically populated based on main category selection
o	Reason: Detailed explanation of purchase need
o	Payee: Select employee from list
o	Amount: Enter amount in ETB
3.	Validation Rules:
o	Amount cannot exceed maximum purchase fund limit
o	Amount cannot exceed available purchase fund balance
o	All required fields must be completed
4.	Automatic Request ID Generation:
o	Format: RCBPFYYYYMMDD### (e.g., RCBPF20231215001)
o	Daily sequential numbering
Step 2: Request Approval
1.	Finance Admin selects request and clicks "Approve Request"
2.	Biometric Verification Required:
o	Approver must verify identity via fingerprint
o	System loads approver's digital signature
o	Optional approval notes can be added
3.	Request status changes to "Approved"
Step 3: Receipt Upload (Required)
1.	Cashier/Admin selects approved request and clicks "Upload Receipt"
2.	Biometric Verification Required:
o	Uploader must verify identity via fingerprint
3.	File Selection:
o	Select up to 10 receipt files
o	Maximum total size: 50MB
o	Supported formats: PDF, Images (JPG, PNG, GIF), Word, Excel
4.	Files are securely stored and linked to the request
Step 4: Fund Dispensing
1.	Cashier selects receipt-uploaded request and clicks "Dispense Cash"
2.	Dual Biometric Verification Required:
o	Step 1: Verify Payee identity
o	Step 2: Verify Dispenser identity
3.	Enter dispensing details:
o	Given amount (defaults to requested amount)
o	Completion date
o	Given by (auto-filled from biometric verification)
4.	Both signatures are digitally recorded
Step 5: Confirmation
1.	Accountant selects dispensed request and clicks "Confirm Request"
2.	Biometric Verification Required:
o	Confirmer must verify identity via fingerprint
o	System loads confirmer's digital signature
3.	Request status changes to "Confirmed"
Step 6: Dispense Approval
1.	Finance Admin selects confirmed request and clicks "Approve Dispense"
2.	Biometric Verification Required:
o	Approver must verify identity via fingerprint
o	System loads approver's digital signature
3.	Request status changes to "Approved" for dispense
________________________________________
Receipt Management
Uploading Receipts
Requirements:
•	Request must be approved
•	User must be authenticated via fingerprint
•	Maximum 10 files per request
•	Total size limit: 50MB
•	Supported formats: PDF, JPG, PNG, GIF, DOC, DOCX, XLS, XLSX
Upload Process:
1.	Select approved request
2.	Click "Upload Receipt"
3.	Verify identity via fingerprint
4.	Select files from computer
5.	Review file list and confirm upload
6.	System stores files securely in database
Viewing Receipts
In Request Details:
•	Double-click any request or click "View Details"
•	Receipts section displays all uploaded files
•	File preview available for images and PDFs
•	Download individual receipts
•	View file information (name, size, type)
Receipt Card Features:
•	File icon and type identification
•	File size information
•	Image preview for visual files
•	PDF preview with document icon
•	Download and view buttons
Receipt File Organization
•	All receipts automatically organized by Request ID
•	Files stored in: Documents/Receipts/[Request_ID]/
•	Original filenames preserved
•	Secure database backup
________________________________________
Available Balance Management & Replenishment
Current Available Amount Calculation
The system automatically calculates available purchase funds based on:
•	Current Status = "In Use" funds
•	Dispense Approval Status = "Approved" transactions
Automatic Replenishment Trigger
When available amount reaches 0 ETB:
1.	System automatically changes replenish status to "Completed"
2.	Finance department is notified for fund replenishment
3.	New purchase fund cycle begins
Balance Validation During Requests
When creating new requests:
•	System checks: Requested Amount ≤ Available Balance
•	If insufficient funds: "Requested amount exceeds available purchase fund balance" error
•	Prevents over-committing funds
Real-time Balance Updates
•	Available amount updates immediately after dispense approval
•	No manual intervention required
•	Transparent fund tracking
•	Automatic replenishment notifications
________________________________________
Detailed Feature Guide
Creating Purchase Fund Requests
Form Fields Explained:
•	Requisition Unit: The department or unit requesting funds
•	Main Category: Broad classification (e.g., Medical Supplies, Office Equipment, Maintenance)
•	Sub Category: Specific item type (automatically populated)
•	Reason: Detailed justification for the purchase
•	Payee: Employee who will receive the funds
•	Amount: Requested amount in Ethiopian Birr
Category Management:
•	Main Categories: Pre-defined broad classifications
•	Sub Categories: Dynamic based on main category selection
•	Ensures consistent purchasing classification
•	Supports reporting and analysis by category
Validation Rules:
•	Maximum request limit: System-defined
•	Available balance check: Prevents over-committing funds
•	Required field validation: All fields marked with * are mandatory
•	Category selection: Both main and sub-category required
Editing Requests
•	Only available for non-voided, non-dispensed requests
•	Select request and click "Edit Request"
•	Modify necessary fields
•	Category changes update sub-category options
•	Save changes
Voiding Requests
•	Available for non-dispensed requests
•	Mandatory void reason required
•	Permanent record maintained for audit trail
•	Cannot be undone
•	Receipts remain attached for reference
Deleting Requests
•	Only available for non-dispensed requests
•	Permanent deletion from system
•	Use with caution - prefer voiding instead
•	Associated receipts also deleted
Viewing Request Details
•	Double-click any row or click "View Details"
•	Complete request information including categories
•	Status history and audit trail
•	Biometric signatures
•	Receipt attachments with preview
•	Export to PDF capability (excluding receipts for file size)
________________________________________
Biometric Signature Integration
Device Connection
1.	Connect Device: Establishes connection with fingerprint scanner
2.	Status Indicators:
o	Green: Connected and ready
o	Red: Disconnected or error
o	Yellow: Processing
Fingerprint Verification Process
1.	Click verification button in any approval dialog
2.	Place finger on scanner when prompted
3.	System captures fingerprint template
4.	Matches against employee database
5.	Loads corresponding digital signature
6.	Verification status displayed
Signature Storage
•	All signatures stored securely in database
•	Linked to employee records
•	Cannot be modified by users
•	Audit trail maintained for all verifications
Dual Verification for Dispensing
•	Payee Verification: Ensures funds go to correct recipient
•	Dispenser Verification: Ensures proper dispensing authorization
•	Both signatures permanently recorded
•	Prevents fraudulent transactions
________________________________________
Reporting and Export
Export to Excel
Options Available:
1.	Tabular Export: Basic data in spreadsheet format
2.	Selected Row Export: Detailed information for specific request
3.	Date Range Export: Filter by specific time period
Export to Word
Options Available:
1.	Tabular Report: Formatted table in Word document
2.	Detailed Report: Comprehensive request details
3.	Date Range Report: Custom time period selection
Export to PDF
•	Available from request details view
•	Includes all signatures and status information
•	Professional formatting for official records
•	Note: Receipts excluded from PDF export due to file size considerations
Printing Options
1.	Tabular Print: Quick data table printout
2.	Selected Row Print: Detailed single request print
3.	Date Range Print: Custom period with basic information
Advanced Search
Search Criteria:
•	Request ID
•	Payee name
•	Requisition unit
•	Approval status
•	Confirmation status
•	Dispensed status
•	Date range
•	Category filters
Receipt Export and Download
•	Individual receipt download from details view
•	Automatic organization by Request ID
•	Bulk download available for multiple receipts
•	Original file formats preserved
________________________________________
Troubleshooting
Common Issues and Solutions
Fingerprint Device Issues
Problem: "Device Not Connected" error
Solutions:
1.	Check USB connection
2.	Click "Connect Device" button
3.	Restart application if persistent
4.	Verify device drivers are installed
Problem: Fingerprint not recognized
Solutions:
1.	Ensure finger is clean and dry
2.	Reposition finger on scanner
3.	Contact admin to verify fingerprint enrollment
Receipt Upload Issues
Problem: "Upload Failed" error
Solutions:
1.	Check file size (max 50MB total)
2.	Verify file format is supported
3.	Ensure request is approved
4.	Check network connection
Problem: "Maximum files exceeded"
Solution: Reduce number of files (max 10 per request)
Request Creation Issues
Problem: "Amount exceeds maximum limit" error
Solution: Reduce requested amount to within allowed limits
Problem: "Amount exceeds available balance" error
Solution:
•	Check current available purchase fund balance
•	Reduce amount or wait for fund replenishment
•	Contact finance department for balance inquiry
System Performance
Problem: Slow loading or unresponsive
Solutions:
1.	Click "Refresh Data" button
2.	Check network connection
3.	Close and reopen application
4.	Clear temporary files if necessary
Error Messages Reference
Error Message	Cause	Solution
"Device Status: Connection Failed"	Fingerprint device not detected	Check USB connection and drivers
"No matching employee found"	Fingerprint not in database	Contact system administrator
"Cannot upload to non-approved request"	Request not approved	Wait for approval or contact approver
"File size exceeds limit"	Total files > 50MB	Reduce number or size of files
"Maximum files exceeded"	More than 10 files selected	Select fewer files
"Void reason is required"	Mandatory field missing	Enter reason for voiding
________________________________________
Best Practices
For Requesters
•	Plan purchase needs in advance
•	Provide detailed and accurate reasons
•	Select appropriate categories for reporting
•	Verify amounts before submission
•	Keep within departmental budgets
For Receipt Uploaders
•	Upload receipts promptly after approval
•	Ensure receipt clarity and readability
•	Use descriptive filenames when possible
•	Verify all receipts are included before finalizing
•	Keep digital copies for personal records
For Approvers
•	Verify request legitimacy before approval
•	Check category appropriateness
•	Use biometric verification for all approvals
•	Add meaningful approval notes when necessary
•	Monitor available balances regularly
For Cashiers
•	Always perform dual verification during dispensing
•	Verify payee identity matches request
•	Ensure receipts are uploaded before dispensing
•	Maintain accurate dispensing records
•	Report any discrepancies immediately
For All Users
•	Keep login credentials secure
•	Log out when not using the system
•	Report system issues promptly
•	Regularly update stored fingerprints if physical changes occur
•	Backup important receipts locally
System Maintenance
•	Regular database backups
•	Keep fingerprint device clean
•	Update software as new versions become available
•	Regular audit of system logs and receipts
•	Monitor storage capacity for receipt files
________________________________________
Support and Contact Information
Technical Support
•	IT Department: Extension 1234
•	Email: it-support@afranhospital.gov.et
•	Office Hours: 8:00 AM - 5:00 PM, Monday-Friday
Finance Department
•	Purchase Fund Administrator: Extension 5678
•	Email: finance@afranhospital.gov.et
•	Balance Inquiries: Available during office hours
Emergency Support
For critical system issues outside office hours, contact:
•	On-call IT Support: +251-911-234-567
Receipt Support
•	Scanning Assistance: IT Department
•	File Format Issues: Technical Support
•	Upload Problems: System Administrator
________________________________________
Version Information
Current Version: Receipt-Based Purchase Fund Management System v3.0
Last Updated: December 2025
Compatibility: Smart HRMS Integration Ready
Receipt Storage: Secure database with backup
Maximum File Upload: 10 files, 50MB total per request



**9.VAT Purchase Details Management System**
Complete Technical Documentation
________________________________________
TABLE OF CONTENTS
1.	System Overview
2.	Architecture & Dependencies
3.	Class Structure
4.	Data Model
5.	User Interface Components
6.	Core Functionalities
7.	Validation Rules
8.	Business Logic
9.	Purchase Types Reference
10.	Database Operations
11.	Reporting & Export
12.	Voiding System
13.	Filtering & Search
14.	User Guide
15.	Troubleshooting
16.	Code Reference
________________________________________
SYSTEM OVERVIEW
Purpose
The VAT Purchase Details Management System is a comprehensive JavaFX-based application designed for managing Value Added Tax (VAT) purchase records. It provides a complete solution for recording, tracking, voiding, and reporting VAT purchase transactions with full audit trail capabilities, specifically designed for Ethiopian tax compliance.
Key Features
•	Complete CRUD Operations with voiding instead of deletion
•	Multi-currency VAT Calculation (15% VAT for taxable purchases)
•	6 Purchase Types covering all VAT categories
•	Dual Calendar Support (Gregorian and Ethiopian)
•	Receipt Type Validation (Machine vs Manual receipts)
•	Advanced Filtering by date range, status, and search terms
•	Export Capabilities (CSV, PDF with formatting)
•	Print Functionality with formatted reports
•	Audit Trail with user tracking and void reasons
•	Real-time Calculations and validation
Target Users
•	Finance Department Staff
•	Purchase Administrators
•	Tax Compliance Officers
•	Auditors
•	Accounts Payable Team
•	System Administrators
________________________________________
ARCHITECTURE & DEPENDENCIES
Technology Stack
•	Frontend: JavaFX (UI Framework)
•	Language: Java 8+
•	Database: MySQL (via Connecting class)
•	PDF Generation: iText PDF Library
•	Printing: JavaFX Print API
Package Structure
text
smarthrms/
├── VatPurchaseDetailsFX.java    # Main UI Controller
├── VatPurchaseModel.java         # Data Model
├── Connecting.java               # Database Connection
└── resources/                    # Additional resources
Dependencies
java
// JavaFX Core
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

// Java I/O
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

// iText PDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

// Java AWT for Desktop operations
import java.awt.Desktop;
________________________________________
CLASS STRUCTURE
Class: VatPurchaseDetailsFX
Extends: BorderPane
Key Properties
Property	Type	Description
con	Connecting	Database connection handler
currentUser	String	Currently logged-in username
masterList	ObservableList<VatPurchaseModel>	Complete dataset from database
filteredList	ObservableList<VatPurchaseModel>	Date-range filtered data
activeList	ObservableList<VatPurchaseModel>	Non-voided records only
table	TableView<VatPurchaseModel>	Main data display table
ROWS_PER_PAGE	static final int	Pagination: 20 rows/page
UI Component Properties
Component	Type	Purpose
vatCategory	ComboBox<String>	G = Goods, S = Services
calendarType	ComboBox<String>	G = Gregorian, E = Ethiopian
purchaseType	ComboBox<Integer>	1-6 (see Purchase Types)
unitMeasure	ComboBox<Integer>	Unit codes (2-10)
sellerTin	TextField	10-digit TIN
sellerName	TextField	Seller name
receiptNumber	TextField	FS or M format
mrcNumber	TextField	Machine Receipt Code
description	TextField	Item description
quantity	TextField	Numeric quantity
unitPrice	TextField	Price per unit
datePicker	DatePicker	Purchase date
fromDatePicker	DatePicker	Filter start date
toDatePicker	DatePicker	Filter end date
totalValue	Label	Calculated total
vatAmount	Label	Calculated VAT
totalAfterVat	Label	Total + VAT
footerTotal	Label	Grand total all
activeTotalLabel	Label	Active view total
validationLabel	Label	Error messages
searchField	TextField	Search input
viewFilter	ComboBox<String>	View mode selector
showVoidedCheckBox	CheckBox	Toggle voided view
________________________________________
DATA MODEL
Class: VatPurchaseModel
Fields
Field	Type	Description
vatCategory	String	G or S (Default: S)
calendarType	String	G or E (Default: G)
purchaseType	int	1-6 (see purchase types)
sellerTin	String	10-digit number (optional)
sellerName	String	Seller name (optional)
dateOfPurchase	String	YYYY-MM-DD
receiptNumber	String	FS or M format
mrcNumber	String	Machine Receipt Code
description	String	Item description
unitMeasure	int	Unit code (2-10)
quantity	double	Numeric quantity
unitPrice	double	Price per unit
totalValue	double	qty × price
vatAmount	double	15% if taxable (types 1-5)
totalAfterVat	double	total + vat
voided	boolean	Void status
voidedBy	String	User who voided
voidedAt	String	Timestamp
voidReason	String	Void reason
createdBy	String	User who created
Unit Measure Codes
Code	Meaning	Description
2	KG	Kilogram
3	ML	Milliliter
4	GM	Gram
5	LIT	Liter
6	MT	Metric Ton
7	PCS	Pieces (Default)
8	CT	Carton
9	OTHER	Other units
10	PC	Piece
________________________________________
USER INTERFACE COMPONENTS
Main Window Layout
text
BorderPane
├── Center: ScrollPane
│   └── VBox (mainContainer)
│       ├── Title Label
│       ├── TitledPane (Documentation)
│       ├── Validation Label
│       ├── Form GridPane
│       ├── Buttons HBox
│       ├── Search Bar HBox
│       ├── Date Range Filter HBox
│       ├── Table Container VBox
│       │   ├── Table Label
│       │   ├── Filter Controls HBox
│       │   ├── TableView
│       │   └── Totals HBox
│       └── Pagination
Form Fields Layout
Row 1: Core Types
Column 0	Column 1	Column 2	Column 3	Column 4	Column 5
VAT Category*	[ComboBox G/S]	Calendar Type*	[ComboBox G/E]	Purchase Type*	[ComboBox 1-6]
Row 2: Seller & Unit
| Unit Measure* | [ComboBox 2-10] | Seller TIN | [TextField] | Seller Name | [TextField] |
Row 3: Receipt & Date
| Receipt No* | [TextField] | MRC No | [TextField] | Date* | [DatePicker] |
Row 4: Description (spans all columns)
| Description* | [TextField - spans 5 columns] |
Row 5: Quantity & Price
| Quantity* | [TextField] | Unit Price* | [TextField] | Total Value | [Label: 0.00] |
Row 6: VAT Results
| VAT (15%) | [Label: 0.00] | After VAT | [Label: 0.00] |
Button Bar
Button	Color	Action
💾 Save	Green (#27ae60)	Save current record
🚫 Void Record	Red (#e74c3c)	Void selected record
↩️ Unvoid Record	Orange (#f39c12)	Restore voided record
🧹 Clear & Reset	Gray (#95a5a6)	Clear form, set defaults
📊 Export CSV	Blue (#3498db)	Export all active to CSV
📄 Export PDF	Purple (#9b59b6)	Export all active to PDF
🖨️ Print	Dark Blue (#34495e)	Print all active records
Filter Controls
View Filter ComboBox
•	All Records: Shows all including voided
•	Active Only: Shows only non-voided (Default)
•	Voided Only: Shows only voided records
Show Voided CheckBox
•	Checked: Shows all records
•	Unchecked: Shows active only
Date Range Filter
•	From Date: Start date (default: 1 month ago)
•	To Date: End date (default: today)
•	Apply Filter: Apply date range
•	Clear Filter: Reset to default
•	📊 Export Filtered CSV: Export current filter view
•	🖨️ Print Filtered: Print current filter view
•	📄 Export Filtered PDF: Export current filter as PDF
Search Field
•	Real-time search across:
o	Receipt Number
o	Seller Name
o	Description
o	Seller TIN
o	MRC Number
________________________________________
CORE FUNCTIONALITIES
1. Save Purchase (savePurchase())
Process Flow:
1.	Clear previous validation errors
2.	Validate mandatory fields
3.	Validate TIN if provided
4.	Validate MRC if FS receipt
5.	Create VatPurchaseModel object
6.	Set createdBy to current user
7.	Call con.saveVatPurchases(purchase)
8.	Show success message with details
9.	Clear form and set defaults
10.	Reload data from page 0
11.	Refresh table display
Success Message:
text
✅ Purchase saved successfully!
Receipt: FS12345678
Purchase Type: Taxable-local Inputs (15% VAT)
Amount: 1,150.00
Recorded by: admin
2. Void Purchase (voidPurchase())
Process Flow:
1.	Check if record selected
2.	Verify record not already voided
3.	Show void dialog with reason input
4.	Validate reason not empty
5.	Call con.voidVatPurchase(receipt, user, reason)
6.	Update model properties:
o	setVoided(true)
o	setVoidedBy(currentUser)
o	setVoidedAt(now)
o	setVoidReason(reason)
7.	Refresh table with red styling
8.	Update totals
Void Dialog:
text
╔══════════════════════════════════════════════╗
║ Void Purchase Record                          ║
║══════════════════════════════════════════════║
║ Are you sure you want to void this record?   ║
║ Receipt: FS12345678                          ║
║ Amount: 1,150.00                             ║
║                                              ║
║ Void Reason: [_____________________________] ║
║             [_____________________________]   ║
║                                              ║
║                    [Void] [Cancel]           ║
╚══════════════════════════════════════════════╝
3. Calculate (calculate())
Formula:
java
total = quantity × unitPrice
vat = (purchaseType != 6) ? total × 0.15 : 0  // VAT only for types 1-5
afterVat = total + vat
Triggers:
•	Quantity field change
•	Unit Price field change
•	Purchase Type change
•	Form population
4. Load Data (loadPage(int pageIndex))
Process:
1.	Calculate offset: pageIndex × ROWS_PER_PAGE
2.	Call con.fetchAllVatPurchases(offset, ROWS_PER_PAGE)
3.	Update masterList
4.	Create active filter: !purchase.isVoided()
5.	Apply current view filter
6.	Set table items
7.	Update totals
8.	Refresh table
________________________________________
VALIDATION RULES
Regular Expressions
java
// TIN: Exactly 10 digits
Pattern TIN_PATTERN = Pattern.compile("^\\d{10}$");

// Machine Receipt: FS + 8 digits
Pattern RECEIPT_MACHINE_PATTERN = Pattern.compile("^FS\\d{8}$");

// Manual Receipt: M + any digits
Pattern RECEIPT_MANUAL_PATTERN = Pattern.compile("^M\\d+$");
Mandatory Fields Check
Field	Condition	Error Message
VAT Category	Not null/empty	VAT Category is required
Calendar Type	Not null/empty	Calendar Type is required
Purchase Type	Not null	Purchase Type is required
Unit Measure	Not null	Unit Measure is required
Date	Not null	Date is required
Description	Not empty	Description is required
Quantity	Numeric, not empty	Valid quantity is required
Unit Price	Numeric, not empty	Valid unit price is required
Receipt Number	Valid format	Valid receipt number required
MRC	If receipt starts with FS	MRC Number is REQUIRED for machine receipts
Field-Specific Validation
TIN Validation
•	Empty: Allowed (optional)
•	Non-empty: Must be exactly 10 digits
•	Visual: Red border on error, Green on valid
Receipt Validation
•	Machine Receipt (FS): Must be exactly FS + 8 digits
•	Manual Receipt (M): Must be M followed by any digits
•	MRC Requirement:
o	FS: MRC mandatory (yellow highlight)
o	M: MRC optional (normal highlight)
Numeric Fields
•	Quantity: Accepts integers and decimals
•	Unit Price: Accepts integers and decimals
•	No commas or special characters allowed
________________________________________
BUSINESS LOGIC
VAT Calculation Rules
Purchase Type	Code	VAT Rate	Description
Capital Assets (Local)	1	15%	Taxable-local purchase of Capital Assets (Line No. 65)
Capital Assets (Imported)	2	15%	Taxable-imported purchase of Capital Assets (Line No. 75)
Inputs (Local)	3	15%	Taxable-local purchase of Inputs (Line No. 100)
Inputs (Imported)	4	15%	Taxable-imported purchase of Inputs (Line No. 110)
Expense Inputs	5	15%	Taxable-general Expense Inputs (Line No. 120)
Exempt	6	0%	Tax Exempted purchase with no VAT (Line No. 85 or 130)
Receipt Number Rules
Machine Receipt (FS...):
•	Format: FS + exactly 8 digits
•	Example: FS12345678
•	MRC Number: MANDATORY
•	Used for: Electronic/fiscal receipts
Manual Receipt (M...):
•	Format: M + any digits
•	Example: M1234, M99999
•	MRC Number: OPTIONAL
•	Used for: Handwritten/manual receipts
Unit of Measure Logic
Code	Display	Use Case
2	KG	Weight-based items
3	ML	Liquids in small quantities
4	GM	Small weight items
5	LIT	Liquids in volume
6	MT	Heavy items
7	PCS	Countable items (Default)
8	CT	Boxed items
9	OTHER	Miscellaneous
10	PC	Individual pieces
Voiding Business Rules
1.	Only active records can be voided
2.	Void reason is mandatory for audit trail
3.	Voided records are NOT deleted - remain in database
4.	Voided records appear in RED in the table
5.	Voided records EXCLUDED from:
o	Financial calculations
o	Grand totals
o	Reports and exports
o	Printing
6.	Voiding is logged with user and timestamp
7.	Unvoiding is disabled for compliance reasons
________________________________________
PURCHASE TYPES REFERENCE
Complete Purchase Type Specifications
Code	Name	VAT Rate	Tax Line	Description
1	Capital Assets (Local)	15%	Line 65	Purchase of capital assets from local suppliers
2	Capital Assets (Imported)	15%	Line 75	Imported capital assets with VAT paid at customs
3	Inputs (Local)	15%	Line 100	Raw materials, components from local suppliers
4	Inputs (Imported)	15%	Line 110	Imported raw materials with VAT paid at customs
5	Expense Inputs	15%	Line 120	General business expenses subject to VAT
6	Exempt	0%	Line 85/130	VAT-exempt purchases (no input VAT claimed)
Color Coding in Table
Type	Text Color	Background (Even)	Background (Odd)
1 (Cap-Local)	#3498db (Blue)	#e8f4fc	#d1e9f9
2 (Cap-Imp)	#2980b9 (Dark Blue)	#d6eaf8	#aed6f1
3 (Input-Local)	#27ae60 (Green)	#e8f6f3	#d1f2eb
4 (Input-Imp)	#2ecc71 (Light Green)	#d4f4dd	#abebc6
5 (Expense)	#f39c12 (Orange)	#fef9e7	#fcf3cf
6 (Exempt)	#e74c3c (Red)	#fdedec	#fadbd8
________________________________________
DATABASE OPERATIONS
Connecting Class Methods
saveVatPurchases(VatPurchaseModel purchase)
•	Purpose: Insert new VAT purchase record
•	Parameters: Complete VatPurchaseModel object
•	Returns: void (throws exception on error)
fetchAllVatPurchases(int offset, int limit)
•	Purpose: Retrieve paginated VAT purchases
•	Parameters:
o	offset: Starting row
o	limit: Number of rows
•	Returns: List<VatPurchaseModel>
voidVatPurchase(String receiptNumber, String user, String reason)
•	Purpose: Mark record as voided
•	Updates:
o	voided = true
o	voided_by = user
o	voided_at = NOW()
o	void_reason = reason
unvoidVatPurchase(String receiptNumber)
•	Purpose: Restore voided record
•	Updates: Reset all void fields to NULL
Database Schema (Assumed)
sql
CREATE TABLE vat_purchases (
    id INT PRIMARY KEY AUTO_INCREMENT,
    vat_category CHAR(1),
    calendar_type CHAR(1),
    purchase_type INT,
    seller_tin VARCHAR(10),
    seller_name VARCHAR(255),
    date_of_purchase DATE,
    receipt_number VARCHAR(20) UNIQUE,
    mrc_number VARCHAR(50),
    description TEXT,
    unit_measure INT,
    quantity DECIMAL(10,2),
    unit_price DECIMAL(10,2),
    total_value DECIMAL(10,2),
    vat_amount DECIMAL(10,2),
    total_after_vat DECIMAL(10,2),
    voided BOOLEAN DEFAULT FALSE,
    voided_by VARCHAR(50),
    voided_at DATETIME,
    void_reason TEXT,
    created_by VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
________________________________________
REPORTING & EXPORT
CSV Export
Format:
csv
Status,VAT Category,Calendar Type,Purchase Type,Seller TIN,Seller Name,Date,Receipt Number,MRC Number,Description,Unit Measure,Quantity,Unit Price,Total Value,VAT Amount,Total After VAT,Recorded By
ACTIVE,"S","G",3,"1234567890","ABC Suppliers","2024-01-15","FS12345678","MRC001","Raw Material",7,100.00,10.00,1000.00,150.00,1150.00,"admin"
Features:
•	Excludes voided records
•	Includes MRC Number field
•	Includes Recorded By field
•	Comma-separated with quotes
•	Opens automatically after save
PDF Export
Structure:
text
┌─────────────────────────────────────────────────────┐
│       SMART HRMS - VAT ACTIVE PURCHASES REPORT       │
│           FILTERED VAT ACTIVE PURCHASES REPORT        │
│         Date Range: 2024-01-01 to 2024-01-31         │
│         Voided Records Excluded                       │
│                                                       │
│ Generated on: 2024-01-31 | Generated by: admin        │
│ Total Active Records: 50                               │
│═══════════════════════════════════════════════════════│
│ Status │ VAT │ Cal │ Type │ TIN │ ... │ After VAT    │
│═══════════════════════════════════════════════════════│
│ ACTIVE │ S   │ G   │ 3    │ ... │ 1,150.00         │
│ ACTIVE │ G   │ E   │ 6    │ ... │ 1,000.00         │
│ ...                                                  │
│═══════════════════════════════════════════════════════│
│                                                       │
│          Summary                                      │
│ Total Active Records: 50                              │
│ Total VAT Paid: 7,500.00                              │
│ Total Purchases Amount: 50,000.00                     │
│                                                       │
│ This report excludes voided records...                │
└─────────────────────────────────────────────────────┘
Features:
•	Professional formatting
•	Color-coded purchase types
•	Automatic pagination for large datasets
•	Summary totals
•	Header on each page
•	Voided records disclaimer
Printing
Print Layout:
•	Title: VAT ACTIVE PURCHASES REPORT
•	Disclaimer: VOIDED RECORDS ARE EXCLUDED
•	Metadata: Generation date and user
•	Summary: Record count and totals
•	Table: Formatted with all columns
•	Grand Total: Highlighted footer
________________________________________
VOIDING SYSTEM
Visual Indicators
Status	Text Color	Background (Even)	Background (Odd)
Active (Type 1)	Black	#e8f4fc	#d1e9f9
Active (Type 2)	Black	#d6eaf8	#aed6f1
Active (Type 3)	Black	#e8f6f3	#d1f2eb
Active (Type 4)	Black	#d4f4dd	#abebc6
Active (Type 5)	Black	#fef9e7	#fcf3cf
Active (Type 6)	Black	#fdedec	#fadbd8
Voided	Red (#e74c3c)	#ffebee	#ffcdd2
Audit Trail Fields
java
// For voided records
purchase.isVoided()        // boolean: true/false
purchase.getVoidedBy()     // String: username
purchase.getVoidedAt()     // String: timestamp
purchase.getVoidReason()   // String: reason provided

// For created records
purchase.getCreatedBy()    // String: username
Voiding Flow Diagram
text
[User selects record] → [Check if active] → [Show void dialog]
                       ↓
                  [Enter reason]
                       ↓
        [Reason not empty?] → No → [Disable Void button]
                       ↓ Yes
           [Call voidVatPurchase()]
                       ↓
    [Update model: voided=true, voidedBy, voidedAt, reason]
                       ↓
          [Refresh table with red styling]
                       ↓
       [Update totals (exclude voided)]
                       ↓
         [Show success message]
Unvoiding Restrictions
Current Behavior:
•	Unvoid button shows informational dialog
•	Unvoid functionality is disabled
•	Reason: Audit compliance
Dialog Message:
text
╔════════════════════════════════════════════════════╗
║ Unvoid Function Disabled                            ║
║════════════════════════════════════════════════════║
║ Unvoid functionality is currently disabled for VAT  ║
║ purchase records.                                    ║
║                                                     ║
║ For audit and compliance reasons:                    ║
║ • Once a VAT purchase is voided, it cannot be restored║
║ • Voided records remain in the system for audit      ║
║ • If you need to correct a voided record, create new ║
║ • Contact system administrator for exceptional cases ║
║                                                     ║
║                                   [OK]               ║
╚════════════════════════════════════════════════════╝
________________________________________
FILTERING & SEARCH
View Filter Logic
java
switch(filter) {
    case "All Records":
        table.setItems(masterList);  // Show all, including voided
        break;
    case "Active Only":
        // Filter out voided records
        FilteredList<VatPurchaseModel> activeFiltered = 
            masterList.filtered(purchase -> !purchase.isVoided());
        activeList.setAll(activeFiltered);
        table.setItems(activeList);
        break;
    case "Voided Only":
        // Show only voided records
        FilteredList<VatPurchaseModel> voidedFiltered = 
            masterList.filtered(VatPurchaseModel::isVoided);
        table.setItems(voidedFiltered);
        break;
}
Date Range Filter
Process:
1.	Get fromDate and toDate from pickers
2.	Validate: fromDate ≤ toDate
3.	Filter masterList by date range
4.	Store in filteredList
5.	Set table items to filteredList
6.	Update totals
7.	Show summary with:
o	Record count
o	Total purchases
o	VAT paid
Success Message:
text
✅ Filter applied successfully!
Date Range: 2024-01-01 to 2024-01-31
Active Records Found: 25
Total Purchases: ETB 45,678.90
VAT Paid: ETB 5,951.85
Search Filter
Search Fields:
•	Receipt Number
•	Seller Name
•	Description
•	Seller TIN
•	MRC Number
Implementation:
java
filtered.setPredicate(purchase ->
    purchase.getReceiptNumber().toLowerCase().contains(searchTerm) ||
    purchase.getSellerName().toLowerCase().contains(searchTerm) ||
    purchase.getDescription().toLowerCase().contains(searchTerm) ||
    purchase.getSellerTin().toLowerCase().contains(searchTerm) ||
    purchase.getMrcNumber().toLowerCase().contains(searchTerm)
);
________________________________________
USER GUIDE
Getting Started
1. Initial Screen
When you open the VAT Purchase Details module, you'll see:
•	Documentation panel (collapsible)
•	Data entry form
•	Button toolbar
•	Search bar
•	Date range filter
•	Data table with active records
•	Totals footer
2. Setting Default Values
The system automatically sets:
•	VAT Category: S (Services)
•	Calendar Type: G (Gregorian)
•	Purchase Type: 3 (Taxable-local Inputs)
•	Unit Measure: 7 (PCS)
•	Date: Today
•	Date Range: Last 30 days
Entering a New Purchase
Step-by-Step Process
1.	Select VAT Category
o	G = Goods (physical products)
o	S = Services (default)
2.	Select Calendar Type
o	G = Gregorian (standard)
o	E = Ethiopian (optional)
3.	Select Purchase Type
o	1 = Capital Assets (Local)
o	2 = Capital Assets (Imported)
o	3 = Inputs (Local) - DEFAULT
o	4 = Inputs (Imported)
o	5 = Expense Inputs
o	6 = Exempt
4.	Select Unit Measure
o	Choose appropriate unit (default: PCS)
5.	Enter Seller Information (Optional)
o	TIN: Exactly 10 digits if provided
o	Name: Optional
6.	Enter Receipt Number
o	Machine: FS12345678 (8 digits)
o	Manual: M1234 (any digits)
7.	Enter MRC Number
o	Required for FS receipts
o	Optional for M receipts
8.	Enter Date
o	Default: Today
o	Can be changed
9.	Enter Description
o	Item or service description
10.	Enter Quantity & Price
o	Numbers only (no commas)
o	Decimals allowed
o	Totals calculate automatically
11.	Review Calculated Values
o	Total Value: qty × price
o	VAT: 15% if taxable (types 1-5)
o	After VAT: total + VAT
12.	Click Save (💾)
Voiding a Record
1.	Select the record in the table
2.	Click Void Record (🚫)
3.	Enter void reason (required)
4.	Click Void to confirm
5.	Record turns red and excluded from totals
Filtering Records
By Status
•	Use View dropdown
•	Choose: All, Active Only, Voided Only
By Date Range
1.	Set From Date and To Date
2.	Click Apply Filter
3.	Table shows only records in range
4.	Click Clear Filter to reset
By Search
1.	Type in search box
2.	Real-time filtering on:
o	Receipt number
o	Seller name
o	Description
o	TIN
o	MRC number
Exporting Data
CSV Export
•	Click Export CSV (📊)
•	Choose filename and location
•	Opens automatically in Excel
PDF Export
•	Click Export PDF (📄)
•	Professional formatted report
•	Includes summary totals
Filtered Export
1.	Apply date range filter
2.	Click Export Filtered CSV/PDF
3.	Only filtered records exported
Printing
1.	Click Print (🖨️)
2.	Preview dialog appears
3.	Select printer
4.	Click Print
________________________________________
TROUBLESHOOTING
Common Issues and Solutions
Issue: "Error loading data"
Cause: Database connection problem
Solution:
•	Check database server is running
•	Verify connection settings in Connecting class
•	Check network connectivity
Issue: Validation errors persist
Cause: Missing or invalid field values
Solution:
•	Check all red-bordered fields
•	Ensure receipt format is correct
•	Verify quantity/price are numbers
Issue: MRC field not saving
Cause: Field not included in save operation
Solution:
•	Ensure mrcNumber is set in model
•	Check database column exists
Issue: Voided records still show in totals
Cause: Filter not applied correctly
Solution:
•	Use "Active Only" view
•	Verify isVoided() check in calculations
Issue: PDF export fails
Cause: iText library missing or file permission
Solution:
•	Ensure iText JAR is in classpath
•	Check write permissions on target folder
Error Messages Guide
Message	Meaning	Action
"TIN must be exactly 10 digits"	Invalid TIN format	Enter 10 digits only
"Receipt must be FS+8digits or M+digits"	Wrong receipt format	Check format guide
"MRC Number is REQUIRED for machine receipts"	Missing MRC for FS receipt	Enter MRC number
"Valid quantity is required"	Quantity empty or invalid	Enter numeric value
"This record is already voided"	Cannot void again	Record already voided
"'From' date cannot be after 'To' date"	Invalid date range	Correct date order
________________________________________
CODE REFERENCE
Key Method Signatures
Initialization
java
public VatPurchaseDetailsFX(String username)
private void buildUI()
private GridPane createForm()
private HBox createButtons()
private void buildTable()
private TitledPane createDocumentationPane()
Data Operations
java
private void savePurchase()
private void loadPage(int pageIndex)
private void refreshData()
private void populateForm(VatPurchaseModel p)
private void clearForm()
private void setDefaults()
Voiding
java
private void voidPurchase()
private void unvoidPurchase()
Validation
java
private boolean validateTin()
private boolean validateReceipt()
private boolean validateMandatoryFields()
private boolean isNumeric(String str)
Calculations
java
private void calculate()
private void updateTotals()
Filtering
java
private void filterData()
private void applyDateRangeFilter()
private void clearDateRangeFilter()
private void applyViewFilter()
private void toggleVoidedVisibility()
Export/Print
java
private void exportCSV()
private void exportFilteredCSV()
private void exportPDF()
private void exportFilteredPDF()
private void exportDataToPDF(File file, ObservableList<VatPurchaseModel> data, String title, String subtitle)
private void printTable()
private void printFilteredData()
private Node createPrintableNode(ObservableList<VatPurchaseModel> dataToPrint, String title)
private TableView<VatPurchaseModel> createPrintTableView()
Important Constants
java
private static final int ROWS_PER_PAGE = 20;
private static final Pattern TIN_PATTERN = Pattern.compile("^\\d{10}$");
private static final Pattern RECEIPT_MACHINE_PATTERN = Pattern.compile("^FS\\d{8}$");
private static final Pattern RECEIPT_MANUAL_PATTERN = Pattern.compile("^M\\d+$");
Color Coding Reference
java
// Buttons
Green: "#27ae60"    // Save
Red: "#e74c3c"      // Void
Orange: "#f39c12"   // Unvoid
Gray: "#95a5a6"     // Clear
Blue: "#3498db"     // Export CSV
Purple: "#9b59b6"   // Export PDF
Dark Blue: "#34495e" // Print
Light Green: "#2ecc71" // Export Filtered CSV

// Text
Dark Gray: "#2c3e50" // Title
Red: "#e74c3c"       // Errors, Voided, Exempt
Green: "#27ae60"     // Active totals, Type 3
Blue: "#3498db"      // Type 1
Dark Blue: "#2980b9" // Type 2
Light Green: "#2ecc71" // Type 4
Orange: "#f39c12"    // Type 5

// Backgrounds - Type specific
Light Blue: "#e8f4fc"    // Type 1 (even)
Blue: "#d1e9f9"          // Type 1 (odd)
Light Sky: "#d6eaf8"     // Type 2 (even)
Sky: "#aed6f1"           // Type 2 (odd)
Light Green: "#e8f6f3"   // Type 3 (even)
Green: "#d1f2eb"         // Type 3 (odd)
Light Mint: "#d4f4dd"    // Type 4 (even)
Mint: "#abebc6"          // Type 4 (odd)
Light Yellow: "#fef9e7"  // Type 5 (even)
Yellow: "#fcf3cf"        // Type 5 (odd)
Light Pink: "#fdedec"    // Type 6 (even)
Pink: "#fadbd8"          // Type 6 (odd)

// Voided backgrounds
Light Red: "#ffebee"     // Voided (even)
Red: "#ffcdd2"           // Voided (odd)
________________________________________
APPENDIX
Version History
Version	Date	Changes
1.0.0	Initial	Base functionality
1.1.0	Update	Added MRC field
1.2.0	Update	Enhanced voiding system
1.3.0	Update	Added 6 purchase types
1.4.0	Current	Added Recorded By field
Purchase Type Line References
Type	Line No.	Description
1	65	Taxable-local Purchase of Capital Assets
2	75	Taxable-imported Purchase of Capital Assets
3	100	Taxable-local Purchase of Inputs
4	110	Taxable-imported Purchase of Inputs
5	120	Taxable-general Expense Inputs Purchase
6	85/130	Tax Exempted Purchase
Future Enhancements
1.	Batch Operations: Bulk void/export multiple records
2.	Advanced Analytics: Charts and trend analysis
3.	User Permissions: Role-based access control
4.	Audit Log Viewer: Interface for void history
5.	Email Reports: Scheduled report delivery
6.	API Integration: External system connectivity
7.	Tax Report Generation: Automated VAT return forms
Support Contact
For technical support or questions:
•	System Administrator: [admin email]
•	Developer: [developer contact]
•	Documentation: [documentation location]
Glossary
Term	Definition
VAT	Value Added Tax - 15% consumption tax
TIN	Tax Identification Number - 10-digit unique identifier
MRC	Machine Receipt Code - identifier for fiscal receipts
FS	Fiscal receipt prefix for machine-generated receipts
M	Manual receipt prefix for handwritten receipts
Capital Assets	Long-term assets used in business operations
Inputs	Raw materials and components used in production
Expense Inputs	General business expenses subject to VAT
Exempt	VAT-exempt purchases with no input tax credit
Void	Mark a record as invalid while maintaining audit trail
________________________________________
Document Version: 1.4.0
Last Updated: January 2024
© Smart HRMS - VAT Purchase Management System



10.VAT Sales Details Management System
Complete Technical Documentation
________________________________________
TABLE OF CONTENTS
1.	System Overview
2.	Architecture & Dependencies
3.	Class Structure
4.	Data Model
5.	User Interface Components
6.	Core Functionalities
7.	Validation Rules
8.	Business Logic
9.	Database Operations
10.	Reporting & Export
11.	Voiding System
12.	Filtering & Search
13.	User Guide
14.	Troubleshooting
15.	Code Reference
________________________________________
SYSTEM OVERVIEW
Purpose
The VAT Sales Details Management System is a comprehensive JavaFX-based application designed for managing Value Added Tax (VAT) sales records. It provides a complete solution for recording, tracking, voiding, and reporting VAT sales transactions with full audit trail capabilities.
Key Features
•	Complete CRUD Operations with voiding instead of deletion
•	Multi-currency VAT Calculation (15% VAT for taxable sales)
•	Dual Calendar Support (Gregorian and Ethiopian)
•	Receipt Type Validation (Machine vs Manual receipts)
•	Advanced Filtering by date range, status, and search terms
•	Export Capabilities (CSV, PDF with formatting)
•	Print Functionality with formatted reports
•	Audit Trail with user tracking and void reasons
•	Real-time Calculations and validation
Target Users
•	Finance Department Staff
•	Sales Administrators
•	Tax Compliance Officers
•	Auditors
•	System Administrators
________________________________________
ARCHITECTURE & DEPENDENCIES
Technology Stack
•	Frontend: JavaFX (UI Framework)
•	Language: Java 8+
•	Database: MySQL (via Connecting class)
•	PDF Generation: iText PDF Library
•	Printing: JavaFX Print API
Package Structure
text
smarthrms/
├── VatSaleDetailsFX.java       # Main UI Controller
├── VatSaleModel.java            # Data Model
├── Connecting.java              # Database Connection
└── resources/                   # Additional resources
Dependencies
java
// JavaFX Core
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

// Java I/O
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

// iText PDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

// Java AWT for Desktop operations
import java.awt.Desktop;
________________________________________
CLASS STRUCTURE
Class: VatSaleDetailsFX
Extends: BorderPane
Key Properties
Property	Type	Description
con	Connecting	Database connection handler
currentUser	String	Currently logged-in username
masterList	ObservableList<VatSaleModel>	Complete dataset from database
filteredList	ObservableList<VatSaleModel>	Date-range filtered data
activeList	ObservableList<VatSaleModel>	Non-voided records only
table	TableView<VatSaleModel>	Main data display table
ROWS_PER_PAGE	static final int	Pagination: 20 rows/page
UI Component Properties
Component	Type	Purpose
vatCategory	ComboBox<String>	G = Goods, S = Services
calendarType	ComboBox<String>	G = Gregorian, E = Ethiopian
saleType	ComboBox<Integer>	1=Taxable, 2=Zero, 3=Exempt
unitMeasure	ComboBox<Integer>	Unit codes (2-10)
buyerTin	TextField	10-digit TIN
buyerName	TextField	Buyer name
receiptNumber	TextField	FS or M format
mrcNumber	TextField	MRC for FS receipts
description	TextField	Item description
quantity	TextField	Numeric quantity
unitPrice	TextField	Price per unit
datePicker	DatePicker	Sale date
fromDatePicker	DatePicker	Filter start date
toDatePicker	DatePicker	Filter end date
totalValue	Label	Calculated total
vatAmount	Label	Calculated VAT
totalAfterVat	Label	Total + VAT
footerTotal	Label	Grand total all
activeTotalLabel	Label	Active view total
validationLabel	Label	Error messages
searchField	TextField	Search input
viewFilter	ComboBox<String>	View mode selector
showVoidedCheckBox	CheckBox	Toggle voided view
________________________________________
DATA MODEL
Class: VatSaleModel
Fields
Field	Type	Description
vatCategory	String	G or S
calendarType	String	G or E
saleType	int	1, 2, or 3
buyerTin	String	10-digit number
buyerName	String	Optional
dateOfSale	String	YYYY-MM-DD
receiptNumber	String	FS or M format
mrcNumber	String	Machine Receipt Code
description	String	Item description
unitMeasure	int	Unit code
quantity	double	Numeric quantity
unitPrice	double	Price per unit
totalValue	double	qty × price
vatAmount	double	15% if taxable
totalAfterVat	double	total + vat
voided	boolean	Void status
voidedBy	String	User who voided
voidedAt	String	Timestamp
voidReason	String	Void reason
createdBy	String	User who created
Unit Measure Codes
Code	Meaning	Description
2	KG	Kilogram
3	ML	Milliliter
4	GM	Gram
5	LIT	Liter
6	MT	Metric Ton
7	PCS	Pieces (Default)
8	CT	Carton
9	OTHER	Other units
10	PC	Piece
________________________________________
USER INTERFACE COMPONENTS
Main Window Layout
text
BorderPane
├── Center: ScrollPane
│   └── VBox (mainContainer)
│       ├── Title Label
│       ├── TitledPane (Documentation)
│       ├── Validation Label
│       ├── Form GridPane
│       ├── Buttons HBox
│       ├── Search Bar HBox
│       ├── Date Range Filter HBox
│       ├── Table Container VBox
│       │   ├── Table Label
│       │   ├── Filter Controls HBox
│       │   ├── TableView
│       │   └── Totals HBox
│       └── Pagination
Form Fields Layout
Row 1: Core Types
Column 0	Column 1	Column 2	Column 3	Column 4	Column 5
VAT Category*	[ComboBox G/S]	Calendar Type*	[ComboBox G/E]	Sale Type*	[ComboBox 1/2/3]
Row 2: Buyer & Unit
| Unit Measure* | [ComboBox 2-10] | Buyer TIN | [TextField] | Buyer Name | [TextField] |
Row 3: Receipt & Date
| Receipt No* | [TextField] | MRC No | [TextField] | Date* | [DatePicker] |
Row 4: Description (spans all columns)
| Description* | [TextField - spans 5 columns] |
Row 5: Quantity & Price
| Quantity* | [TextField] | Unit Price* | [TextField] | Total Value | [Label: 0.00] |
Row 6: VAT Results
| VAT (15%) | [Label: 0.00] | After VAT | [Label: 0.00] |
Button Bar
Button	Color	Action
💾 Save	Green (#27ae60)	Save current record
🚫 Void Record	Red (#e74c3c)	Void selected record
🔄 Unvoid Record	Orange (#f39c12)	Restore voided record
🧹 Clear & Reset	Gray (#95a5a6)	Clear form, set defaults
📊 Export CSV	Blue (#3498db)	Export all active to CSV
📄 Export PDF	Purple (#9b59b6)	Export all active to PDF
🖨️ Print	Dark Blue (#34495e)	Print all active records
Filter Controls
View Filter ComboBox
•	All Records: Shows all including voided
•	Active Only: Shows only non-voided (Default)
•	Voided Only: Shows only voided records
Show Voided CheckBox
•	Checked: Shows all records
•	Unchecked: Shows active only
Date Range Filter
•	From Date: Start date (default: 1 month ago)
•	To Date: End date (default: today)
•	Apply Filter: Apply date range
•	Clear Filter: Reset to default
•	Export Filtered CSV: Export current filter view
•	Print Filtered: Print current filter view
•	Export Filtered PDF: Export current filter as PDF
Search Field
•	Real-time search across:
o	Receipt Number
o	Buyer Name
o	Description
o	Buyer TIN
________________________________________
CORE FUNCTIONALITIES
1. Save Sale (saveSale())
Process Flow:
1.	Clear previous validation errors
2.	Validate mandatory fields
3.	Validate TIN if provided
4.	Create VatSaleModel object
5.	Set createdBy to current user
6.	Call con.saveVatSales(sale)
7.	Show success message
8.	Clear form and set defaults
9.	Reload data from page 0
10.	Refresh table display
Validation Steps:
java
- VAT Category selected?
- Calendar Type selected?
- Sale Type selected?
- Unit Measure selected?
- Date selected?
- Description not empty?
- Quantity is numeric?
- Unit Price is numeric?
- Receipt number valid?
- MRC provided if FS receipt?
2. Void Sale (voidSale())
Process Flow:
1.	Check if record selected
2.	Verify record not already voided
3.	Show void dialog with reason input
4.	Validate reason not empty
5.	Call con.voidVatSale(receipt, user, reason)
6.	Update model properties:
o	setVoided(true)
o	setVoidedBy(currentUser)
o	setVoidedAt(now)
o	setVoidReason(reason)
7.	Refresh table
8.	Update totals
Void Dialog:
text
╔══════════════════════════════════════════════╗
║ Void Sales Record                            ║
║══════════════════════════════════════════════║
║ Are you sure you want to void this record?   ║
║ Receipt: FS12345678                          ║
║ Amount: 1,150.00                             ║
║                                              ║
║ Void Reason: [_____________________________] ║
║             [_____________________________]   ║
║                                              ║
║                    [Void] [Cancel]           ║
╚══════════════════════════════════════════════╝
3. Calculate (calculate())
Formula:
java
total = quantity × unitPrice
vat = (saleType == 1) ? total × 0.15 : 0
afterVat = total + vat
Triggers:
•	Quantity field change
•	Unit Price field change
•	Sale Type change
•	Form population
4. Load Data (loadPage(int pageIndex))
Process:
1.	Calculate offset: pageIndex × ROWS_PER_PAGE
2.	Call con.fetchAllVatSales(offset, ROWS_PER_PAGE)
3.	Update masterList
4.	Create active filter: !sale.isVoided()
5.	Apply current view filter
6.	Set table items
7.	Update totals
8.	Refresh table
________________________________________
VALIDATION RULES
Regular Expressions
java
// TIN: Exactly 10 digits
Pattern TIN_PATTERN = Pattern.compile("^\\d{10}$");

// Machine Receipt: FS + 8 digits
Pattern RECEIPT_MACHINE_PATTERN = Pattern.compile("^FS\\d{8}$");

// Manual Receipt: M + any digits
Pattern RECEIPT_MANUAL_PATTERN = Pattern.compile("^M\\d+$");
Mandatory Fields Check
Field	Condition	Error Message
VAT Category	Not null/empty	VAT Category is required
Calendar Type	Not null/empty	Calendar Type is required
Sale Type	Not null	Sale Type is required
Unit Measure	Not null	Unit Measure is required
Date	Not null	Date is required
Description	Not empty	Description is required
Quantity	Numeric, not empty	Valid quantity is required
Unit Price	Numeric, not empty	Valid unit price is required
Receipt Number	Valid format	Valid receipt number required
MRC	If receipt starts with FS	MRC Number is REQUIRED for machine receipts
Field-Specific Validation
TIN Validation
•	Empty: Allowed (optional)
•	Non-empty: Must be exactly 10 digits
•	Visual: Red border on error, Green on valid
Receipt Validation
•	Machine Receipt (FS): Must be exactly FS + 8 digits
•	Manual Receipt (M): Must be M followed by any digits
•	MRC Requirement:
o	FS: MRC mandatory (yellow highlight)
o	M: MRC optional (normal highlight)
Numeric Fields
•	Quantity: Accepts integers and decimals
•	Unit Price: Accepts integers and decimals
•	No commas or special characters allowed
________________________________________
BUSINESS LOGIC
VAT Calculation Rules
Sale Type	Code	VAT Rate	Description
Taxable	1	15%	Standard VAT rate
Zero Rated	2	0%	Export/International
Exempt	3	0%	VAT-exempt items
Receipt Number Rules
Machine Receipt (FS...):
•	Format: FS + exactly 8 digits
•	Example: FS12345678
•	MRC Number: MANDATORY
•	Used for: Electronic/fiscal receipts
Manual Receipt (M...):
•	Format: M + any digits
•	Example: M1234, M99999
•	MRC Number: OPTIONAL
•	Used for: Handwritten/manual receipts
Unit of Measure Logic
Code	Display	Use Case
2	KG	Weight-based items
3	ML	Liquids in small quantities
4	GM	Small weight items
5	LIT	Liquids in volume
6	MT	Heavy items
7	PCS	Countable items (Default)
8	CT	Boxed items
9	OTHER	Miscellaneous
10	PC	Individual pieces
Voiding Business Rules
1.	Only active records can be voided
2.	Void reason is mandatory for audit trail
3.	Voided records are NOT deleted - remain in database
4.	Voided records appear in RED in the table
5.	Voided records EXCLUDED from:
o	Financial calculations
o	Grand totals
o	Reports and exports
o	Printing
6.	Voiding is logged with user and timestamp
7.	Unvoiding is disabled for compliance reasons
________________________________________
DATABASE OPERATIONS
Connecting Class Methods
saveVatSales(VatSaleModel sale)
•	Purpose: Insert new VAT sale record
•	Parameters: Complete VatSaleModel object
•	Returns: void (throws exception on error)
fetchAllVatSales(int offset, int limit)
•	Purpose: Retrieve paginated VAT sales
•	Parameters:
o	offset: Starting row
o	limit: Number of rows
•	Returns: List<VatSaleModel>
voidVatSale(String receiptNumber, String user, String reason)
•	Purpose: Mark record as voided
•	Updates:
o	voided = true
o	voided_by = user
o	voided_at = NOW()
o	void_reason = reason
unvoidVatSale(String receiptNumber)
•	Purpose: Restore voided record
•	Updates: Reset all void fields to NULL
Database Schema (Assumed)
sql
CREATE TABLE vat_sales (
    id INT PRIMARY KEY AUTO_INCREMENT,
    vat_category CHAR(1),
    calendar_type CHAR(1),
    sale_type INT,
    buyer_tin VARCHAR(10),
    buyer_name VARCHAR(255),
    date_of_sale DATE,
    receipt_number VARCHAR(20) UNIQUE,
    mrc_number VARCHAR(50),
    description TEXT,
    unit_measure INT,
    quantity DECIMAL(10,2),
    unit_price DECIMAL(10,2),
    total_value DECIMAL(10,2),
    vat_amount DECIMAL(10,2),
    total_after_vat DECIMAL(10,2),
    voided BOOLEAN DEFAULT FALSE,
    voided_by VARCHAR(50),
    voided_at DATETIME,
    void_reason TEXT,
    created_by VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
________________________________________
REPORTING & EXPORT
CSV Export
Format:
csv
Status,VAT Category,Calendar Type,Sale Type,Buyer TIN,Buyer Name,Date,Receipt Number,MRC Number,Description,Unit Measure,Quantity,Unit Price,Total Value,VAT Amount,Total After VAT
ACTIVE,"G","G",1,"1234567890","John Doe","2024-01-15","FS12345678","MRC001","Laptop",7,1.00,1000.00,1000.00,150.00,1150.00
Features:
•	Excludes voided records
•	Includes MRC Number field
•	Comma-separated with quotes
•	Opens automatically after save
PDF Export
Structure:
text
┌─────────────────────────────────────────────────────┐
│          SMART HRMS - VAT ACTIVE SALES REPORT        │
│              FILTERED VAT ACTIVE SALES REPORT         │
│         Date Range: 2024-01-01 to 2024-01-31         │
│         Voided Records Excluded                       │
│                                                       │
│ Generated on: 2024-01-31 | Generated by: admin        │
│ Total Active Records: 50                               │
│═══════════════════════════════════════════════════════│
│ Status │ VAT │ Cal │ Type │ TIN │ ... │ After VAT    │
│═══════════════════════════════════════════════════════│
│ ACTIVE │ G   │ G   │ 1    │ ... │ 1,150.00         │
│ ...                                                  │
│═══════════════════════════════════════════════════════│
│                                                       │
│          Summary                                      │
│ Total Active Records: 50                              │
│ Total VAT Collected: 7,500.00                         │
│ Total Sales Amount: 50,000.00                         │
│                                                       │
│ This report excludes voided records...                │
└─────────────────────────────────────────────────────┘
Features:
•	Professional formatting
•	Color-coded sale types
•	Automatic pagination for large datasets
•	Summary totals
•	Header on each page
•	Voided records disclaimer
Printing
Print Layout:
•	Title: VAT ACTIVE SALES REPORT
•	Disclaimer: VOIDED RECORDS ARE EXCLUDED
•	Metadata: Generation date and user
•	Summary: Record count and totals
•	Table: Formatted with all columns
•	Grand Total: Highlighted footer
________________________________________
VOIDING SYSTEM
Visual Indicators
Status	Text Color	Background (Even)	Background (Odd)
Active (Taxable)	Black	#e8f6f3	#d1f2eb
Active (Zero)	Black	#fef9e7	#fcf3cf
Active (Exempt)	Black	#fdedec	#fadbd8
Voided	Red (#e74c3c)	#ffebee	#ffcdd2
Audit Trail Fields
java
// For voided records
sale.isVoided()        // boolean: true/false
sale.getVoidedBy()     // String: username
sale.getVoidedAt()     // String: timestamp
sale.getVoidReason()   // String: reason provided

// For created records
sale.getCreatedBy()    // String: username
Voiding Flow Diagram
text
[User selects record] → [Check if active] → [Show void dialog]
                       ↓
                  [Enter reason]
                       ↓
        [Reason not empty?] → No → [Disable Void button]
                       ↓ Yes
           [Call voidVatSale()]
                       ↓
    [Update model: voided=true, voidedBy, voidedAt, reason]
                       ↓
          [Refresh table with red styling]
                       ↓
       [Update totals (exclude voided)]
                       ↓
         [Show success message]
Unvoiding Restrictions
Current Behavior:
•	Unvoid button shows informational dialog
•	Unvoid functionality is disabled
•	Reason: Audit compliance
Dialog Message:
text
╔════════════════════════════════════════════════════╗
║ Unvoid Function Disabled                            ║
║════════════════════════════════════════════════════║
║ Unvoid functionality is currently disabled for VAT  ║
║ sales records.                                       ║
║                                                     ║
║ For audit and compliance reasons:                    ║
║ • Once a VAT sale is voided, it cannot be restored   ║
║ • Voided records remain in the system for audit      ║
║ • If you need to correct a voided record, create new ║
║ • Contact system administrator for exceptional cases ║
║                                                     ║
║                                   [OK]               ║
╚════════════════════════════════════════════════════╝
________________________________________
FILTERING & SEARCH
View Filter Logic
java
switch(filter) {
    case "All Records":
        table.setItems(masterList);  // Show all, including voided
        break;
    case "Active Only":
        // Filter out voided records
        FilteredList<VatSaleModel> activeFiltered = 
            masterList.filtered(sale -> !sale.isVoided());
        activeList.setAll(activeFiltered);
        table.setItems(activeList);
        break;
    case "Voided Only":
        // Show only voided records
        FilteredList<VatSaleModel> voidedFiltered = 
            masterList.filtered(VatSaleModel::isVoided);
        table.setItems(voidedFiltered);
        break;
}
Date Range Filter
Process:
1.	Get fromDate and toDate from pickers
2.	Validate: fromDate ≤ toDate
3.	Filter masterList by date range
4.	Store in filteredList
5.	Set table items to filteredList
6.	Update totals
7.	Show summary with:
o	Record count
o	Total sales
o	VAT collected
Search Filter
Search Fields:
•	Receipt Number
•	Buyer Name
•	Description
•	Buyer TIN
Implementation:
java
filtered.setPredicate(sale ->
    sale.getReceiptNumber().toLowerCase().contains(searchTerm) ||
    sale.getBuyerName().toLowerCase().contains(searchTerm) ||
    sale.getDescription().toLowerCase().contains(searchTerm) ||
    sale.getBuyerTin().toLowerCase().contains(searchTerm)
);
________________________________________
USER GUIDE
Getting Started
1. Initial Screen
When you open the VAT Sales Details module, you'll see:
•	Documentation panel (collapsible)
•	Data entry form
•	Button toolbar
•	Search bar
•	Date range filter
•	Data table with active records
•	Totals footer
2. Setting Default Values
The system automatically sets:
•	VAT Category: G (Goods)
•	Calendar Type: G (Gregorian)
•	Sale Type: 1 (Taxable)
•	Unit Measure: 7 (PCS)
•	Date: Today
•	Date Range: Last 30 days
Entering a New Sale
Step-by-Step Process
1.	Select VAT Category
o	G = Goods (physical products)
o	S = Services (labor, consulting)
2.	Select Calendar Type
o	G = Gregorian (standard)
o	E = Ethiopian (optional)
3.	Select Sale Type
o	1 = Taxable (15% VAT)
o	2 = Zero Rated (0% VAT)
o	3 = Exempt (0% VAT)
4.	Select Unit Measure
o	Choose appropriate unit (default: PCS)
5.	Enter Buyer Information (Optional)
o	TIN: Exactly 10 digits if provided
o	Name: Optional
6.	Enter Receipt Number
o	Machine: FS12345678 (8 digits)
o	Manual: M1234 (any digits)
7.	Enter MRC Number
o	Required for FS receipts (yellow field)
o	Optional for M receipts
8.	Enter Date
o	Default: Today
o	Can be changed
9.	Enter Description
o	Item or service description
10.	Enter Quantity & Price
o	Numbers only (no commas)
o	Decimals allowed
o	Totals calculate automatically
11.	Review Calculated Values
o	Total Value: qty × price
o	VAT: 15% if taxable
o	After VAT: total + VAT
12.	Click Save (💾)
Voiding a Record
1.	Select the record in the table
2.	Click Void Record (🚫)
3.	Enter void reason (required)
4.	Click Void to confirm
5.	Record turns red and excluded from totals
Filtering Records
By Status
•	Use View dropdown
•	Choose: All, Active Only, Voided Only
By Date Range
1.	Set From Date and To Date
2.	Click Apply Filter
3.	Table shows only records in range
4.	Click Clear Filter to reset
By Search
1.	Type in search box
2.	Real-time filtering on:
o	Receipt number
o	Buyer name
o	Description
o	TIN
Exporting Data
CSV Export
•	Click Export CSV (📊)
•	Choose filename and location
•	Opens automatically in Excel
PDF Export
•	Click Export PDF (📄)
•	Professional formatted report
•	Includes summary totals
Filtered Export
1.	Apply date range filter
2.	Click Export Filtered CSV/PDF
3.	Only filtered records exported
Printing
1.	Click Print (🖨️)
2.	Preview dialog appears
3.	Select printer
4.	Click Print
________________________________________
TROUBLESHOOTING
Common Issues and Solutions
Issue: "Error loading data"
Cause: Database connection problem
Solution:
•	Check database server is running
•	Verify connection settings in Connecting class
•	Check network connectivity
Issue: Validation errors persist
Cause: Missing or invalid field values
Solution:
•	Check all red-bordered fields
•	Ensure receipt format is correct
•	Verify quantity/price are numbers
Issue: MRC field not saving
Cause: Field not included in save operation
Solution:
•	Ensure mrcNumber is set in model
•	Check database column exists
Issue: Voided records still show in totals
Cause: Filter not applied correctly
Solution:
•	Use "Active Only" view
•	Verify isVoided() check in calculations
Error Messages Guide
Message	Meaning	Action
"TIN must be exactly 10 digits"	Invalid TIN format	Enter 10 digits only
"Receipt must be FS+8digits or M+digits"	Wrong receipt format	Check format guide
"MRC Number is REQUIRED for machine receipts"	Missing MRC for FS receipt	Enter MRC number
"Valid quantity is required"	Quantity empty or invalid	Enter numeric value
"This record is already voided"	Cannot void again	Record already voided
________________________________________
CODE REFERENCE
Key Method Signatures
Initialization
java
public VatSaleDetailsFX(String username)
private void buildUI()
private GridPane createForm()
private HBox createButtons()
private void buildTable()
Data Operations
java
private void saveSale()
private void loadPage(int pageIndex)
private void refreshData()
private void populateForm(VatSaleModel s)
private void clearForm()
private void setDefaults()
Voiding
java
private void voidSale()
private void unvoidSale()
Validation
java
private boolean validateTin()
private boolean validateReceipt()
private boolean validateMandatoryFields()
private boolean isNumeric(String str)
Calculations
java
private void calculate()
private void updateTotals()
Filtering
java
private void filterData()
private void applyDateRangeFilter()
private void clearDateRangeFilter()
private void applyViewFilter()
private void toggleVoidedVisibility()
Export/Print
java
private void exportCSV()
private void exportFilteredCSV()
private void exportPDF()
private void exportFilteredPDF()
private void exportDataToPDF(File file, ObservableList<VatSaleModel> data, String title, String subtitle)
private void printTable()
private void printFilteredData()
private Node createPrintableNode(ObservableList<VatSaleModel> dataToPrint, String title)
Important Constants
java
private static final int ROWS_PER_PAGE = 20;
private static final Pattern TIN_PATTERN = Pattern.compile("^\\d{10}$");
private static final Pattern RECEIPT_MACHINE_PATTERN = Pattern.compile("^FS\\d{8}$");
private static final Pattern RECEIPT_MANUAL_PATTERN = Pattern.compile("^M\\d+$");
Color Coding Reference
java
// Buttons
Green: "#27ae60"    // Save
Red: "#e74c3c"      // Void
Orange: "#f39c12"   // Unvoid
Gray: "#95a5a6"     // Clear
Blue: "#3498db"     // Export CSV
Purple: "#9b59b6"   // Export PDF
Dark Blue: "#34495e" // Print

// Text
Dark Gray: "#2c3e50" // Title
Red: "#e74c3c"       // Errors, Voided
Green: "#27ae60"     // Active totals
Orange: "#f39c12"    // Zero rated
Purple: "#9b59b6"    // PDF button

// Backgrounds
Light Green: "#e8f6f3" // Taxable (even)
Green: "#d1f2eb"       // Taxable (odd)
Light Yellow: "#fef9e7" // Zero rated (even)
Yellow: "#fcf3cf"      // Zero rated (odd)
Light Pink: "#fdedec"   // Exempt (even)
Pink: "#fadbd8"        // Exempt (odd)
Light Red: "#ffebee"    // Voided (even)
Red: "#ffcdd2"         // Voided (odd)
________________________________________
APPENDIX
Version History
Version	Date	Changes
1.0.0	Initial	Base functionality
1.1.0	Update	Added MRC field
1.2.0	Update	Enhanced voiding system
1.3.0	Current	Full documentation
Future Enhancements
1.	Batch Operations: Bulk void/export
2.	Advanced Analytics: Charts and trends
3.	User Permissions: Role-based access
4.	Audit Log Viewer: Interface for void history
5.	Email Reports: Scheduled report delivery
6.	API Integration: External system connectivity
Support Contact
For technical support or questions:
•	System Administrator: [admin email]
•	Developer: [developer contact]
•	Documentation: [documentation location]


