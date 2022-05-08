import struct
import pyodbc
from msal import ConfidentialClientApplication

TENANT_ID = 'provide your AAD tenant id'
SECRET = "secret of your SPN"
CLIENT_ID = "client id of your SPN"
server = 'Azure SQL server'
database = 'Database name'
#version might vary
driver = 'ODBC Driver 18 for SQL Server'


def acquire_token(tenant_id: str, client_id: str, client_credential: str, *scopes: str) -> str:

    app = ConfidentialClientApplication(
        client_id, authority=f"https://login.microsoftonline.com/{tenant_id}",
        client_credential=client_credential
    )
    result = app.acquire_token_silent(scopes=list(scopes), account=None)
    if not result:
        result = app.acquire_token_for_client(scopes=list(scopes))
    return result['access_token']


token = acquire_token(TENANT_ID, CLIENT_ID,
                      SECRET, "https://database.windows.net/.default")

token_bytes = token.encode('utf-16-le')
token_struct = struct.pack(f'<I{len(token_bytes)}s', len(token_bytes), token_bytes)

conn_str_oauth = f'Driver={{{driver}}};Server=tcp:{server};Database={database}'
conn_str_auth = f'Driver={{{driver}}};Server=tcp:{server};Database={database};authentication' \
                f'=ActiveDirectoryServicePrincipal;UID={CLIENT_ID};PWD={SECRET} '

conn = pyodbc.connect(conn_str_oauth, attrs_before={1256: token_struct})
print(conn)
conn = pyodbc.connect(conn_str_auth)
print(conn)
