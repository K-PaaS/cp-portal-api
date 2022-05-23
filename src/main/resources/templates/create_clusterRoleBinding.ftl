apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: cluster-admin-${userName}
  namespace: ${spaceName}
subjects:
- kind: ServiceAccount
  name: ${userName}
  namespace: ${spaceName}
roleRef:
  kind: ClusterRole
  name: cluster-admin
  apiGroup: rbac.authorization.k8s.io
