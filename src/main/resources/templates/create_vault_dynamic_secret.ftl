apiVersion: secrets.hashicorp.com/v1beta1
kind: VaultDynamicSecret
metadata:
  name: ${name}-dynamic-secret
  namespace: vso-dynamic
spec:
  mount: database-secret
  path: creds/${name}-role
  destination:
    create: false
    name: ${name}-dynamic-secret
  rolloutRestartTargets:
  - kind: Deployment
    name: ${name}-dynamic-secret
  vaultAuthRef: dynamic-vault-auth