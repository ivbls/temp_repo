# Basic java server

The program solution was made in Java and contains two maven projects that serve as a server and client. Those are the two directories uploaded in this repository.
Maven was used for dependency management in both projects.
The database used for this demonstation is postgresql. The database contains only one table that contains these columns:
id - integer [PRIMARY_KEY], transaction_type - varchar(255), transaction_id - varchar(255), amount - numeric(12, 2), currency - varchar(3)
Some manual setting up is necessary in the server project to make it interact with the database correctly, where the variables db_name, db_socket, db_uname and db_pass need to be set to values that will allow it to connect to a locally run postgres server.
