{
    "connection_url": "postgresql://{{username}}:{{password}}@${serviceName}.${namespace}.svc.cluster.local:5432/postgres?sslmode=disable",
    "username": "postgres",
    "password": "postgres-pass",
    "plugin_name": "postgresql-database-plugin",
    "allowed_roles": [
        "${name}-role"
    ]
}