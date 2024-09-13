{
    "name": "${name}-policy",
    "policy": "path \"database-secret/creds/${name}-role\" {\n   capabilities = [\"read\"]\n}\n"
}