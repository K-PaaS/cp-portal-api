apiVersion: secrets.hashicorp.com/v1beta1
kind: VaultDynamicSecret
metadata:
  name: ${db_name}-vault-dynamic-secret
  namespace: ${namespace}
spec:
  mount: database-secret
  path: creds/${db_name}-role
  destination:
    create: true
    name: ${db_name}-dynamic-secret
  rolloutRestartTargets:
  - kind: Deployment
    name: ${db_name}-dynamic-secret
  vaultAuthRef: ${db_name}-dynamic-vault-auth