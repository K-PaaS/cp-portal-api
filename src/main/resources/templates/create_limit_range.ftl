apiVersion: v1
kind: LimitRange
metadata:
  name: ${name}
  namespace: ${namespace}
spec:
  limits:
  - default:
      cpu: ${limit_cpu}
      memory: ${limit_memory}
    type: Container
