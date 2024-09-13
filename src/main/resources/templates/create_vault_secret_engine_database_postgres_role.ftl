{
    "creation_statements": [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}';       GRANT ALL PRIVILEGES ON DATABASE postgres TO \"{{name}}\";"
    ],
    "backend": "${name}-db",
    "db_name": "${name}-db",
    "name": "${name}-role",
    "default_ttl": "${defaultTtl}",
    "max_ttl": "${maxTtl}",
    "revocation_statements": [
    "REVOKE ALL ON DATABASE postgres FROM  \"{{name}}\";"
    ]
}