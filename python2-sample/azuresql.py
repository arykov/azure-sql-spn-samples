import struct
import pyodbc

TENANT_ID = 'provide your AAD tenant id'
SECRET = "secret of your SPN"
CLIENT_ID = "client id of your SPN"
server = 'Azure SQL server'
database = 'Database name'
#version might vary
driver = 'ODBC Driver 18 for SQL Server'


conn_str_oauth = 'Driver={' + driver + '};Server=tcp:' + server + ';Database=' + database
conn_str_auth = 'Driver={' + driver + '};Server=tcp:' + server + ';Database=' + database + ';authentication' \
                '=ActiveDirectoryServicePrincipal;UID=' \
                + CLIENT_ID + ';PWD=' + SECRET

conn = pyodbc.connect(conn_str_auth)
print conn
