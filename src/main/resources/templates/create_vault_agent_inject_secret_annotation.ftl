      annotations:
        vault.hashicorp.com/agent-inject: "true"
        vault.hashicorp.com/agent-inject-secret-database-secret-creds: "database-secret/creds/${db_name}-role"
        vault.hashicorp.com/role: "${app_name}-role"