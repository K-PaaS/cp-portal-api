{
    "audience": "vault",
    "bound_service_account_names": [
        "${name}"
    ],
    "bound_service_account_namespaces": [
        "${namespace}"
    ],
    "ttl": 24h,
    "policies": [
        "${name}-policy"
    ]
}