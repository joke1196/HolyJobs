# HolyJobs
1. If you want to try the application in local and send emails, you must have a configured SMTP server (for example with Postfix).  
   You can then configure the sending parameters in the "conf/application.conf" file. They are already set for a default localhost server.
2. Execute the "TableCreation.sql" script on your database, then change the DB's connection parameters in the "conf/application.conf" file.
