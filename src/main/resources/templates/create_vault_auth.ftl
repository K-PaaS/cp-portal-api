apiVersion: secrets.hashicorp.com/v1beta1
kind: VaultAuth
metadata:
  name: ${db_name}-dynamic-vault-auth
  namespace: ${namespace}
spec:
  method: kubernetes
  mount: k8s-auth-mount
  kubernetes:
    role: k8s-auth-${db_name}-role
    serviceAccount: ${db_name}-dynamic-service-account
    audiences:
      - vault