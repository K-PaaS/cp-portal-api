apiVersion: v1
kind: ServiceAccount
metadata:
  name: ${app_name}
  namespace: ${namespace}
automountServiceAccountToken: true
