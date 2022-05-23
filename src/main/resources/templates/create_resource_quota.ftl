apiVersion: v1
kind: ResourceQuota
metadata:
  name: ${name}
  namespace: ${namespace}
spec:
  hard:
    limits.cpu: ${limits_cpu}
    limits.memory: ${limits_memory}