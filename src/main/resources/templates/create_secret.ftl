apiVersion: v1
kind: Secret
type: kubernetes.io/service-account-token
metadata:
  name: ${tokenName}
  namespace: ${spaceName}
  annotations:
    kubernetes.io/service-account.name: ${userName}